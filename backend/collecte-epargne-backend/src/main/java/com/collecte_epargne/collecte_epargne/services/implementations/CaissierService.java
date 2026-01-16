package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.CaissierDashboardDTO;
import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.utils.StatusValidation;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * COMMENT CLEF: Service Caissier - Validation transactions + Dashboard agence
 * Responsabilité: Valider/Rejeter transactions, voir clients/collecteurs agence
 */
@Service
@Transactional
public class CaissierService {
    
    private static final Logger log = LoggerFactory.getLogger(CaissierService.class);
    private static final BigDecimal COMMISSION_CAISSIER = new BigDecimal("0.02"); // 2%
    
    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;
    private final AgenceZoneRepository agenceZoneRepository;
    private final GainsService gainsService;

    public CaissierService(TransactionRepository transactionRepository,
                           CompteRepository compteRepository,
                           EmployeRepository employeRepository,
                           ClientRepository clientRepository,
                           AgenceZoneRepository agenceZoneRepository,
                           GainsService gainsService) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
        this.agenceZoneRepository = agenceZoneRepository;
        this.gainsService = gainsService;
    }

    /**
     * COMMENT CLEF: Récupère transactions EN_ATTENTE pour validation (filtre agence caissier)
     */
    public List<TransactionDto> obtenirTransactionsEnAttente(Integer idCaissier) {
        log.info("Récupération transactions en attente pour caissier {}", idCaissier);
        
        Employe caissier = employeRepository.findById(idCaissier)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé: " + idCaissier));
        
        if (!caissier.getTypeEmploye().equals(TypeEmploye.CAISSIER)) {
            throw new RuntimeException("L'employé n'est pas un caissier");
        }
        
        AgenceZone agence = caissier.getAgenceZone();
        if (agence == null) {
            throw new RuntimeException("Caissier non assigné à une agence");
        }
        
        // COMMENT CLEF: Query transactions EN_ATTENTE par agence
        List<Transaction> transactions = transactionRepository
            .findByStatusValidationAndCaissierValidateur_AgenceZone_IdOrderByDateTransactionDesc(
                StatusValidation.EN_ATTENTE,
                agence.getIdAgence()
            );
        
        log.info("Trouvé {} transactions en attente", transactions.size());
        return transactions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    /**
     * COMMENT CLEF: Valide transaction = crédite compte + enregistre gains caissier
     */
    public TransactionDto validerTransaction(String idTransaction, Integer idCaissier) {
        log.info("Validation transaction {} par caissier {}", idTransaction, idCaissier);
        
        Transaction transaction = transactionRepository.findById(idTransaction)
            .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + idTransaction));
        
        Employe caissier = employeRepository.findById(idCaissier)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé: " + idCaissier));
        
        // COMMENT CLEF: Vérifications sécurité
        if (!transaction.getStatusValidation().equals(StatusValidation.EN_ATTENTE)) {
            throw new RuntimeException("Transaction n'est pas en attente: " + transaction.getStatusValidation());
        }
        
        if (!caissier.getAgenceZone().equals(transaction.getCompte().getClient().getCollecteurAssigne().getAgenceZone())) {
            throw new RuntimeException("Transaction ne concerne pas l'agence du caissier");
        }
        
        // COMMENT CLEF: Crédite le compte client
        Compte compte = transaction.getCompte();
        BigDecimal nouveauSolde = compte.getSolde().add(transaction.getMontant());
        compte.setSolde(nouveauSolde);
        compteRepository.save(compte);
        
        // COMMENT CLEF: Enregistre transaction comme VALIDEE
        transaction.setStatusValidation(StatusValidation.VALIDEE);
        transaction.setCaissierValidateur(caissier);
        transaction.setDateValidationCaisse(Instant.now());
        Transaction saved = transactionRepository.save(transaction);
        
        // COMMENT CLEF: Enregistre gains caissier (2% du montant)
        BigDecimal gainsCaissier = transaction.getMontant().multiply(COMMISSION_CAISSIER);
        gainsService.enregistrerGains(caissier.getIdEmploye(), gainsCaissier, "CAISSIER", 
            transaction.getIdTransaction(), caissier.getAgenceZone().getIdAgence());
        
        log.info("Transaction {} validée - Compte crédité de {}, Caissier gagne {}", 
            idTransaction, transaction.getMontant(), gainsCaissier);
        
        return convertToDto(saved);
    }

    /**
     * COMMENT CLEF: Rejette transaction + enregistre motif
     */
    public TransactionDto rejeterTransaction(String idTransaction, String motifRejet, Integer idCaissier) {
        log.info("Rejet transaction {} - Motif: {}", idTransaction, motifRejet);
        
        Transaction transaction = transactionRepository.findById(idTransaction)
            .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));
        
        Employe caissier = employeRepository.findById(idCaissier)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));
        
        // COMMENT CLEF: Vérifications
        if (!transaction.getStatusValidation().equals(StatusValidation.EN_ATTENTE)) {
            throw new RuntimeException("Transaction n'est pas en attente");
        }
        
        transaction.setStatusValidation(StatusValidation.REJETEE);
        transaction.setMotifRejet(motifRejet);
        transaction.setCaissierValidateur(caissier);
        transaction.setDateValidationCaisse(Instant.now());
        
        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction {} rejetée", idTransaction);
        
        return convertToDto(saved);
    }

    /**
     * COMMENT CLEF: Dashboard Caissier - KPIs agence
     */
    public CaissierDashboardDTO obtenirDashboard(Integer idCaissier) {
        log.info("Génération dashboard caissier {}", idCaissier);
        
        Employe caissier = employeRepository.findById(idCaissier)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));
        
        AgenceZone agence = caissier.getAgenceZone();
        
        CaissierDashboardDTO dashboard = new CaissierDashboardDTO();
        dashboard.setIdCaissier(idCaissier);
        dashboard.setNomCaissier(caissier.getUtilisateur().getNom());
        dashboard.setAgenceNom(agence.getNom());
        
        // COMMENT CLEF: Transactions en attente
        long enAttente = transactionRepository
            .findByStatusValidationAndCaissierValidateur_AgenceZone_Id(
                StatusValidation.EN_ATTENTE,
                agence.getIdAgence()
            ).size();
        dashboard.setTransactionsEnAttente(enAttente);
        
        // COMMENT CLEF: Transactions validées aujourd'hui
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        List<Transaction> valideeAujourdhui = transactionRepository
            .findByStatusValidationAndDateValidationCaisseBetweenAndCaissierValidateur_AgenceZone_Id(
                StatusValidation.VALIDEE,
                startOfDay,
                endOfDay,
                agence.getIdAgence()
            );
        
        dashboard.setTransactionsValideeAujourdhui((long) valideeAujourdhui.size());
        dashboard.setMontantValideAujourd(
            valideeAujourdhui.stream()
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        
        // COMMENT CLEF: Stats agence
        dashboard.setClientsTotal((long) clientRepository.findByCollecteurAssigne_AgenceZone(agence).size());
        dashboard.setCollecteursTotal((long) employeRepository
            .findByAgenceZoneAndTypeEmploye(agence, TypeEmploye.COLLECTEUR).size());
        
        return dashboard;
    }

    /**
     * COMMENT CLEF: Voir caissiers disponibles d'une agence (pour collecteur choisir)
     */
    public List<EmployeDto> obtenirCaissiersDisponibles(Integer idAgence) {
        AgenceZone agence = agenceZoneRepository.findById(idAgence)
            .orElseThrow(() -> new RuntimeException("Agence non trouvée"));
        
        return employeRepository.findByAgenceZoneAndTypeEmploye(agence, TypeEmploye.CAISSIER)
            .stream()
            .map(this::convertEmployeToDto)
            .collect(Collectors.toList());
    }

    /**
     * COMMENT CLEF: Voir clients assignés à collecteurs de l'agence
     */
    public List<ClientDto> obtenirClientsAgence(Integer idAgence) {
        AgenceZone agence = agenceZoneRepository.findById(idAgence)
            .orElseThrow(() -> new RuntimeException("Agence non trouvée"));
        
        return clientRepository.findByCollecteurAssigne_AgenceZone(agence)
            .stream()
            .map(this::convertClientToDto)
            .collect(Collectors.toList());
    }

    /**
     * COMMENT CLEF: Voir collecteurs de l'agence
     */
    public List<EmployeDto> obtenirCollecteursAgence(Integer idAgence) {
        AgenceZone agence = agenceZoneRepository.findById(idAgence)
            .orElseThrow(() -> new RuntimeException("Agence non trouvée"));
        
        return employeRepository.findByAgenceZoneAndTypeEmploye(agence, TypeEmploye.COLLECTEUR)
            .stream()
            .map(this::convertEmployeToDto)
            .collect(Collectors.toList());
    }

    // ========== HELPERS ==========
    
    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setIdTransaction(transaction.getIdTransaction());
        dto.setMontant(transaction.getMontant());
        dto.setDateTransaction(transaction.getDateTransaction());
        // Ajouter autres mappings selon TransactionMapper existant
        return dto;
    }
    
    private EmployeDto convertEmployeToDto(Employe employe) {
        EmployeDto dto = new EmployeDto();
        dto.setIdEmploye(employe.getIdEmploye());
        dto.setMatricule(employe.getMatricule());
        dto.setTypeEmploye(employe.getTypeEmploye());
        // Ajouter autres mappings
        return dto;
    }
    
    private ClientDto convertClientToDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setCodeClient(client.getCodeClient());
        dto.setNumeroClient(client.getNumeroClient());
        // Ajouter autres mappings
        return dto;
    }
}
