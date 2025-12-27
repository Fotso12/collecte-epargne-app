package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Client;
import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Trouver un client par son numéro unique (numeroClient)
    Optional<Client> findByNumeroClient(Long numeroClient);

    // Trouver un client par son numéro de CNI
    Optional<Client> findByNumCni(String numCni);

    Optional<Client> findByCodeClient(String codeClient);


    void deleteByCodeClient(String codeClient);
    // Trouver tous les clients assignés à un collecteur spécifique (nécessaire pour la recherche des employés)
    List<Client> findByCollecteurAssigneIdEmploye(Integer idEmploye);

    boolean existsByCodeClient(String codeClient);
}
