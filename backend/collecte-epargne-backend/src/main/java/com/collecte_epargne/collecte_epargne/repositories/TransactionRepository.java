package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

    public interface TransactionRepository extends JpaRepository<Transaction, String> {

        Optional<Transaction> findByReference(String reference);

        @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.statut IN :statuts AND t.typeTransaction = :type")
        BigDecimal sumMontantByStatutsAndType(@Param("statuts") java.util.Collection<com.collecte_epargne.collecte_epargne.utils.StatutTransaction> statuts,
                                             @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type);

        @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.statut = :statut AND t.typeTransaction = :type AND t.dateTransaction >= :startDate")
        BigDecimal sumMontantByStatutTypeAndDateAfter(@Param("statut") com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut,
                                                     @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type,
                                                     @Param("startDate") java.time.Instant startDate);

        @Query("SELECT t.initiateur.idEmploye, t.initiateur.utilisateur.nom, SUM(t.montant) FROM Transaction t " +
               "WHERE t.statut = :statut AND t.typeTransaction = :type " +
               "GROUP BY t.initiateur.idEmploye, t.initiateur.utilisateur.nom ORDER BY SUM(t.montant) DESC")
        List<Object[]> findCollectorsOrderBySumMontantDesc(@Param("statut") com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut,
                                                          @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type);

        long countByStatut(com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut);
    }
