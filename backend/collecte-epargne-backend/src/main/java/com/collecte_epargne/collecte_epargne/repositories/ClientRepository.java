package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Client;
import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    // Trouver un client par son numéro unique (numeroClient)
    Optional<Client> findByNumeroClient(String numeroClient);

    // Trouver un client par son numéro de CNI
    Optional<Client> findByNumCni(String numCni);

    // Trouver tous les clients assignés à un collecteur spécifique (nécessaire pour la recherche des employés)
    List<Client> findByCollecteurAssigneIdEmploye(Integer idEmploye);

    //Trouver les clients en fonction de leur code
    Optional<Client> findByCodeClient(String codeClient);
}
