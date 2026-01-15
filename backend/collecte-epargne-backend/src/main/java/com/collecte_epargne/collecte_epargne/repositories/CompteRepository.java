package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COALESCE(SUM(c.solde), 0) FROM Compte c WHERE c.client.collecteurAssigne.agenceZone.idAgence = :idAgence")
    BigDecimal sumAllSoldesByAgence(@Param("idAgence") Integer idAgence);

    long countByClient_CollecteurAssigne_AgenceZone_IdAgence(Integer idAgence);
}
