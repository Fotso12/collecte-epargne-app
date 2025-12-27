package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

    public interface TransactionRepository extends JpaRepository<Transaction, String> {

        Optional<Transaction> findByReference(String reference);
    }
