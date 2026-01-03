package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.RapportCollecteurDto;
import com.collecte_epargne.collecte_epargne.services.interfaces.RapportCollecteurInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports-collecteurs")
public class RapportCollecteurController {

    private final RapportCollecteurInterface rapportCollecteurService;

    public RapportCollecteurController(RapportCollecteurInterface rapportCollecteurService) {
        this.rapportCollecteurService = rapportCollecteurService;
    }

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<RapportCollecteurDto> create(
            @RequestBody RapportCollecteurDto dto) {
        RapportCollecteurDto created = rapportCollecteurService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<RapportCollecteurDto>> getAll() {
        return ResponseEntity.ok(rapportCollecteurService.getAll());
    }

    // 🔹 GET BY ID RAPPORT
    @GetMapping("/{idRapport}")
    public ResponseEntity<RapportCollecteurDto> getById(
            @PathVariable String idRapport) {
        return ResponseEntity.ok(
                rapportCollecteurService.getById(idRapport));
    }

    // 🔹 UPDATE
    @PutMapping("/{idRapport}")
    public ResponseEntity<RapportCollecteurDto> update(
            @PathVariable String idRapport,
            @RequestBody RapportCollecteurDto dto) {

        RapportCollecteurDto updated =
                rapportCollecteurService.update(idRapport, dto);

        return ResponseEntity.ok(updated);
    }

    // 🔹 DELETE
    @DeleteMapping("/{idRapport}")
    public ResponseEntity<Void> delete(
            @PathVariable String idRapport) {

        rapportCollecteurService.delete(idRapport);
        return ResponseEntity.noContent().build();
    }
}
