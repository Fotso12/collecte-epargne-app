package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.utils.StatusApprobation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, String> {

    // Trouver un compte par son numéro unique (numCompte)
    Optional<Compte> findByNumCompte(String numCompte);

    // Trouver tous les comptes d'un client spécifique
    List<Compte> findByClientCodeClient(String codeClient);

    // Trouver tous les comptes d'un type spécifique
    List<Compte> findByTypeCompteId(Integer idType);
    
    // Calculer le solde total de tous les comptes
    @Query("SELECT COALESCE(SUM(c.solde), 0) FROM Compte c")
    BigDecimal sumAllSoldes();

    // NOTE: Replaced legacy Institution-based queries by AgenceZone-based queries below.

    // COMMENT CLEF: Query methods AgenceZone - Superviseur approbation
        @Query("SELECT c FROM Compte c WHERE c.statusApprobation = :statusApprobation " +
            "AND c.client.collecteurAssigne.agenceZone = :agenceZone " +
            "ORDER BY c.dateOuverture DESC")
    List<Compte> findPendingApprovalsByAgence(
        StatusApprobation statusApprobation,
        AgenceZone agenceZone
    );

    @Query("SELECT COUNT(c) FROM Compte c WHERE c.statusApprobation = :statusApprobation " +
           "AND c.client.collecteurAssigne.agenceZone = :agenceZone")
    long countPendingApprovalsByAgence(
        StatusApprobation statusApprobation,
        AgenceZone agenceZone
    );
}
