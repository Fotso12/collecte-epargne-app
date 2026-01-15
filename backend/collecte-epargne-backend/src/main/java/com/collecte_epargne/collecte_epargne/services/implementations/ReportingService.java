package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.mappers.TransactionMapper;
import com.collecte_epargne.collecte_epargne.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public ReportingService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public List<TransactionDto> getTransactionsByDateRange(Instant startDate, Instant endDate) {
        return transactionRepository.findByDateTransactionBetween(startDate, endDate).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByDateRangeAndAgence(Instant startDate, Instant endDate, Integer idAgence) {
        if (idAgence == null) return getTransactionsByDateRange(startDate, endDate);
        return transactionRepository.findByDateTransactionBetweenAndInitiateur_AgenceZone_IdAgence(startDate, endDate, idAgence).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
