package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT e FROM Employe e LEFT JOIN e.clientsAssignes c WHERE e.typeEmploye = com.collecte_epargne.collecte_epargne.utils.TypeEmploye.COLLECTEUR AND e.agenceZone.idAgence = :idAgence GROUP BY e.idEmploye ORDER BY COUNT(c) DESC")
    List<Employe> findCollecteursOrderByClientCountDescByAgence(@Param("idAgence") Integer idAgence);

    // Requête pour récupérer les collecteurs triés par le score total d'épargne des clients

    @Query("SELECT e FROM Employe e LEFT JOIN e.clientsAssignes c WHERE e.typeEmploye = com.collecte_epargne.collecte_epargne.utils.TypeEmploye.COLLECTEUR GROUP BY e.idEmploye ORDER BY SUM(c.scoreEpargne) DESC")
    List<Employe> findCollecteursOrderByTotalClientScoreDesc();

    List<Employe> findByAgenceZoneIdAgenceAndTypeEmploye(Integer idAgence, TypeEmploye typeEmploye);

    long countByTypeEmploye(TypeEmploye typeEmploye);

    long countByTypeEmployeAndAgenceZone_IdAgence(TypeEmploye typeEmploye, Integer idAgence);
}