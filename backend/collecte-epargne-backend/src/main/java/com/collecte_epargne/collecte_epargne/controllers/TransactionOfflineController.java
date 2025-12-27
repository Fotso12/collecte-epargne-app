package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionOfflineInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions-offline")
public class TransactionOfflineController {

    private final TransactionOfflineInterface service;

    public TransactionOfflineController(TransactionOfflineInterface service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransactionOfflineDto> create(
            @RequestBody TransactionOfflineDto dto
    ) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionOfflineDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TransactionOfflineDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<TransactionOfflineDto>> getByStatut(
            @PathVariable StatutSynchroOffline statut
    ) {
        return ResponseEntity.ok(service.getByStatutSynchro(statut));
    }

    @GetMapping("/employe/{idEmploye}")
    public ResponseEntity<List<TransactionOfflineDto>> getByEmploye(
            @PathVariable Integer idEmploye
    ) {
        return ResponseEntity.ok(service.getByEmploye(idEmploye));
    }

    @PutMapping("/{id}/synchroniser/{idTransaction}")
    public ResponseEntity<TransactionOfflineDto> synchroniser(
            @PathVariable String id,
            @PathVariable String idTransaction
    ) {
        return ResponseEntity.ok(
                service.markAsSynced(id, idTransaction)
        );
    }
}