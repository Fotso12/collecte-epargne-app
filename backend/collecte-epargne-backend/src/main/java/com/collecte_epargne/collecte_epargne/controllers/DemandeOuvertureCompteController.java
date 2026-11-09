package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.DemandeOuvertureCompteDto;
import com.collecte_epargne.collecte_epargne.services.implementations.DemandeOuvertureCompteService;
import com.collecte_epargne.collecte_epargne.utils.StatutDemande;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes-ouverture")
public class DemandeOuvertureCompteController {

    private final DemandeOuvertureCompteService demandeService;

    public DemandeOuvertureCompteController(DemandeOuvertureCompteService demandeService) {
        this.demandeService = demandeService;
    }

    @PostMapping
    public ResponseEntity<?> createDemande(@Valid @RequestBody DemandeOuvertureCompteDto demandeDto) {
        try {
            return new ResponseEntity<>(demandeService.createDemande(demandeDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<DemandeOuvertureCompteDto>> getAll() {
        try {
            return new ResponseEntity<>(demandeService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/client/{codeClient}")
    public ResponseEntity<List<DemandeOuvertureCompteDto>> getByClient(@PathVariable String codeClient) {
        try {
            return new ResponseEntity<>(demandeService.getByClient(codeClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<DemandeOuvertureCompteDto>> getByStatut(@PathVariable String statut) {
        try {
            StatutDemande statutEnum = StatutDemande.valueOf(statut.toUpperCase());
            return new ResponseEntity<>(demandeService.getByStatut(statutEnum), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{idDemande}")
    public ResponseEntity<?> getById(@PathVariable Long idDemande) {
        try {
            return new ResponseEntity<>(demandeService.getById(idDemande), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{idDemande}/valider")
    public ResponseEntity<?> validerDemande(
            @PathVariable Long idDemande,
            @RequestBody Map<String, String> request) {
        try {
            String loginSuperviseur = request.get("loginSuperviseur");
            if (loginSuperviseur == null || loginSuperviseur.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le login du superviseur est requis"));
            }
            return new ResponseEntity<>(
                    demandeService.validerDemande(idDemande, loginSuperviseur, null),
                    HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{idDemande}/rejeter")
    public ResponseEntity<?> rejeterDemande(
            @PathVariable Long idDemande,
            @RequestBody Map<String, String> request) {
        try {
            String loginSuperviseur = request.get("loginSuperviseur");
            String motifRejet = request.get("motifRejet");
            
            if (loginSuperviseur == null || loginSuperviseur.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le login du superviseur est requis"));
            }
            
            return new ResponseEntity<>(
                    demandeService.rejeterDemande(idDemande, loginSuperviseur, motifRejet),
                    HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

