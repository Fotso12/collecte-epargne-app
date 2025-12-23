package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.services.implementations.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ClientDto clientDto) {
        try {
            return new ResponseEntity<>(clientService.save(clientDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<ClientDto>> getAll() {
        try {
            return new ResponseEntity<>(clientService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des clients", e);
        }
    }

    @GetMapping("/{codeClient}")
    public ResponseEntity<?> getById(@PathVariable String codeClient) {
        try {
            return new ResponseEntity<>(clientService.getById(codeClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/numero/{numeroClient}")
    public ResponseEntity<?> getByNumeroClient(@PathVariable String numeroClient) {
        try {
            return new ResponseEntity<>(clientService.getByNumeroClient(numeroClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{codeClient}")
    public ResponseEntity<?> update(@PathVariable String codeClient, @RequestBody ClientDto clientDto) {
        try {
            return new ResponseEntity<>(clientService.update(codeClient, clientDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{codeClient}")
    public ResponseEntity<?> delete(@PathVariable String codeClient) {
        try {
            clientService.delete(codeClient);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}