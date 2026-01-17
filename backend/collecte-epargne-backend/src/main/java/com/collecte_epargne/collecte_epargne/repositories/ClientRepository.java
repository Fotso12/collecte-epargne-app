package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
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



    void deleteByCodeClient(String codeClient);
    // Trouver tous les clients assignés à un collecteur spécifique (nécessaire pour la recherche des employés)
    List<Client> findByCollecteurAssigneIdEmploye(Integer idEmploye);
    
    // Compter les clients assignés à un collecteur
    long countByCollecteurAssigne(com.collecte_epargne.collecte_epargne.entities.Employe collecteur);


    //Trouver les clients en fonction de leur code
    Optional<Client> findByCodeClient(String codeClient);
    boolean existsByCodeClient(String codeClient);

    // COMMENT CLEF: Query methods AgenceZone - Caissier/Superviseur
    List<Client> findByCollecteurAssigne_AgenceZone(AgenceZone agenceZone);

    Optional<Client> findByUtilisateurLogin(String login);

    long countByCollecteurAssigne_AgenceZone(AgenceZone agenceZone);
}
