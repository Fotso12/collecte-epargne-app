package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.RapportCollecteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RapportCollecteurRepository extends JpaRepository<RapportCollecteur, String> {

    // Trouver un rapport par ID
    Optional<RapportCollecteur> findByIdRapport(String idRapport);

    // Trouver les rapports par employé
    List<RapportCollecteur> findByIdEmploye(String idEmploye);

    // Trouver les rapports par date
    List<RapportCollecteur> findByDateRapport(LocalDate dateRapport);
}
