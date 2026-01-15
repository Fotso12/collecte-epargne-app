package com.collecte_epargne.collecte_epargne.service_test;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.mappers.TransactionOfflineMapper;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.services.implementations.TransactionOfflineService;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionOfflineServiceTest {
    @Mock
    private TransactionOfflineRepository repository;

    @Mock
    private TransactionOfflineMapper mapper;

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionOfflineService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTransactionOffline_success() {
        TransactionOfflineDto dto = new TransactionOfflineDto();
        dto.setIdOffline("OFF1");
        dto.setIdEmploye("1");
        dto.setCodeClient("CL1");
        dto.setIdCompte("C1");
        dto.setDateTransaction(Instant.now().toString());
        dto.setMontant(BigDecimal.valueOf(500));

        TransactionOffline entity = new TransactionOffline();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(employeRepository.findById(1)).thenReturn(Optional.of(new Employe()));
        when(clientRepository.findByCodeClient("CL1")).thenReturn(Optional.of(new Client()));
        when(compteRepository.findById("C1")).thenReturn(Optional.of(new Compte()));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        TransactionOfflineDto result = service.save(dto);

        assertNotNull(result);
        assertEquals(StatutSynchroOffline.EN_ATTENTE, entity.getStatutSynchro());
    }

    @Test
    void markAsSynced_success() {
        TransactionOffline offline = new TransactionOffline();
        Transaction transaction = new Transaction();

        when(repository.findById("OFF1")).thenReturn(Optional.of(offline));
        when(transactionRepository.findById("TX1")).thenReturn(Optional.of(transaction));
        when(repository.save(offline)).thenReturn(offline);
        when(mapper.toDto(offline)).thenReturn(new TransactionOfflineDto());

        TransactionOfflineDto result =
                service.markAsSynced("OFF1", "TX1");

        assertEquals(StatutSynchroOffline.SYNCHRONISEE, offline.getStatutSynchro());
        assertNotNull(offline.getDateSynchro());
    }

    @Test
    void getById_success() {
        TransactionOffline offline = new TransactionOffline();

        when(repository.findById("OFF1")).thenReturn(Optional.of(offline));
        when(mapper.toDto(offline)).thenReturn(new TransactionOfflineDto());

        TransactionOfflineDto dto = service.getById("OFF1");

        assertNotNull(dto);
    }
}
