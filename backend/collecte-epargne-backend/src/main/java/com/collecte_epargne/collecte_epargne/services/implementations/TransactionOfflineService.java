package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.mappers.TransactionOfflineMapper;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionOfflineInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TransactionOfflineService(TransactionOfflineRepository repository, TransactionOfflineMapper mapper, EmployeRepository employeRepository, ClientRepository clientRepository, CompteRepository compteRepository, TransactionRepository transactionRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.transactionRepository = transactionRepository;
    }

    // ----------------------------------------------------
    // Création transaction offline
    // ----------------------------------------------------
    @Override
    public TransactionOfflineDto save(TransactionOfflineDto dto) {

        log.info("Début sauvegarde transaction offline ID={}", dto.getIdOffline());

        TransactionOffline entity = mapper.toEntity(dto);

        entity.setIdOffline(dto.getIdOffline());
        entity.setDateTransaction(dto.getDateTransaction());
        entity.setStatutSynchro(StatutSynchroOffline.EN_ATTENTE);

        entity.setEmploye(
                employeRepository.findById(Integer.valueOf(dto.getIdEmploye()))
                        .orElseThrow(() -> {
                            log.error("Employé introuvable ID={}", dto.getIdEmploye());
                            return new RuntimeException("Employé introuvable");
                        })
        );

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
