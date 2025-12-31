package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.RoleDto;
import com.collecte_epargne.collecte_epargne.services.implementations.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody RoleDto roleDto) {
        logger.info("Création de rôle avec code: {}", roleDto.getCode());
        try {
            return new ResponseEntity<>(roleService.save(roleDto), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de rôle: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<RoleDto>> getAll() {
        logger.info("Récupération de tous les rôles");
        try {
            return new ResponseEntity<>(roleService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de tous les rôles: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des rôles", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        logger.info("Récupération de rôle avec id: {}", id);
        try {
            return new ResponseEntity<>(roleService.getById(id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de rôle avec id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody RoleDto roleDto) {
        logger.info("Mise à jour de rôle avec id: {}", id);
        try {
            return new ResponseEntity<>(roleService.update(id, roleDto), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de rôle avec id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            roleService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Un code 400 est approprié si la suppression est bloquée par la logique métier (rôle utilisé)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}