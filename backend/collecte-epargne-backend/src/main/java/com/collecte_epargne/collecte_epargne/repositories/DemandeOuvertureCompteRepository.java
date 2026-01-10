package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.DemandeOuvertureCompte;
import com.collecte_epargne.collecte_epargne.utils.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeOuvertureCompteRepository extends JpaRepository<DemandeOuvertureCompte, Long> {
    
    // Trouver toutes les demandes d'un client
    List<DemandeOuvertureCompte> findByClientCodeClient(String codeClient);
    
    // Trouver toutes les demandes par statut
    List<DemandeOuvertureCompte> findByStatut(StatutDemande statut);
    
    // Trouver toutes les demandes en attente
    List<DemandeOuvertureCompte> findByStatutOrderByDateDemandeDesc(StatutDemande statut);
    
    // Trouver une demande par client et type de compte (pour éviter les doublons)
    Optional<DemandeOuvertureCompte> findByClientCodeClientAndTypeCompteIdAndStatut(
        String codeClient, 
        Integer idTypeCompte, 
        StatutDemande statut
    );
}

