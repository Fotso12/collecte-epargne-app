package com.collecte_epargne.collecte_epargne.service_test;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import com.collecte_epargne.collecte_epargne.mappers.TransactionMapper;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.TransactionRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.TransactionService;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_success() {
        // GIVEN
        TransactionDto dto = new TransactionDto();
        dto.setIdTransaction("TX1");
        dto.setIdCompte("C1");
        dto.setMontant(BigDecimal.valueOf(1000));

        Transaction transaction = new Transaction();
        Compte compte = new Compte();

        when(transactionMapper.toEntity(dto)).thenReturn(transaction);
        when(compteRepository.findById("C1")).thenReturn(Optional.of(compte));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        // WHEN
        TransactionDto result = transactionService.create(dto);

        // THEN
        assertNotNull(result);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void validerParCaissier_success() {
        Transaction transaction = new Transaction();
        Employe caissier = new Employe();

        when(transactionRepository.findById("TX1")).thenReturn(Optional.of(transaction));
        when(employeRepository.findById(1)).thenReturn(Optional.of(caissier));
        when(transactionMapper.toDto(transaction)).thenReturn(new TransactionDto());

        TransactionDto result = transactionService.validerParCaissier("TX1", "1");

        assertEquals(StatutTransaction.VALIDEE_CAISSE, transaction.getStatut());
        assertNotNull(transaction.getDateValidationCaisse());
    }

    @Test
    void validerParSuperviseur_success() {
        Transaction transaction = new Transaction();
        Employe superviseur = new Employe();

        when(transactionRepository.findById("TX1")).thenReturn(Optional.of(transaction));
        when(employeRepository.findById(2)).thenReturn(Optional.of(superviseur));
        when(transactionMapper.toDto(transaction)).thenReturn(new TransactionDto());

        TransactionDto result = transactionService.validerParSuperviseur("TX1", "2");

        assertEquals(StatutTransaction.VALIDEE_SUPERVISEUR, transaction.getStatut());
        assertNotNull(transaction.getDateValidationSuperviseur());
    }

    @Test
    void rejeterTransaction_success() {
        Transaction transaction = new Transaction();

        when(transactionRepository.findById("TX1")).thenReturn(Optional.of(transaction));

        transactionService.rejeterTransaction("TX1", "Montant incorrect");

        assertEquals(StatutTransaction.REJETEE, transaction.getStatut());
        assertEquals("Montant incorrect", transaction.getMotifRejet());
    }
}
