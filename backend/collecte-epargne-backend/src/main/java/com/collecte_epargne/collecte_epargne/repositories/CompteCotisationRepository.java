package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompteCotisationRepository extends JpaRepository<CompteCotisation, String> {

    // Trouver une cotisation par ID
    Optional<CompteCotisation> findById(String id);

    // Trouver toutes les cotisations d'un compte
    List<CompteCotisation> findByCompte_IdCompte(String idCompte);

    // Trouver toutes les cotisations d'un plan
    List<CompteCotisation> findByPlanCotisation_IdPlan(String idPlan);
}
