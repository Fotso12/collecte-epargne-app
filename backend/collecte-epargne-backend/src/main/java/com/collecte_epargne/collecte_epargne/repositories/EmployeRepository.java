package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Institution;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Integer> {

    Optional<Employe> findByMatricule(String matricule);

    List<Employe> findByTypeEmploye(TypeEmploye typeEmploye);

    Optional<Employe> findByUtilisateurLogin(String login);

    List<Employe> findBySuperviseurIdEmploye(Integer idSuperviseur);

    // Nouvelle requête pour récupérer les collecteurs triés par le nombre de clients
    @Query("SELECT e FROM Employe e LEFT JOIN e.clientsAssignes c WHERE e.typeEmploye = com.collecte_epargne.collecte_epargne.utils.TypeEmploye.COLLECTEUR GROUP BY e.idEmploye ORDER BY COUNT(c) DESC")
    List<Employe> findCollecteursOrderByClientCountDesc();

    // Requête pour récupérer les collecteurs triés par le score total d'épargne des clients
    @Query("SELECT e FROM Employe e LEFT JOIN e.clientsAssignes c WHERE e.typeEmploye = com.collecte_epargne.collecte_epargne.utils.TypeEmploye.COLLECTEUR GROUP BY e.idEmploye ORDER BY SUM(c.scoreEpargne) DESC")
    List<Employe> findCollecteursOrderByTotalClientScoreDesc();

    // COMMENT CLEF: Query methods pour Caissier/Superviseur workflows
    List<Employe> findByAgenceZone_IdAndTypeEmploye(Integer agenceId, TypeEmploye typeEmploye);

    // Ancienne méthode legacy utilisant 'Institution' sur Utilisateur supprimée.
    // Utiliser les méthodes basées sur `AgenceZone` ci-dessous pour les filtres par agence.

    long countByTypeEmploye(TypeEmploye typeEmploye);

    // COMMENT CLEF: Query methods AgenceZone (remplace Institution)
    List<Employe> findByAgenceZoneAndTypeEmploye(AgenceZone agenceZone, TypeEmploye typeEmploye);

    List<Employe> findBySuperviseurAndAgenceZone(Employe superviseur, AgenceZone agenceZone);
}