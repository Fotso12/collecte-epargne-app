package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.UtilisateurCreationRequestDto;
import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;
import com.collecte_epargne.collecte_epargne.services.implementations.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/utilisateurs")
public class UtilisateurController {

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
        try {
            // Le DTO de requête a été validé par @Valid (login, idRole, nom, etc. non nulls).
            String password = creationRequestDto.getPassword();

            // 1. Créer le DTO standard (sans password, mais avec tous les champs de l'entité)
            // Le constructeur par défaut (AllArgsConstructor) du DTO standard a 10 arguments.
            // Utiliser les setters pour éviter les problèmes de signature du constructeur.
            UtilisateurDto utilisateurDto = new UtilisateurDto();
            utilisateurDto.setLogin(creationRequestDto.getLogin());
            utilisateurDto.setIdRole(creationRequestDto.getIdRole());
            utilisateurDto.setNom(creationRequestDto.getNom());
            utilisateurDto.setPrenom(creationRequestDto.getPrenom());
            utilisateurDto.setTelephone(creationRequestDto.getTelephone());
            utilisateurDto.setEmail(creationRequestDto.getEmail());
            utilisateurDto.setStatut(creationRequestDto.getStatut());

            // Note: idEmploye et codeClient sont laissés à null pour la création

            // 2. Le service hache le mot de passe et sauve l'entité.
            return new ResponseEntity<>(utilisateurService.save(utilisateurDto, password), HttpStatus.CREATED);
        } catch (Exception e) {
            // Pour le débogage, vous pouvez logger l'erreur réelle (e.printStackTrace())
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<UtilisateurDto>> getAll() {
        try {
            return new ResponseEntity<>(utilisateurService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des utilisateurs", e);
        }
    }

    @GetMapping("/{login}")
    public ResponseEntity<?> getByLogin(@PathVariable String login) {
        try {
            return new ResponseEntity<>(utilisateurService.getByLogin(login), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint de mise à jour (hors mot de passe).
     * Utilise UtilisateurDto (qui n'a pas le mot de passe).
     */
    @PutMapping("/{login}")
    public ResponseEntity<?> update(@PathVariable String login, @Valid @RequestBody UtilisateurDto utilisateurDto) {
        try {
            return new ResponseEntity<>(utilisateurService.update(login, utilisateurDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint spécifique pour la mise à jour du mot de passe
    @PutMapping("/{login}/password")
    public ResponseEntity<?> updatePassword(@PathVariable String login, @RequestBody Map<String, String> payload) {
        try {
            String newPassword = payload.get("newPassword");
            if (newPassword == null) {
                return new ResponseEntity<>("Le nouveau mot de passe est requis.", HttpStatus.BAD_REQUEST);
            }
            utilisateurService.updatePassword(login, newPassword);
            return new ResponseEntity<>("Mot de passe mis à jour avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{login}")
    public ResponseEntity<?> delete(@PathVariable String login) {
        try {
            utilisateurService.delete(login);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}