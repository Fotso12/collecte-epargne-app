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
import com.collecte_epargne.collecte_epargne.utils.CodeGenerator;
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

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CompteRepository compteRepository;
    private final EmployeRepository employeRepository;
    private final CodeGenerator codeGenerator;

    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            CompteRepository compteRepository,
            EmployeRepository employeRepository,
            CodeGenerator codeGenerator
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.compteRepository = compteRepository;
        this.employeRepository = employeRepository;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public TransactionDto create(TransactionDto transactionDto) {
        log.info("Début création transaction");
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        
        if (transactionDto.getIdTransaction() == null || transactionDto.getIdTransaction().isEmpty()) {
            transaction.setIdTransaction(codeGenerator.generateTransactionRef());
        } else {
            transaction.setIdTransaction(transactionDto.getIdTransaction());
        }

        transaction.setDateTransaction(Instant.now());
        transaction.setStatut(StatutTransaction.EN_ATTENTE);

        Compte compte = compteRepository.findById(transactionDto.getIdCompte())
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        transaction.setCompte(compte);

        if (transactionDto.getIdEmployeInitiateur() != null) {
            try {
                Employe initiateur = employeRepository.findById(Integer.valueOf(transactionDto.getIdEmployeInitiateur()))
                        .orElseThrow(() -> new RuntimeException("Employé initiateur introuvable"));
                transaction.setInitiateur(initiateur);
            } catch (NumberFormatException e) {
                log.warn("L'ID initiateur n'est pas numérique : {}", transactionDto.getIdEmployeInitiateur());
            }
        }

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDto(saved);
    }

    @Override
    public List<TransactionDto> getAll() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto getById(String idTransaction) {
        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));
        return transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDto validerParCaissier(String idTransaction, String idCaissier) {
        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        Employe caissier = findEmployeByAnyIdentifier(idCaissier);

        transaction.setCaissierValidateur(caissier);
        transaction.setDateValidationCaisse(Instant.now());
        transaction.setStatut(StatutTransaction.VALIDEE_CAISSE);

        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDto validerParSuperviseur(String idTransaction, String idSuperviseur) {
        log.info("Tentative de validation superviseur pour TX: {} par ID: {}", idTransaction, idSuperviseur);

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        Employe superviseur = findEmployeByAnyIdentifier(idSuperviseur);

        transaction.setSuperviseurValidateur(superviseur);
        transaction.setDateValidationSuperviseur(Instant.now());
        transaction.setStatut(StatutTransaction.VALIDEE_SUPERVISEUR);

        log.info("Validation réussie pour la transaction {}", idTransaction);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    /**
     * Recherche manuelle pour éviter les erreurs de génération de requêtes SQL incorrectes
     */
    private Employe findEmployeByAnyIdentifier(String identifier) {
        log.info("Recherche de l'employé pour l'identifiant: {}", identifier);

        // On force la récupération de la liste pour filtrer en mémoire Java
        // afin de contourner le problème de colonne 'login' vs 'email' en SQL
        return employeRepository.findAll().stream()
                .filter(e -> e.getUtilisateur() != null && (
                        identifier.equalsIgnoreCase(e.getUtilisateur().getEmail()) ||
                                identifier.equalsIgnoreCase(e.getUtilisateur().getLogin())
                ))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("ERREUR : Aucun employé trouvé avec le login ou l'email: {}", identifier);
                    return new RuntimeException("Employé introuvable : " + identifier);
                });
    }

    @Override
    public void rejeterTransaction(String idTransaction, String motifRejet) {
        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));
        transaction.setMotifRejet(motifRejet);
        transaction.setStatut(StatutTransaction.REJETEE);
        transactionRepository.save(transaction);
    }
}