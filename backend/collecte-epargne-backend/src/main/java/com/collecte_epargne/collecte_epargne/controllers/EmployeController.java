package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.services.implementations.EmployeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/employes")
public class EmployeController {

    private final EmployeService employeService;

    public EmployeController(EmployeService employeService) {
        this.employeService = employeService;
    }

    // ----------------------------------------------------------------------
    // CRUD Générique (Base pour tout Employe)
    // ----------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<?> save(@RequestBody EmployeDto employeDto) {
        try {
            return new ResponseEntity<>(employeService.save(employeDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<EmployeDto>> getAll() {
        try {
            return new ResponseEntity<>(employeService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des employés", e);
        }
    }

    @GetMapping("/{matricule}")
    public ResponseEntity<?> getById(@PathVariable String matricule) {
        try {
            return new ResponseEntity<>(employeService.getById(matricule), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{matricule}")
    public ResponseEntity<?> update(@PathVariable String matricule, @RequestBody EmployeDto employeDto) {
        try {
            return new ResponseEntity<>(employeService.update(matricule, employeDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{matricule}")
    public ResponseEntity<?> delete(@PathVariable String matricule) {
        try {
            employeService.delete(matricule);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // ----------------------------------------------------------------------
    // Listage Spécialisé (CRUD par Rôle)
    // ----------------------------------------------------------------------

    @GetMapping("/superviseurs")
    public ResponseEntity<List<EmployeDto>> getSuperviseurs() {
        return new ResponseEntity<>(employeService.getSuperviseurs(), HttpStatus.OK);
    }

    @GetMapping("/caissiers")
    public ResponseEntity<List<EmployeDto>> getCaissiers() {
        return new ResponseEntity<>(employeService.getCaissiers(), HttpStatus.OK);
    }

    @GetMapping("/collecteurs")
    public ResponseEntity<List<EmployeDto>> getCollecteurs() {
        return new ResponseEntity<>(employeService.getCollecteurs(), HttpStatus.OK);
    }

    // ----------------------------------------------------------------------
    // Relations et Métriques Collecteur
    // ----------------------------------------------------------------------

    @GetMapping("/{idSuperviseur}/collecteurs")
    public ResponseEntity<?> getCollecteursBySuperviseur(@PathVariable String idSuperviseur) {
        try {
            return new ResponseEntity<>(employeService.getCollecteursBySuperviseur(idSuperviseur), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/collecteurs/{matricule}/clients")
    public ResponseEntity<List<ClientDto>> getClientsByCollecteur(@PathVariable("matricule") String matricule) {
        try {
            // On passe le matricule reçu de l'URL au service
            return new ResponseEntity<>(employeService.getClientsByCollecteur(matricule), HttpStatus.OK);
        } catch (Exception e) {
            // C'est ici que l'erreur 400 est générée si le matricule n'est pas trouvé ou invalide
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // --- Fonctions de Tri ---

    /** Liste les collecteurs du plus au moins performant (par nombre de clients) */
    @GetMapping("/collecteurs/performance/client-count")
    public ResponseEntity<List<EmployeDto>> getCollecteursByClientCount() {
        return new ResponseEntity<>(employeService.getCollecteursOrderedByClientCount(), HttpStatus.OK);
    }

    /** Liste les collecteurs du plus au moins performant (par score d'épargne total) */
    @GetMapping("/collecteurs/performance/score-total")
    public ResponseEntity<List<EmployeDto>> getCollecteursByTotalClientScore() {
        return new ResponseEntity<>(employeService.getCollecteursOrderedByTotalClientScore(), HttpStatus.OK);
    }


}