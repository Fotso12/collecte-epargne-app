package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanCotisationRepository extends JpaRepository<PlanCotisation, String> {

    // Trouver un plan par ID
    Optional<PlanCotisation> findById(String idPlan);

    // Trouver un plan par nom
    Optional<PlanCotisation> findByNom(String nom);
}
