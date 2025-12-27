package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
<<<<<<< HEAD
=======
import java.util.Optional;
>>>>>>> e7d8b8a8ef19a82cdbc5dd8aa8c4525106492910

@Repository
public interface CompteCotisationRepository extends JpaRepository<CompteCotisation, String> {

<<<<<<< HEAD
    List<CompteCotisation> findByCompteIdCompte(String idCompte);

    List<CompteCotisation> findByPlanCotisationIdPlan(String idPlanCotisation);

    boolean existsByPlanCotisationIdPlan(String idPlanCotisation);
}

=======
    // Trouver une cotisation par ID
    Optional<CompteCotisation> findById(String id);

    // Trouver toutes les cotisations d'un compte
    List<CompteCotisation> findByCompte_IdCompte(String idCompte);

    // Trouver toutes les cotisations d'un plan
    List<CompteCotisation> findByPlanCotisation_IdPlan(String idPlan);
}
>>>>>>> e7d8b8a8ef19a82cdbc5dd8aa8c4525106492910
