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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService implements TransactionInterface {

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

    // ------------------------------------------------------------------
    // Création d'une transaction
    // ------------------------------------------------------------------
    @Override
    public TransactionDto create(TransactionDto transactionDto) {

        Transaction transaction = transactionMapper.toEntity(transactionDto);

        // ID et date
        transaction.setIdTransaction(transactionDto.getIdTransaction());
        transaction.setDateTransaction(Instant.now());

        // Statut initial
        transaction.setStatut(StatutTransaction.EN_ATTENTE);

        // Compte
        Compte compte = compteRepository.findById(transactionDto.getIdCompte())
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        transaction.setCompte(compte);

        // Employé initiateur (collecteur)
        if (transactionDto.getIdEmployeInitiateur() != null) {
            Employe initiateur = employeRepository.findById(
                    Integer.valueOf(transactionDto.getIdEmployeInitiateur())
            ).orElseThrow(() -> new RuntimeException("Employé initiateur introuvable"));

            transaction.setInitiateur(initiateur);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    // ------------------------------------------------------------------
    // Récupération par ID
    // ------------------------------------------------------------------
    @Override
    public TransactionDto getById(String idTransaction) {
        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        return transactionMapper.toDto(transaction);
    }

    // ------------------------------------------------------------------
    // Liste de toutes les transactions
    // ------------------------------------------------------------------
    @Override
    public List<TransactionDto> getAll() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Validation par le caissier
    // ------------------------------------------------------------------
    @Override
    public TransactionDto validerParCaissier(String idTransaction, String idCaissier) {

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        Employe caissier = employeRepository.findById(Integer.valueOf(idCaissier))
                .orElseThrow(() -> new RuntimeException("Caissier introuvable"));

        transaction.setCaissierValidateur(caissier);
        transaction.setDateValidationCaisse(Instant.now());
        transaction.setStatut(StatutTransaction.VALIDEE_CAISSE);

        return transactionMapper.toDto(transaction);
    }

    // ------------------------------------------------------------------
    // Validation par le superviseur
    // ------------------------------------------------------------------
    @Override
    public TransactionDto validerParSuperviseur(String idTransaction, String idSuperviseur) {

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        Employe superviseur = employeRepository.findById(Integer.valueOf(idSuperviseur))
                .orElseThrow(() -> new RuntimeException("Superviseur introuvable"));

        transaction.setSuperviseurValidateur(superviseur);
        transaction.setDateValidationSuperviseur(Instant.now());
        transaction.setStatut(StatutTransaction.VALIDEE_SUPERVISEUR);

        return transactionMapper.toDto(transaction);
    }

    // ------------------------------------------------------------------
    // Rejet d'une transaction
    // ------------------------------------------------------------------
    @Override
    public void rejeterTransaction(String idTransaction, String motifRejet) {

        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        transaction.setMotifRejet(motifRejet);
        transaction.setStatut(StatutTransaction.REJETEE);

        transactionRepository.save(transaction);
    }
}
