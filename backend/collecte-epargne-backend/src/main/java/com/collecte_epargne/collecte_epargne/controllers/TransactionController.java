package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.services.implementations.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> create(@RequestBody TransactionDto dto) {
        return new ResponseEntity<>(transactionService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @PutMapping("/{id}/valider-caissier/{idCaissier}")
    public ResponseEntity<TransactionDto> validerParCaissier(
            @PathVariable String id,
            @PathVariable String idCaissier
    ) {
        return ResponseEntity.ok(
                transactionService.validerParCaissier(id, idCaissier)
        );
    }

    @PutMapping("/{id}/valider-superviseur/{idSuperviseur}")
    public ResponseEntity<TransactionDto> validerParSuperviseur(
            @PathVariable String id,
            @PathVariable String idSuperviseur
    ) {
        return ResponseEntity.ok(
                transactionService.validerParSuperviseur(id, idSuperviseur)
        );
    }

    @PutMapping("/{id}/rejeter")
    public ResponseEntity<Void> rejeter(
            @PathVariable String id,
            @RequestParam String motif
    ) {
        transactionService.rejeterTransaction(id, motif);
        return ResponseEntity.noContent().build();
    }
}