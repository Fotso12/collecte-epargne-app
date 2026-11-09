package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.TransactionOffline;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionOfflineRepository extends JpaRepository<TransactionOffline, String> {

    // Transactions offline non synchronisées
    List<TransactionOffline> findByStatutSynchro(StatutSynchroOffline statutSynchro);

    // Transactions offline d’un employé
    List<TransactionOffline> findByEmploye_IdEmploye(Integer idEmploye);

    // Transactions offline d'un employé sur une période (ex: aujourd'hui)
    List<TransactionOffline> findByEmploye_IdEmployeAndDateTransactionBetween(Integer idEmploye, java.time.Instant start, java.time.Instant end);
}