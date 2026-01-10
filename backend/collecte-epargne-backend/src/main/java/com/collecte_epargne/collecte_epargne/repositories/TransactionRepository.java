package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

import java.util.Optional;

    public interface TransactionRepository extends JpaRepository<Transaction, String> {

        Optional<Transaction> findByReference(String reference);

        @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.statut = :statut AND t.typeTransaction = :type")
        BigDecimal sumMontantByStatutAndType(@Param("statut") com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut,
                                             @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type);

        long countByStatut(com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut);
    }
