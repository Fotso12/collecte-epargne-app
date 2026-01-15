package com.collecte_epargne.collecte_epargne.services.implementations;

import java.util.UUID;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.mappers.TransactionOfflineMapper;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionOfflineInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import com.collecte_epargne.collecte_epargne.utils.CodeGenerator;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionInterface;
import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionOfflineService implements TransactionOfflineInterface {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionOfflineService.class);

    private final TransactionOfflineRepository repository;
    private final TransactionOfflineMapper mapper;
    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionInterface transactionService;
    private final CodeGenerator codeGenerator;

    public TransactionOfflineService(TransactionOfflineRepository repository, TransactionOfflineMapper mapper, EmployeRepository employeRepository, ClientRepository clientRepository, CompteRepository compteRepository, TransactionRepository transactionRepository, TransactionInterface transactionService, CodeGenerator codeGenerator) {
        this.repository = repository;
        this.mapper = mapper;
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.codeGenerator = codeGenerator;
    }

    // ----------------------------------------------------
    // Création transaction offline
    // ----------------------------------------------------
    @Override
    public TransactionOfflineDto save(TransactionOfflineDto dto) {

        if (dto.getIdOffline() == null || dto.getIdOffline().isEmpty()) {
            dto.setIdOffline("OFF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        log.info("Début sauvegarde transaction offline ID={}", dto.getIdOffline());

        TransactionOffline entity = mapper.toEntity(dto);

        entity.setIdOffline(dto.getIdOffline());
        
        if (dto.getDateTransaction() == null || dto.getDateTransaction().isEmpty()) {
            entity.setDateTransaction(Instant.now());
        } else {
            try {
                String dateStr = dto.getDateTransaction();
                // Si la date ne finit pas par Z et n'a pas d'offset, on ajoute Z pour Instant.parse
                if (!dateStr.endsWith("Z") && !dateStr.contains("+") && dateStr.contains("T")) {
                    dateStr += "Z";
                }
                entity.setDateTransaction(Instant.parse(dateStr));
            } catch (Exception e) {
                log.warn("Format de date invalide: [{}]. Utilisation de Instant.now(). Erreur: {}", 
                        dto.getDateTransaction(), e.getMessage());
                entity.setDateTransaction(Instant.now());
            }
        }
        
        entity.setStatutSynchro(StatutSynchroOffline.EN_ATTENTE);

        // Tentative de récupération de l'employé avec gestion d'erreur plus robuste
        Employe initiateur = null;
        try {
            initiateur = employeRepository.findById(Integer.valueOf(dto.getIdEmploye()))
                    .orElse(null);
            
            if (initiateur == null) {
                // Essayer de trouver par login si l'ID n'est pas trouvé (cas où le mobile envoie le login)
                initiateur = employeRepository.findAll().stream()
                        .filter(e -> e.getUtilisateur() != null && dto.getIdEmploye().equalsIgnoreCase(e.getUtilisateur().getLogin()))
                        .findFirst()
                        .orElse(null);
            }
        } catch (NumberFormatException e) {
            log.warn("L'ID initiateur n'est pas numérique, recherche par login: {}", dto.getIdEmploye());
            initiateur = employeRepository.findAll().stream()
                    .filter(emp -> emp.getUtilisateur() != null && dto.getIdEmploye().equalsIgnoreCase(emp.getUtilisateur().getLogin()))
                    .findFirst()
                    .orElse(null);
        }

        if (initiateur == null) {
            log.error("Employé initiateur introuvable pour l'identifiant: {}", dto.getIdEmploye());
            throw new RuntimeException("Employé introuvable : " + dto.getIdEmploye());
        }
        entity.setEmploye(initiateur);

        entity.setClient(
                clientRepository.findByCodeClient(dto.getCodeClient())
                        .orElseThrow(() -> {
                            log.error("Client introuvable CODE={}", dto.getCodeClient());
                            return new RuntimeException("Client introuvable");
                        })
        );

        entity.setCompte(
                compteRepository.findById(dto.getIdCompte())
                        .orElseThrow(() -> {
                            log.error("Compte introuvable ID={}", dto.getIdCompte());
                            return new RuntimeException("Compte introuvable");
                        })
        );

        // Validation par Caissier (Nouveau Flux)
        if (dto.getIdCaissierValidation() != null && !dto.getIdCaissierValidation().isEmpty()) {
            Employe caissier = employeRepository.findById(Integer.valueOf(dto.getIdCaissierValidation()))
                    .orElse(null);
            if (caissier != null) {
                entity.setCaissierChoisi(caissier);
                log.info("Caissier valideur assigné : ID={}", dto.getIdCaissierValidation());
            }
        }

        TransactionOffline saved = repository.save(entity);

        log.info("Transaction offline sauvegardée avec succès ID={}", saved.getIdOffline());

        return mapper.toDto(saved);
    }

    // ----------------------------------------------------
    // Récupération par ID
    // ----------------------------------------------------
    @Override
    public TransactionOfflineDto getById(String idOffline) {

        log.info("Recherche transaction offline ID={}", idOffline);

        return repository.findById(idOffline)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                    log.error("Transaction offline introuvable ID={}", idOffline);
                    return new RuntimeException("Transaction offline introuvable");
                });
    }

    // ----------------------------------------------------
    // Liste de toutes les transactions offline
    // ----------------------------------------------------
    @Override
    public List<TransactionOfflineDto> getAll() {

        log.info("Récupération liste complète des transactions offline");

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // Filtrer par statut
    // ----------------------------------------------------
    @Override
    public List<TransactionOfflineDto> getByStatutSynchro(StatutSynchroOffline statut) {

        log.info("Recherche transactions offline par statut={}", statut);

        return repository.findByStatutSynchro(statut)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // Filtrer par employé
    // ----------------------------------------------------
    @Override
    public List<TransactionOfflineDto> getByEmploye(Integer idEmploye) {

        log.info("Recherche transactions offline par employé ID={}", idEmploye);

        return repository.findByEmploye_IdEmploye(idEmploye)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // Filtrer par employé (Aujourd'hui)
    // ----------------------------------------------------
    @Override
    public List<TransactionOfflineDto> getByEmployeToday(Integer idEmploye) {
        log.info("Recherche transactions offline par employé ID={} pour AUJOURD'HUI", idEmploye);

        Instant now = Instant.now();
        // Début de journée (UTC approximatif pour l'instant, à affiner selon TZ)
        Instant start = now.truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        // Fin de journée
        Instant end = start.plus(1, java.time.temporal.ChronoUnit.DAYS);

        return repository.findByEmploye_IdEmployeAndDateTransactionBetween(idEmploye, start, end)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // Filtrer par caissier (A valider)
    // ----------------------------------------------------
    @Override
    public List<TransactionOfflineDto> getByCaissier(Integer idCaissier) {
        log.info("Recherche transactions offline en attente de validation pour le caissier ID={}", idCaissier);

        return repository.findByCaissierChoisi_IdEmployeAndStatutSynchro(idCaissier, StatutSynchroOffline.EN_ATTENTE)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // Validation d'une transaction offline par le caissier
    // ----------------------------------------------------
    @Override
    public TransactionOfflineDto valider(String idOffline, Integer idCaissier) {
        log.info("Validation transaction offline ID={} par caissier ID={}", idOffline, idCaissier);

        TransactionOffline offline = repository.findById(idOffline)
                .orElseThrow(() -> new RuntimeException("Transaction offline introuvable"));

        if (offline.getStatutSynchro() != StatutSynchroOffline.EN_ATTENTE) {
            throw new RuntimeException("Cette transaction a déjà été traitée ou est déjà synchronisée");
        }

        Employe caissier = employeRepository.findById(idCaissier)
                .orElseThrow(() -> new RuntimeException("Caissier introuvable"));

        // 1. Créer la transaction finale
        Transaction transaction = new Transaction();
        transaction.setIdTransaction(codeGenerator.generateTransactionRef());
        transaction.setReference(codeGenerator.generateTransactionRef()); // Générer une référence unique
        transaction.setMontant(offline.getMontant());
        transaction.setTypeTransaction(offline.getTypeTransaction());
        transaction.setDateTransaction(offline.getDateTransaction()); // On garde la date de la collecte
        transaction.setCompte(offline.getCompte());
        transaction.setInitiateur(offline.getEmploye());
        transaction.setCaissierValidateur(caissier);
        transaction.setDateValidationCaisse(Instant.now());
        transaction.setStatut(StatutTransaction.VALIDEE_CAISSE);

        // Calculer les soldes avant/après (obligatoires)
        Compte compte = offline.getCompte();
        BigDecimal soldeAvant = compte.getSolde();
        BigDecimal montant = offline.getMontant();

        // Calcul du solde après selon le type de transaction
        BigDecimal soldeApres;
        if (offline.getTypeTransaction() == TypeTransaction.RETRAIT) {
            soldeApres = soldeAvant.subtract(montant);
        } else {
            // DEPOT ou VERSEMENT
            soldeApres = soldeAvant.add(montant);
        }

        transaction.setSoldeAvant(soldeAvant);
        transaction.setSoldeApres(soldeApres);

        // Mettre à jour le solde du compte
        compte.setSolde(soldeApres);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // 2. Marquer l'offline comme synchronisée
        offline.setStatutSynchro(StatutSynchroOffline.SYNCHRONISEE);
        offline.setTransactionFinale(savedTransaction);
        offline.setDateSynchro(Instant.now());
        offline.setCaissierChoisi(caissier); // Juste au cas où il n'était pas déjà mis

        return mapper.toDto(repository.save(offline));
    }

    @Override
    public void rejeter(String idOffline, String motif) {
        log.info("Rejet transaction offline ID={} pour motif: {}", idOffline, motif);
        TransactionOffline offline = repository.findById(idOffline)
                .orElseThrow(() -> new RuntimeException("Transaction offline introuvable"));

        offline.setStatutSynchro(StatutSynchroOffline.ERREUR); // Ou on peut ajouter REJETEE à l'id
        repository.save(offline);
    }

    // ----------------------------------------------------
    // Synchronisation transaction offline → transaction finale
    // ----------------------------------------------------
    @Override
    public TransactionOfflineDto markAsSynced(String idOffline, String idTransactionFinale) {

        log.info(
                "Début synchronisation offline={} vers transaction finale={}",
                idOffline, idTransactionFinale
        );

        TransactionOffline offline = repository.findById(idOffline)
                .orElseThrow(() -> {
                    log.error("Transaction offline introuvable ID={}", idOffline);
                    return new RuntimeException("Transaction offline introuvable");
                });

        Transaction transaction = transactionRepository.findById(idTransactionFinale)
                .orElseThrow(() -> {
                    log.error("Transaction finale introuvable ID={}", idTransactionFinale);
                    return new RuntimeException("Transaction finale introuvable");
                });

        offline.setTransactionFinale(transaction);
        offline.setStatutSynchro(StatutSynchroOffline.SYNCHRONISEE);
        offline.setDateSynchro(Instant.now());

        TransactionOffline saved = repository.save(offline);

        log.info(
                "Synchronisation réussie : offline={} → transaction={}",
                idOffline, idTransactionFinale
        );

        return mapper.toDto(saved);
    }
}
