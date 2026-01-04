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

    // --- NOUVELLE MÉTHODE : IMPORTATION CSV ---
    @PostMapping("/import")
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/clients/import - Importation CSV");
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Le fichier est vide", HttpStatus.BAD_REQUEST);
            }
            clientService.importClientsFromCSV(file);
            return new ResponseEntity<>("Importation réussie", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de l'importation CSV", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ClientDto clientDto) {
        log.info("POST /api/clients - Création client");

        try {
            return new ResponseEntity<>(clientService.save(clientDto), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur création client", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAll() {
        log.info("GET /api/clients - Liste des clients");

        try {
            return new ResponseEntity<>(clientService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur récupération liste clients", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération des clients",
                    e
            );
        }
    }

    @GetMapping("/{numeroClient}")
    public ResponseEntity<?> getById(@PathVariable Long numeroClient) {
        log.info("GET /api/clients/{}", numeroClient);

        try {
            return new ResponseEntity<>(clientService.getById(numeroClient), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Client non trouvé avec numero={}", numeroClient, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/numero/{numeroClient}")
    public ResponseEntity<?> getByNumeroClient(@PathVariable Long numeroClient) {
        log.info("GET /api/clients/numero/{}", numeroClient);

        try {
            return new ResponseEntity<>(clientService.getByNumeroClient(numeroClient), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Client non trouvé avec numero={}", numeroClient, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/code/{codeClient}")
    public ResponseEntity<?> getByCodeClient(@PathVariable String codeClient) {
        log.info("GET /api/clients/code/{}", codeClient);

        try {
            return new ResponseEntity<>(clientService.getByCodeClient(codeClient), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Client non trouvé avec code={}", codeClient, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{numeroClient}")
    public ResponseEntity<?> update(@PathVariable Long numeroClient,
                                    @RequestBody ClientDto clientDto) {
        log.info("PUT /api/clients/{} - Mise à jour client", numeroClient);

        try {
            return new ResponseEntity<>(clientService.update(numeroClient, clientDto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur mise à jour client numero={}", numeroClient, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/code/{codeClient}")
    public ResponseEntity<?> updateByCodeClient(@PathVariable String codeClient,
                                                @RequestBody ClientDto clientDto) {
        log.info("PUT /api/clients/code/{} - Mise à jour client", codeClient);

        try {
            return new ResponseEntity<>(
                    clientService.updateByCodeClient(codeClient, clientDto),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Erreur mise à jour client code={}", codeClient, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/code/{codeClient}")
    public ResponseEntity<?> deleteByCodeClient(@PathVariable String codeClient) {
        log.info("DELETE /api/clients/code/{}", codeClient);

        try {
            clientService.deleteByCodeClient(codeClient);
            return new ResponseEntity<>("Client supprimé avec succès", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Erreur suppression client code={}", codeClient, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{numeroClient}")
    public ResponseEntity<?> delete(@PathVariable Long numeroClient) {
        log.info("DELETE /api/clients/{}", numeroClient);

        try {
            clientService.delete(numeroClient);
            return new ResponseEntity<>("Client supprimé avec succès", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Erreur suppression client numero={}", numeroClient, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}