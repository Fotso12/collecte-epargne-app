package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.services.implementations.PdfService;
import com.collecte_epargne.collecte_epargne.services.implementations.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final PdfService pdfService;

    public TransactionController(TransactionService transactionService, PdfService pdfService) {
        this.transactionService = transactionService;
        this.pdfService = pdfService;
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

    @GetMapping("/{id}/recu")
    public ResponseEntity<InputStreamResource> generateReceipt(@PathVariable String id) {
        log.info("Génération du reçu pour la transaction : {}", id);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recu-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(transactionService.generateReceipt(id)));
    }

    @GetMapping("/agence/{idAgence}")
    public ResponseEntity<List<TransactionDto>> getByAgence(@PathVariable Integer idAgence) {
        log.info("Récupération des transactions pour l'agence ID={}", idAgence);
        return ResponseEntity.ok(transactionService.getTransactionsByAgence(idAgence));
    }

    @GetMapping("/agence/{idAgence}/a-valider")
    public ResponseEntity<List<TransactionDto>> getAValiderByAgence(@PathVariable Integer idAgence) {
        log.info("Récupération des transactions à valider pour l'agence ID={}", idAgence);
        return ResponseEntity.ok(transactionService.getTransactionsAValiderByAgence(idAgence));
    }
}