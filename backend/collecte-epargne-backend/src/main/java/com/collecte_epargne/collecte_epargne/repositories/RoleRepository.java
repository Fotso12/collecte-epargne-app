package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Trouver un rôle par son nom
    Optional<Role> findByNom(String nom);

    // Trouver un rôle par son code (Nécessaire pour l'unicité)
    Optional<Role> findByCode(String code);
}