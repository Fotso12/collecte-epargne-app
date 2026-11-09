package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.*;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.utils.StatusApprobation;
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
 * COMMENT CLEF: Service Superviseur - Approbation comptes + KPIs + Collection
 * Responsabilité: Approuver/Rejeter comptes clients, voir KPIs agence, historique collection
 */
@Service
@Transactional
public class SuperviseurService {
    
    private static final Logger log = LoggerFactory.getLogger(SuperviseurService.class);
    private static final BigDecimal COMMISSION_SUPERVISEUR = new BigDecimal("0.01"); // 1%
    
    private final CompteRepository compteRepository;
    private final EmployeRepository employeRepository;
    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final AgenceZoneRepository agenceZoneRepository;
    private final GainsService gainsService;

    public SuperviseurService(CompteRepository compteRepository,
                              EmployeRepository employeRepository,
                              TransactionRepository transactionRepository,
                              ClientRepository clientRepository,
                              AgenceZoneRepository agenceZoneRepository,
                              GainsService gainsService) {
        this.compteRepository = compteRepository;
        this.employeRepository = employeRepository;
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
        this.agenceZoneRepository = agenceZoneRepository;
        this.gainsService = gainsService;
    }

    /**
     * COMMENT CLEF: Récupère comptes EN_ATTENTE d'approbation (filtre agence superviseur)
     */
    public List<CompteDto> obtenirComptesEnAttenteApprobation(Integer idSuperviseur) {
        log.info("Récupération comptes en attente approbation pour superviseur {}", idSuperviseur);
        
        Employe superviseur = employeRepository.findById(idSuperviseur)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé: " + idSuperviseur));
        
        if (!superviseur.getTypeEmploye().equals(TypeEmploye.SUPERVISEUR)) {
            throw new RuntimeException("L'employé n'est pas un superviseur");
        }
        
        AgenceZone agence = superviseur.getAgenceZone();
        if (agence == null) {
            throw new RuntimeException("Superviseur non assigné à une agence");
        }
        
        // COMMENT CLEF: Query comptes EN_ATTENTE par agence
        List<Compte> comptes = compteRepository.findPendingApprovalsByAgence(StatusApprobation.EN_ATTENTE, agence);
        
        log.info("Trouvé {} comptes en attente approbation", comptes.size());
        return comptes.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    /**
     * COMMENT CLEF: Approuve compte = client peut faire transactions
     */
    public CompteDto approuverCompte(String idCompte, Integer idSuperviseur) {
        log.info("Approbation compte {} par superviseur {}", idCompte, idSuperviseur);
        
        Compte compte = compteRepository.findById(idCompte)
            .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + idCompte));
        
        Employe superviseur = employeRepository.findById(idSuperviseur)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé: " + idSuperviseur));
        
        // COMMENT CLEF: Vérifications sécurité
        if (!compte.getStatusApprobation().equals(StatusApprobation.EN_ATTENTE)) {
            throw new RuntimeException("Compte n'est pas en attente: " + compte.getStatusApprobation());
        }
        
        // COMMENT CLEF: Marque compte comme APPROUVE
        compte.setStatusApprobation(StatusApprobation.APPROUVE);
        compte.setDateApprobation(Instant.now());
        compte.setSuperviseurApprobation(superviseur);
        
        Compte saved = compteRepository.save(compte);
        log.info("Compte {} approuvé - Client peut maintenant transactionner", idCompte);
        
        return convertToDto(saved);
    }

    /**
     * COMMENT CLEF: Rejette compte + enregistre motif
     */
    public CompteDto rejeterCompte(String idCompte, String motifRejet, Integer idSuperviseur) {
        log.info("Rejet compte {} - Motif: {}", idCompte, motifRejet);
        
        Compte compte = compteRepository.findById(idCompte)
            .orElseThrow(() -> new RuntimeException("Compte non trouvé"));
        
        Employe superviseur = employeRepository.findById(idSuperviseur)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));
        
        // COMMENT CLEF: Vérifications
        if (!compte.getStatusApprobation().equals(StatusApprobation.EN_ATTENTE)) {
            throw new RuntimeException("Compte n'est pas en attente");
        }
        
        compte.setStatusApprobation(StatusApprobation.REJETE);
        compte.setMotifRejetApprobation(motifRejet);
        compte.setDateApprobation(Instant.now());
        compte.setSuperviseurApprobation(superviseur);
        
        Compte saved = compteRepository.save(compte);
        log.info("Compte {} rejeté", idCompte);
        
        return convertToDto(saved);
    }

    /**
     * COMMENT CLEF: Dashboard Superviseur - KPIs agence
     */
    public SuperviseurDashboardDTO obtenirDashboard(Integer idSuperviseur) {
        log.info("Génération dashboard superviseur {}", idSuperviseur);
        
        Employe superviseur = employeRepository.findById(idSuperviseur)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));
        
        AgenceZone agence = superviseur.getAgenceZone();
        
        SuperviseurDashboardDTO dashboard = new SuperviseurDashboardDTO();
        dashboard.setIdSuperviseur(idSuperviseur);
        dashboard.setNomSuperviseur(superviseur.getUtilisateur().getNom());
        dashboard.setAgenceNom(agence.getNom());
        
        // COMMENT CLEF: Comptes en attente approbation
        long comptesEnAttente = compteRepository.countPendingApprovalsByAgence(StatusApprobation.EN_ATTENTE, agence);
        dashboard.setComptesEnAttenteApprobation(comptesEnAttente);
        
        // COMMENT CLEF: Collection aujourd'hui
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        List<Transaction> transactionJour = transactionRepository
            .findByInitiateur_AgenceZoneAndDateTransactionBetween(agence, startOfDay, endOfDay);
        
        BigDecimal montantJour = transactionJour.stream()
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        dashboard.setMontantCollecteJour(montantJour);
        
        // COMMENT CLEF: Gains superviseur (1% du montant collecté jour)
        BigDecimal gainsSuperviseur = montantJour.multiply(COMMISSION_SUPERVISEUR);
        dashboard.setGainsJourSuperviseur(gainsSuperviseur);
        
        // COMMENT CLEF: Stats agence
        dashboard.setCollecteursTotal((long) employeRepository
            .findByAgenceZoneAndTypeEmploye(agence, TypeEmploye.COLLECTEUR).size());
        
        // COMMENT CLEF: Meilleur collecteur
        dashboard.setMeilleurCollecteur(obtenirMeilleurCollecteur(agence));
        
        return dashboard;
    }

    /**
     * COMMENT CLEF: Meilleur collecteur par montant collecté jour
     */
    public CollecteurKPIDTO obtenirMeilleurCollecteur(AgenceZone agence) {
        log.info("Calcul meilleur collecteur pour agence {}", agence.getIdAgence());
        
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        List<Transaction> transactions = transactionRepository
            .findByInitiateur_AgenceZoneAndDateTransactionBetween(agence, startOfDay, endOfDay);
        
        return transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getInitiateur(),
                Collectors.mapping(Transaction::getMontant, 
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ))
            .entrySet().stream()
            .map(entry -> {
                CollecteurKPIDTO dto = new CollecteurKPIDTO();
                dto.setIdCollecteur(entry.getKey().getIdEmploye());
                dto.setNomCollecteur(entry.getKey().getUtilisateur().getNom());
                dto.setMontantCollecte(entry.getValue());
                dto.setNombreTransactions((long) transactions.stream()
                    .filter(t -> t.getInitiateur().equals(entry.getKey())).count());
                return dto;
            })
            .max((a, b) -> a.getMontantCollecte().compareTo(b.getMontantCollecte()))
            .orElse(null);
    }

    /**
     * COMMENT CLEF: Historique collection par période (Daily, Weekly, Monthly, Semi-Annual)
     */
    public List<CollectionKPIDTO> obtenirHistoriqueCollection(Integer idAgence, String periode) {
        log.info("Historique collection agence {} - Période: {}", idAgence, periode);
        
        AgenceZone agence = agenceZoneRepository.findById(idAgence)
            .orElseThrow(() -> new RuntimeException("Agence non trouvée"));
        
        // À implémenter selon les besoins (Daily, Weekly, Monthly, Semi-Annual)
        // Pour now, retourne vide
        return List.of();
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
    
    private CompteDto convertToDto(Compte compte) {
        CompteDto dto = new CompteDto();
        dto.setIdCompte(compte.getIdCompte());
        dto.setNumCompte(compte.getNumCompte());
        dto.setSolde(compte.getSolde());
        // Ajouter autres mappings selon CompteMapper existant
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
