package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionOfflineInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions-offline")
public class TransactionOfflineController {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionOfflineController.class);

    private final TransactionOfflineInterface service;

    public TransactionOfflineController(TransactionOfflineInterface service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // Création transaction offline
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<TransactionOfflineDto> create(
            @RequestBody TransactionOfflineDto dto
    ) {
        log.info("Création transaction offline demandée : idOffline={}", dto.getIdOffline());
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    // ----------------------------------------------------
    // Récupération par ID
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<TransactionOfflineDto> getById(@PathVariable String id) {
        log.info("Récupération transaction offline ID={}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    // ----------------------------------------------------
    // Liste de toutes les transactions offline
    // ----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<TransactionOfflineDto>> getAll() {
        log.info("Récupération de toutes les transactions offline");
        return ResponseEntity.ok(service.getAll());
    }

    // ----------------------------------------------------
    // Filtrer par statut de synchronisation
    // ----------------------------------------------------
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<TransactionOfflineDto>> getByStatut(
            @PathVariable StatutSynchroOffline statut
    ) {
        log.info("Récupération transactions offline par statut={}", statut);
        return ResponseEntity.ok(service.getByStatutSynchro(statut));
    }

    // ----------------------------------------------------
    // Filtrer par employé
    // ----------------------------------------------------
    @GetMapping("/employe/{idEmploye}")
    public ResponseEntity<List<TransactionOfflineDto>> getByEmploye(
            @PathVariable Integer idEmploye
    ) {
        log.info("Récupération transactions offline par employé ID={}", idEmploye);
        return ResponseEntity.ok(service.getByEmploye(idEmploye));
    }

    // ----------------------------------------------------
    // Synchronisation avec transaction finale
    // ----------------------------------------------------
    @PutMapping("/{id}/synchroniser/{idTransaction}")
    public ResponseEntity<TransactionOfflineDto> synchroniser(
            @PathVariable String id,
            @PathVariable String idTransaction
    ) {
        log.info(
                "Synchronisation transaction offline={} avec transaction finale={}",
                id, idTransaction
        );
        return ResponseEntity.ok(service.markAsSynced(id, idTransaction));
    }
}
