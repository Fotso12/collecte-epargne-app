package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.UtilisateurCreationRequestDto;
import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;
import com.collecte_epargne.collecte_epargne.services.implementations.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/utilisateurs")
public class UtilisateurController {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurController.class);

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Endpoint de création d'utilisateur.
     * Utilise UtilisateurCreationRequestDto pour recevoir le mot de passe.
     */
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody UtilisateurCreationRequestDto creationRequestDto) {
        logger.info("Création d'utilisateur avec login: {}", creationRequestDto.getLogin());
        try {
            UtilisateurDto utilisateurDto = new UtilisateurDto();
            utilisateurDto.setLogin(creationRequestDto.getLogin());
            utilisateurDto.setIdRole(creationRequestDto.getIdRole());
            utilisateurDto.setNom(creationRequestDto.getNom());
            utilisateurDto.setPrenom(creationRequestDto.getPrenom());
            utilisateurDto.setTelephone(creationRequestDto.getTelephone());
            utilisateurDto.setEmail(creationRequestDto.getEmail());
            utilisateurDto.setStatut(creationRequestDto.getStatut());

            return new ResponseEntity<>(utilisateurService.save(utilisateurDto, creationRequestDto.getPassword()), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de la création d'utilisateur: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<UtilisateurDto>> getAll() {
        logger.info("Récupération de tous les utilisateurs");
        try {
            return new ResponseEntity<>(utilisateurService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de tous les utilisateurs: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des utilisateurs", e);
        }
    }

    @GetMapping("/{login}")
    public ResponseEntity<?> getByLogin(@PathVariable String login) {
        logger.info("Récupération d'utilisateur avec login: {}", login);
        try {
            return new ResponseEntity<>(utilisateurService.getByLogin(login), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération d'utilisateur avec login {}: {}", login, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{login}")
    public ResponseEntity<?> update(@PathVariable String login, @Valid @RequestBody UtilisateurDto utilisateurDto) {
        logger.info("Mise à jour d'utilisateur avec login: {}", login);
        try {
            return new ResponseEntity<>(utilisateurService.update(login, utilisateurDto), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour d'utilisateur avec login {}: {}", login, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{login}/password")
    public ResponseEntity<?> updatePassword(@PathVariable String login, @RequestBody Map<String, String> payload) {
        logger.info("Mise à jour du mot de passe pour utilisateur avec login: {}", login);
        try {
            String newPassword = payload.get("newPassword");
            if (newPassword == null) {
                return new ResponseEntity<>("Le nouveau mot de passe est requis.", HttpStatus.BAD_REQUEST);
            }
            utilisateurService.updatePassword(login, newPassword);
            return new ResponseEntity<>("Mot de passe mis à jour avec succès", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du mot de passe pour utilisateur avec login {}: {}", login, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{login}")
    public ResponseEntity<?> delete(@PathVariable String login) {
        logger.info("Suppression d'utilisateur avec login: {}", login);
        try {
            utilisateurService.delete(login);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression d'utilisateur avec login {}: {}", login, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{login}/statut")
    public ResponseEntity<UtilisateurDto> updateStatut(@PathVariable String login, @RequestBody Map<String, String> payload) {
        String nouveauStatut = payload.get("statut");
        if (nouveauStatut == null) {
            return ResponseEntity.badRequest().build();
        }
        UtilisateurDto updated = utilisateurService.updateStatut(login, nouveauStatut);
        return ResponseEntity.ok(updated);
    }

    @PutMapping(value = "/{login}/photo", consumes = "multipart/form-data")
    public ResponseEntity<UtilisateurDto> updatePhoto(@PathVariable String login, @RequestParam("photo") MultipartFile photo) {
        logger.info("Mise à jour de la photo pour utilisateur : {}", login);
        try {
            UtilisateurDto updated = utilisateurService.updatePhoto(login, photo);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la photo pour utilisateur {}: {}", login, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}