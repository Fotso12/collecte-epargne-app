package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.mappers.TransactionOfflineMapper;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionOfflineInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionOfflineService implements TransactionOfflineInterface {

    private final TransactionOfflineRepository repository;
    private final TransactionOfflineMapper mapper;
    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;

    public TransactionOfflineService(
            TransactionOfflineRepository repository,
            TransactionOfflineMapper mapper,
            EmployeRepository employeRepository,
            ClientRepository clientRepository,
            CompteRepository compteRepository,
            TransactionRepository transactionRepository
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionOfflineDto save(TransactionOfflineDto dto) {

        TransactionOffline entity = mapper.toEntity(dto);

        entity.setIdOffline(dto.getIdOffline());
        entity.setDateTransaction(dto.getDateTransaction());
        entity.setStatutSynchro(StatutSynchroOffline.EN_ATTENTE);

        entity.setEmploye(
                employeRepository.findById(Integer.valueOf(dto.getIdEmploye()))
                        .orElseThrow(() -> new RuntimeException("Employé introuvable"))
        );

        entity.setClient(
                clientRepository.findById(dto.getCodeClient())
                        .orElseThrow(() -> new RuntimeException("Client introuvable"))
        );

        entity.setCompte(
                compteRepository.findById(dto.getIdCompte())
                        .orElseThrow(() -> new RuntimeException("Compte introuvable"))
        );

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public TransactionOfflineDto getById(String idOffline) {
        return repository.findById(idOffline)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Transaction offline introuvable"));
    }

    @Override
    public List<TransactionOfflineDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionOfflineDto> getByStatutSynchro(StatutSynchroOffline statut) {
        return repository.findByStatutSynchro(statut)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionOfflineDto> getByEmploye(Integer idEmploye) {
        return repository.findByEmploye_IdEmploye(idEmploye)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionOfflineDto markAsSynced(String idOffline, String idTransactionFinale) {

        TransactionOffline offline = repository.findById(idOffline)
                .orElseThrow(() -> new RuntimeException("Transaction offline introuvable"));

        Transaction transaction = transactionRepository.findById(idTransactionFinale)
                .orElseThrow(() -> new RuntimeException("Transaction finale introuvable"));

        offline.setTransactionFinale(transaction);
        offline.setStatutSynchro(StatutSynchroOffline.SYNCHRONISEE);
        offline.setDateSynchro(Instant.now());

        return mapper.toDto(repository.save(offline));
    }
}