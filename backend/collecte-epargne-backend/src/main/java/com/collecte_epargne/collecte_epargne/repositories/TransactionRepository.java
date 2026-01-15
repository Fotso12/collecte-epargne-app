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

        List<Transaction> findByDateTransactionBetween(java.time.Instant start, java.time.Instant end);

        List<Transaction> findByDateTransactionBetweenAndInitiateur_AgenceZone_IdAgence(java.time.Instant start, java.time.Instant end, Integer idAgence);

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

        @Query("SELECT t.initiateur.idEmploye, t.initiateur.utilisateur.nom, SUM(t.montant) FROM Transaction t " +
               "WHERE t.statut = :statut AND t.typeTransaction = :type AND t.initiateur.agenceZone.idAgence = :idAgence " +
               "GROUP BY t.initiateur.idEmploye, t.initiateur.utilisateur.nom ORDER BY SUM(t.montant) DESC")
        List<Object[]> findCollectorsOrderBySumMontantDescByAgence(@Param("statut") com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut,
                                                                  @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type,
                                                                  @Param("idAgence") Integer idAgence);

        List<Transaction> findByInitiateur_AgenceZone_IdAgence(Integer idAgence);

    List<Transaction> findByInitiateur_AgenceZone_IdAgenceAndStatut(Integer idAgence, com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut);

    long countByStatut(com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut);

    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.statut IN :statuts AND t.typeTransaction = :type AND t.initiateur.agenceZone.idAgence = :idAgence")
    BigDecimal sumMontantByStatutsTypeAndAgence(@Param("statuts") java.util.Collection<com.collecte_epargne.collecte_epargne.utils.StatutTransaction> statuts,
                                               @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type,
                                               @Param("idAgence") Integer idAgence);

    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.statut = :statut AND t.typeTransaction = :type AND t.dateTransaction >= :startDate AND t.initiateur.agenceZone.idAgence = :idAgence")
    BigDecimal sumMontantByStatutTypeDateAfterAndAgence(@Param("statut") com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut,
                                                        @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type,
                                                        @Param("startDate") java.time.Instant startDate,
                                                        @Param("idAgence") Integer idAgence);

    long countByInitiateur_AgenceZone_IdAgence(Integer idAgence);

    long countByStatutAndInitiateur_AgenceZone_IdAgence(com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut, Integer idAgence);
}
