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
        return transactionRepository.findAll().stream() // Optimisation: use findByDateTransactionBetween in repo
                .filter(t -> {
                    if (t.getDateTransaction() == null) return false;
                    return t.getDateTransaction().isAfter(startDate) && t.getDateTransaction().isBefore(endDate);
                })
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
