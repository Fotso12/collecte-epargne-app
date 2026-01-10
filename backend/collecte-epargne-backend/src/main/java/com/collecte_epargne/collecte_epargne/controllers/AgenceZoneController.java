package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;
import com.collecte_epargne.collecte_epargne.services.implementations.AgenceZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/AgenceZone")
public class AgenceZoneController {

    private static final Logger log = LoggerFactory.getLogger(AgenceZoneController.class);

    private final AgenceZoneService agenceZoneService;

    public AgenceZoneController(AgenceZoneService agenceZoneService) {
        this.agenceZoneService = agenceZoneService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody AgenceZoneDto agenceZoneDto) {
        log.info("Requête POST /api/AgenceZone : {}", agenceZoneDto);
        log.info("Position received: {}", agenceZoneDto.getPosition());
        try {
            return new ResponseEntity<>(agenceZoneService.save(agenceZoneDto), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur création AgenceZone", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<AgenceZoneDto>> getAll() {
        log.info("Requête GET /api/AgenceZone");
        try {
            return new ResponseEntity<>(agenceZoneService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur récupération AgenceZone", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{idAgence}")
    public ResponseEntity<?> show(@PathVariable Integer idAgence) {
        log.info("Requête GET /api/AgenceZone/{}", idAgence);
        try {
            return new ResponseEntity<>(agenceZoneService.getById(idAgence), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur récupération AgenceZone id={}", idAgence, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idAgence}")
    public ResponseEntity<?> update(@PathVariable Integer idAgence, @RequestBody AgenceZoneDto agenceZoneDto) {
        log.info("Requête PUT /api/AgenceZone/{}", idAgence);
        log.info("Update Payload Position: {}", agenceZoneDto.getPosition());
        try {
            return new ResponseEntity<>(agenceZoneService.update(idAgence, agenceZoneDto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur mise à jour AgenceZone id={}", idAgence, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{idAgence}")
    public ResponseEntity<?> delete(@PathVariable Integer idAgence) {
        log.info("Requête DELETE /api/AgenceZone/{}", idAgence);
        try {
            agenceZoneService.delete(idAgence);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur suppression AgenceZone id={}", idAgence, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
