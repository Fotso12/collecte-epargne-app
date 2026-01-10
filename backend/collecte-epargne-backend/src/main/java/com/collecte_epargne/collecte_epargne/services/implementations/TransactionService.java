package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import com.collecte_epargne.collecte_epargne.mappers.TransactionMapper;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.TransactionRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService implements TransactionInterface {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CompteRepository compteRepository;
    private final EmployeRepository employeRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            CompteRepository compteRepository,
            EmployeRepository employeRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.compteRepository = compteRepository;
        this.employeRepository = employeRepository;
    }

    // ----------------------------------------------------
    // Création d'une transaction
    // ----------------------------------------------------
    @Override
    public TransactionDto create(TransactionDto transactionDto) {

        log.info("Début création transaction ID={}", transactionDto.getIdTransaction());

        Transaction transaction = transactionMapper.toEntity(transactionDto);

        // ID + date
        transaction.setIdTransaction(transactionDto.getIdTransaction());
        transaction.setDateTransaction(Instant.now());

        // Statut initial
        transaction.setStatut(StatutTransaction.EN_ATTENTE);

        // Compte
        Compte compte = compteRepository.findById(transactionDto.getIdCompte())
                .orElseThrow(() -> {
                    log.error(
                            "Création transaction échouée : compte introuvable ID={}",
                            transactionDto.getIdCompte()
                    );
                    return new RuntimeException("Compte introuvable");
                });

        transaction.setCompte(compte);

        // Employé initiateur (collecteur)
        if (transactionDto.getIdEmployeInitiateur() != null) {

            Employe initiateur = employeRepository
                    .findById(Integer.valueOf(transactionDto.getIdEmployeInitiateur()))
                    .orElseThrow(() -> {
                        log.error(
                                "Employé initiateur introuvable ID={}",
                                transactionDto.getIdEmployeInitiateur()
                        );
                        return new RuntimeException("Employé initiateur introuvable");
                    });

            transaction.setInitiateur(initiateur);
        }

        Transaction saved = transactionRepository.save(transaction);

        log.info(
                "Transaction créée avec succès ID={} | Statut={}",
                saved.getIdTransaction(),
                saved.getStatut()
        );

        return transactionMapper.toDto(saved);
    }

    // ----------------------------------------------------
    // Récupération par ID
    // ----------------------------------------------------
    @Override
    public TransactionDto getById(String idTransaction) {

        log.info("Recherche transaction ID={}", idTransaction);

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> {
                    log.error("Transaction introuvable ID={}", idTransaction);
                    return new RuntimeException("Transaction introuvable");
                });

        return transactionMapper.toDto(transaction);
    }

    // ----------------------------------------------------
    // Liste de toutes les transactions
    // ----------------------------------------------------
    @Override
    public List<TransactionDto> getAll() {

        log.info("Récupération de toutes les transactions");

        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // Validation par le caissier
    // ----------------------------------------------------
    @Override
    public TransactionDto validerParCaissier(String idTransaction, String idCaissier) {

        log.info(
                "Validation caisse demandée | Transaction={} | Caissier={}",
                idTransaction, idCaissier
        );

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> {
                    log.error("Transaction introuvable ID={}", idTransaction);
                    return new RuntimeException("Transaction introuvable");
                });

        Employe caissier = employeRepository.findById(Integer.valueOf(idCaissier))
                .orElseThrow(() -> {
                    log.error("Caissier introuvable ID={}", idCaissier);
                    return new RuntimeException("Caissier introuvable");
                });

        transaction.setCaissierValidateur(caissier);
        transaction.setDateValidationCaisse(Instant.now());
        transaction.setStatut(StatutTransaction.VALIDEE_CAISSE);

        log.info(
                "Transaction validée par caissier | Transaction={} | Caissier={}",
                idTransaction, idCaissier
        );

        return transactionMapper.toDto(transaction);
    }

    // ----------------------------------------------------
    // Validation par le superviseur
    // ----------------------------------------------------
    @Override
    public TransactionDto validerParSuperviseur(String idTransaction, String idSuperviseur) {

        log.info(
                "Validation superviseur demandée | Transaction={} | Superviseur={}",
                idTransaction, idSuperviseur
        );

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> {
                    log.error("Transaction introuvable ID={}", idTransaction);
                    return new RuntimeException("Transaction introuvable");
                });

        Employe superviseur = employeRepository.findById(Integer.valueOf(idSuperviseur))
                .orElseThrow(() -> {
                    log.error("Superviseur introuvable ID={}", idSuperviseur);
                    return new RuntimeException("Superviseur introuvable");
                });

        transaction.setSuperviseurValidateur(superviseur);
        transaction.setDateValidationSuperviseur(Instant.now());
        transaction.setStatut(StatutTransaction.VALIDEE_SUPERVISEUR);

        log.info(
                "Transaction validée par superviseur | Transaction={} | Superviseur={}",
                idTransaction, idSuperviseur
        );

        return transactionMapper.toDto(transaction);
    }

    // ----------------------------------------------------
    // Rejet d'une transaction
    // ----------------------------------------------------
    @Override
    public void rejeterTransaction(String idTransaction, String motifRejet) {

        log.info(
                "Rejet transaction demandé | Transaction={} | Motif={}",
                idTransaction, motifRejet
        );

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> {
                    log.error("Transaction introuvable ID={}", idTransaction);
                    return new RuntimeException("Transaction introuvable");
                });

        transaction.setMotifRejet(motifRejet);
        transaction.setStatut(StatutTransaction.REJETEE);

        transactionRepository.save(transaction);

        log.info(
                "Transaction rejetée avec succès | Transaction={}",
                idTransaction
        );
    }
}
