package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CompteCotisationRepository extends JpaRepository<CompteCotisation, String> {

    List<CompteCotisation> findByCompteIdCompte(String idCompte);

    List<CompteCotisation> findByPlanCotisationIdPlan(String idPlanCotisation);

    boolean existsByPlanCotisationIdPlan(String idPlanCotisation);
}

