package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeCompteRepository extends JpaRepository<TypeCompte, Integer> {

    // Trouver un type de compte par son code
    Optional<TypeCompte> findByCode(String code);

    // Trouver un type de compte par son nom
    Optional<TypeCompte> findByNom(String nom);
}
