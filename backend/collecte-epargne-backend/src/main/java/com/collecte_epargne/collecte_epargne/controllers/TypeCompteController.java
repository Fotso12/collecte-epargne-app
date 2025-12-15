package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.TypeCompteDto;
import com.collecte_epargne.collecte_epargne.services.implementations.TypeCompteService;
import com.collecte_epargne.collecte_epargne.services.interfaces.TypeCompteInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/type-comptes")
public class TypeCompteController {

    private final TypeCompteService typeCompteService;

    public TypeCompteController(TypeCompteService typeCompteService) {
        this.typeCompteService = typeCompteService;
    }

    @PostMapping
    public ResponseEntity<TypeCompteDto> save(@RequestBody TypeCompteDto typeCompteDto) {
        try {
            return new ResponseEntity<>(typeCompteService.save(typeCompteDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<TypeCompteDto>> getAll() {
        try {
            return new ResponseEntity<>(typeCompteService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des types de compte", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeCompteDto> getById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(typeCompteService.getById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TypeCompteDto> getByCode(@PathVariable String code) {
        try {
            return new ResponseEntity<>(typeCompteService.getByCode(code), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeCompteDto> update(@PathVariable Integer id, @RequestBody TypeCompteDto typeCompteDto) {
        try {
            return new ResponseEntity<>(typeCompteService.update(id, typeCompteDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            typeCompteService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
