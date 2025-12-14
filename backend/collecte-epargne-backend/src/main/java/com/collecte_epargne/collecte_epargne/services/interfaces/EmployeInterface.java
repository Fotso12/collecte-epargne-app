package com.collecte_epargne.collecte_epargne.services.interfaces;



import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;

import java.util.List;

public interface EmployeInterface {

    // --- CRUD Générique (Utilisé en interne par le CRUD Spécialisé) ---
    EmployeDto save(EmployeDto employeDto);
    EmployeDto getById(String matricule);
    EmployeDto update(String matricule, EmployeDto employeDto);
    void delete(String matricule);
    List<EmployeDto> getAll();

    // --- CRUD Spécialisé (Listage par Rôle) ---
    List<EmployeDto> getSuperviseurs();
    List<EmployeDto> getCaissiers();
    List<EmployeDto> getCollecteurs();

    // --- Fonctions de Listage et Tri Spécialisé (Collecteurs) ---

    /**
     * Liste les collecteurs triés par le nombre de clients assignés.
     */
    List<EmployeDto> getCollecteursOrderedByClientCount();

    /**
     * Liste les collecteurs triés par le score d'épargne total de leurs clients.
     */
    List<EmployeDto> getCollecteursOrderedByTotalClientScore();

    // --- Fonctions de Relations ---

    /**
     * Récupère tous les collecteurs sous la supervision d'un superviseur donné.
     */
    List<EmployeDto> getCollecteursBySuperviseur(String idSuperviseur);

    /**
     * Liste tous les clients assignés à un collecteur spécifique.
     */
    List<ClientDto> getClientsByCollecteur(String idCollecteur);
}