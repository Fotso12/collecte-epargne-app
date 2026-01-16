package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Transaction;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Institution;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.utils.StatusValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
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
                                                     @Param("startDate") Instant startDate);

        @Query("SELECT t.initiateur.idEmploye, t.initiateur.utilisateur.nom, SUM(t.montant) FROM Transaction t " +
               "WHERE t.statut = :statut AND t.typeTransaction = :type " +
               "GROUP BY t.initiateur.idEmploye, t.initiateur.utilisateur.nom ORDER BY SUM(t.montant) DESC")
        List<Object[]> findCollectorsOrderBySumMontantDesc(@Param("statut") com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut,
                                                          @Param("type") com.collecte_epargne.collecte_epargne.utils.TypeTransaction type);

        long countByStatut(com.collecte_epargne.collecte_epargne.utils.StatutTransaction statut);

        // COMMENT CLEF: Query methods pour workflow Caissier - validation transactions
        List<Transaction> findByStatusValidationAndCaissierValidateur_AgenceZone_IdOrderByDateTransactionDesc(
            StatusValidation status,
            Integer agenceId
        );

        List<Transaction> findByStatusValidationAndCaissierValidateur_AgenceZone_Id(
            StatusValidation status,
            Integer agenceId
        );

        List<Transaction> findByStatusValidationAndDateValidationCaisseBetweenAndCaissierValidateur_AgenceZone_Id(
            StatusValidation status,
            Instant dateStart,
            Instant dateEnd,
            Integer agenceId
        );

        // COMMENT CLEF: Query methods pour workflow Superviseur - KPIs institution
        // Remplacer les requêtes basées sur `Utilisateur.Institution` par des requêtes basées sur `AgenceZone`
        List<Transaction> findByStatusValidationAndDateValidationCaisseBetweenAndCaissierValidateur_AgenceZone(
            StatusValidation status,
            Instant dateStart,
            Instant dateEnd,
            AgenceZone agenceZone
        );

        List<Transaction> findByStatusValidationAndDateTransactionBetweenAndInitiateur_AgenceZone(
            StatusValidation status,
            Instant dateStart,
            Instant dateEnd,
            AgenceZone agenceZone
        );

        // COMMENT CLEF: Query methods pour Earnings Service - calcul commissions
        List<Transaction> findByInitiateurAndDateTransactionBetween(
            Employe initiateur,
            Instant dateStart,
            Instant dateEnd
        );

        List<Transaction> findByCaissierValidateurAndDateValidationCaisseBetween(
            Employe cashier,
            Instant dateStart,
            Instant dateEnd
        );

        // Cette variante par Institution a été remplacée par les méthodes AgenceZone ci-dessus.

        // COMMENT CLEF: Query methods pour AgenceZone - Superviseur & Caissier
        @Query("SELECT t FROM Transaction t WHERE t.initiateur.agenceZone = :agence " +
               "AND t.dateTransaction BETWEEN :startDate AND :endDate " +
               "ORDER BY t.dateTransaction DESC")
        List<Transaction> findByInitiateur_AgenceZoneAndDateTransactionBetween(
            @Param("agence") AgenceZone agence,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
        );

        @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.initiateur.agenceZone = :agence " +
               "AND t.dateTransaction BETWEEN :startDate AND :endDate")
        BigDecimal sumMontantByAgenceAndPeriod(
            @Param("agence") AgenceZone agence,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
        );
    }