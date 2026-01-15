package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.services.implementations.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/clients")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // --- NOUVELLE MÉTHODE : CRÉATION AVEC FICHIERS ---
    @PostMapping(value = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createWithFiles(
            @RequestPart("client") String clientJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "cniRecto", required = false) MultipartFile cniRecto,
            @RequestPart(value = "cniVerso", required = false) MultipartFile cniVerso) {

        log.info("POST /api/clients/with-files - Création client avec images");
        try {
            // Conversion du JSON String en DTO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ClientDto clientDto = objectMapper.readValue(clientJson, ClientDto.class);

            return new ResponseEntity<>(
                    clientService.saveWithFiles(clientDto, photo, cniRecto, cniVerso),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            log.error("Erreur création client avec fichiers", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // --- NOUVELLE MÉTHODE : MISE À JOUR AVEC FICHIERS ---
    @PutMapping(value = "/{codeClient}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateWithFiles(
            @PathVariable String codeClient,
            @RequestPart("client") String clientJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "cniRecto", required = false) MultipartFile cniRecto,
            @RequestPart(value = "cniVerso", required = false) MultipartFile cniVerso) {

        log.info("PUT /api/clients/{}/with-files - Mise à jour client avec images", codeClient);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ClientDto clientDto = objectMapper.readValue(clientJson, ClientDto.class);

            return new ResponseEntity<>(
                    clientService.updateWithFiles(codeClient, clientDto, photo, cniRecto, cniVerso),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Erreur mise à jour client avec fichiers", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- NOUVELLE MÉTHODE : IMPORTATION CSV ---
    @PostMapping("/import")
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/clients/import - Importation CSV");
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Le fichier est vide", HttpStatus.BAD_REQUEST);
            }
            java.util.Map<String, Integer> stats = clientService.importClientsFromCSV(file);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de l'importation CSV", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            // Using getByCodeClient since the path variable is codeClient
            return new ResponseEntity<>(clientService.getByCodeClient(codeClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getByNumeroClient(@PathVariable String numeroClient) {
        try {
            return new ResponseEntity<>(clientService.getByNumeroClient(Long.parseLong(numeroClient)), HttpStatus.OK);
        } catch (NumberFormatException e) {
             return new ResponseEntity<>("Numéro client invalide", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<?> getByLogin(@PathVariable String login) {
        try {
            return new ResponseEntity<>(clientService.getByLogin(login), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{codeClient}")
    public ResponseEntity<?> update(@PathVariable String codeClient, @RequestBody ClientDto clientDto) {
        try {
            return new ResponseEntity<>(clientService.updateByCodeClient(codeClient, clientDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{codeClient}")
    public ResponseEntity<?> delete(@PathVariable String codeClient) {
        try {
            clientService.deleteByCodeClient(codeClient);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/agence/{idAgence}")
    public ResponseEntity<List<ClientDto>> getClientsByAgence(@PathVariable Integer idAgence) {
        return new ResponseEntity<>(clientService.getClientsByAgence(idAgence), HttpStatus.OK);
    }
}