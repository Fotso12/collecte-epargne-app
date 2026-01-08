package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.services.implementations.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> create(@RequestBody TransactionDto dto) {
        log.info("Création d'une transaction demandée : {}", dto.getIdTransaction());
        return new ResponseEntity<>(transactionService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAll() {
        log.info("Récupération de toutes les transactions");
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getById(@PathVariable String id) {
        log.info("Récupération transaction ID={}", id);
        return ResponseEntity.ok(transactionService.getById(id));
    }

    // Le pattern :{idCaissier:.+} permet de lire l'email complet avec ses points
    @PutMapping("/{id}/valider-caissier/{idCaissier:.+}")
    @PreAuthorize("hasAnyAuthority('CAISSIER', 'SUPERVISEUR', 'ROLE_CAISSIER', 'ROLE_SUPERVISEUR')")
    public ResponseEntity<TransactionDto> validerParCaissier(
            @PathVariable String id,
            @PathVariable String idCaissier
    ) {
        log.info("Validation caissier : transaction={}, caissier={}", id, idCaissier);
        return ResponseEntity.ok(transactionService.validerParCaissier(id, idCaissier));
    }

    // Modifiez l'annotation PreAuthorize comme ceci :
    @PutMapping("/{id}/valider-superviseur/{idSuperviseur:.+}")
    @PreAuthorize("hasAnyRole('SUPERVISEUR', 'ADMIN') or hasAnyAuthority('SUPERVISEUR', 'ROLE_SUPERVISEUR')")
    public ResponseEntity<TransactionDto> validerParSuperviseur(
            @PathVariable String id,
            @PathVariable String idSuperviseur
    ) {
        log.info("Validation superviseur : transaction={}, superviseur={}", id, idSuperviseur);
        return ResponseEntity.ok(transactionService.validerParSuperviseur(id, idSuperviseur));
    }

    @PutMapping("/{id}/rejeter")
    public ResponseEntity<Void> rejeter(
            @PathVariable String id,
            @RequestParam String motif
    ) {
        log.warn("Rejet transaction ID={}, motif={}", id, motif);
        transactionService.rejeterTransaction(id, motif);
        return ResponseEntity.noContent().build();
    }
}