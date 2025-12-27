package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.services.implementations.ClientService;
import com.collecte_epargne.collecte_epargne.services.interfaces.ClientInterface;
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

    @GetMapping("/{numeroClient}")
    public ResponseEntity<?> getById(@PathVariable Long numeroClient) {
        try {
            return new ResponseEntity<>(clientService.getById(numeroClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/numero/{numeroClient}")
    public ResponseEntity<?> getByNumeroClient(@PathVariable Long numeroClient) {
        try {
            return new ResponseEntity<>(clientService.getByNumeroClient(numeroClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/code/{codeClient}")
    public ResponseEntity<?> getByCodeClient(@PathVariable String codeClient) {
        try {
            return new ResponseEntity<>(clientService.getByCodeClient(codeClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{numeroClient}")
    public ResponseEntity<?> update(@PathVariable Long numeroClient, @RequestBody ClientDto clientDto) {
        try {
            return new ResponseEntity<>(clientService.update(numeroClient, clientDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/code/{codeClient}")
    public ResponseEntity<?> updateByCodeClient(@PathVariable String codeClient, @RequestBody ClientDto clientDto) {
        try {
            // Appel de votre nouvelle méthode service
            return new ResponseEntity<>(clientService.updateByCodeClient(codeClient, clientDto),  HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/code/{codeClient}")
    public ResponseEntity<?> deleteByCodeClient(@PathVariable String codeClient) {
        try {
            clientService.deleteByCodeClient(codeClient);
            return new ResponseEntity<>("Client supprimé avec succès", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{numeroClient}")
    public ResponseEntity<?> delete(@PathVariable Long numeroClient) {
        try {
            clientService.delete(numeroClient);
            return new ResponseEntity<>("Client supprimé avec succès", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}