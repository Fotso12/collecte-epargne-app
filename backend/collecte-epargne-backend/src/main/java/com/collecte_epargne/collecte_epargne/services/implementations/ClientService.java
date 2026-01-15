package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.ClientMapper;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.ClientInterface;
import com.collecte_epargne.collecte_epargne.utils.CodeGenerator;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClientService implements ClientInterface {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final EmployeRepository employeRepository;
    private final CodeGenerator codeGenerator;
    private final FileStorageService fileStorageService;
    private final com.collecte_epargne.collecte_epargne.repositories.RoleRepository roleRepository;
    private final com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository agenceZoneRepository;

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    public ClientService(ClientRepository clientRepository,
                         ClientMapper clientMapper,
                         UtilisateurRepository utilisateurRepository,
                         EmployeRepository employeRepository,
                         CodeGenerator codeGenerator,
                         FileStorageService fileStorageService,
                         com.collecte_epargne.collecte_epargne.repositories.RoleRepository roleRepository,
                         com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository agenceZoneRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.employeRepository = employeRepository;
        this.codeGenerator = codeGenerator;
        this.fileStorageService = fileStorageService;
        this.roleRepository = roleRepository;
        this.agenceZoneRepository = agenceZoneRepository;
    }

    private void assignerRelations(Client client, ClientDto dto) {
        // 1. Liaison avec l'Utilisateur
        if (dto.getLoginUtilisateur() != null) {
            String login = dto.getLoginUtilisateur();
            Utilisateur utilisateur = utilisateurRepository.findById(login)
                    .orElseThrow(() -> {
                        log.error("Erreur assignation : Utilisateur non trouvé pour le login : {}", login);
                        return new RuntimeException("Utilisateur non trouvé avec le login : " + login);
                    });
            client.setUtilisateur(utilisateur);
        }

        // 2. Liaison avec le Collecteur Assigné
        if (dto.getCodeCollecteurAssigne() != null && !dto.getCodeCollecteurAssigne().isEmpty()) {
            try {
                Integer idCollecteur = Integer.parseInt(dto.getCodeCollecteurAssigne());
                Employe collecteur = employeRepository.findById(idCollecteur)
                        .orElseThrow(() -> {
                            log.error("Erreur assignation : Collecteur non trouvé avec l'ID : {}", idCollecteur);
                            return new RuntimeException("Collecteur non trouvé avec l'ID : " + idCollecteur);
                        });

                if (collecteur.getTypeEmploye() != TypeEmploye.COLLECTEUR) {
                    log.warn("L'employé {} n'est pas un COLLECTEUR", idCollecteur);
                    throw new IllegalArgumentException("L'employé assigné doit être de type COLLECTEUR.");
                }

                client.setCollecteurAssigne(collecteur);
            } catch (NumberFormatException e) {
                log.error("Format d'ID collecteur invalide : {}", dto.getCodeCollecteurAssigne());
                throw new IllegalArgumentException("L'ID du collecteur doit être un nombre valide.");
            }
        }
        // Si codeCollecteurAssigne n'est pas fourni, on ne modifie pas le collecteur existant
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {
        log.info("Tentative de sauvegarde d'un nouveau client");
        Objects.requireNonNull(clientDto, "clientDto ne doit pas être null");

        if (clientDto.getNumeroClient() != null && clientRepository.findByNumeroClient(clientDto.getNumeroClient()).isPresent()) {
            log.warn("Échec sauvegarde : Le numéro client {} existe déjà", clientDto.getNumeroClient());
            throw new RuntimeException("Un client avec ce numéro client existe déjà.");
        }

        // Vérification de la couverture de la ville
        if (clientDto.getVille() != null && !agenceZoneRepository.existsByVille(clientDto.getVille())) {
            log.warn("Tentative d'inscription dans une ville non couverte : {}", clientDto.getVille());
            throw new RuntimeException("Votre ville (" + clientDto.getVille() + ") n'est pas encore prise en compte par nos services.");
        }

        Client clientToSave = clientMapper.toEntity(clientDto);
        assignerRelations(clientToSave, clientDto);

        if (clientToSave.getCodeClient() == null || clientToSave.getCodeClient().isEmpty()) {
            String generatedCode = codeGenerator.generateClientCode();
            clientToSave.setCodeClient(generatedCode);
            log.info("Code client généré automatiquement : {}", generatedCode);
        }

        Client savedClient = clientRepository.save(clientToSave);
        log.info("Client sauvegardé avec succès. NumeroClient: {}, Code: {}", savedClient.getNumeroClient(), savedClient.getCodeClient());
        return clientMapper.toDto(savedClient);
    }

    /**
     * Sauvegarde un client avec ses fichiers physiques
     */
    @Transactional
    public ClientDto saveWithFiles(ClientDto clientDto, MultipartFile photo, MultipartFile recto, MultipartFile verso) {
        if (photo != null && !photo.isEmpty()) {
            clientDto.setPhotoPath(fileStorageService.save(photo, "photos"));
        }
        if (recto != null && !recto.isEmpty()) {
            clientDto.setCniRectoPath(fileStorageService.save(recto, "cni_recto"));
        }
        if (verso != null && !verso.isEmpty()) {
            clientDto.setCniVersoPath(fileStorageService.save(verso, "cni_versos"));
        }
        return this.save(clientDto);
    }

    /**
     * Met à jour un client avec ses fichiers physiques
     */
    @Transactional
    public ClientDto updateWithFiles(String codeClient, ClientDto clientDto, MultipartFile photo, MultipartFile recto, MultipartFile verso) {
        // Charger le client existant pour préserver les chemins non mis à jour
        Client existingClient = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + codeClient));
        
        // Sauvegarder les nouveaux fichiers et mettre à jour les chemins dans le DTO
        if (photo != null && !photo.isEmpty()) {
            clientDto.setPhotoPath(fileStorageService.save(photo, "photos"));
        } else {
            // Préserver le chemin existant si aucun nouveau fichier n'est fourni
            clientDto.setPhotoPath(existingClient.getPhotoPath());
        }
        
        if (recto != null && !recto.isEmpty()) {
            clientDto.setCniRectoPath(fileStorageService.save(recto, "cni_recto"));
        } else {
            clientDto.setCniRectoPath(existingClient.getCniRectoPath());
        }
        
        if (verso != null && !verso.isEmpty()) {
            clientDto.setCniVersoPath(fileStorageService.save(verso, "cni_verso"));
        } else {
            clientDto.setCniVersoPath(existingClient.getCniVersoPath());
        }
        
        return this.updateByCodeClient(codeClient, clientDto);
    }

    @Override
    public List<ClientDto> getAll() {
        log.info("Récupération de la liste de tous les clients");
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDto getById(Long numeroClient) {
        log.info("Récupération du client avec numéro : {}", numeroClient);
        Client client = clientRepository.findById(numeroClient)
                .orElseThrow(() -> {
                    log.error("Client non trouvé avec le numéro : {}", numeroClient);
                    return new RuntimeException("Client non trouvé avec le numéro : " + numeroClient);
                });
        return clientMapper.toDto(client);
    }

    @Override
    public ClientDto getByCodeClient(String codeClient) {
        log.info("Récupération du client avec code : {}", codeClient);
        Client client = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> {
                    log.error("Client non trouvé avec le code : {}", codeClient);
                    return new RuntimeException("Client non trouvé avec le code : " + codeClient);
                });
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto update(Long numeroClient, ClientDto clientDto) {
        log.info("Mise à jour du client numéro : {}", numeroClient);
        Client existingClient = clientRepository.findById(numeroClient)
                .orElseThrow(() -> {
                    log.error("Mise à jour impossible : Client {} inexistant", numeroClient);
                    return new RuntimeException("Client non trouvé pour la mise à jour : " + numeroClient);
                });

        existingClient.setAdresse(clientDto.getAdresse());
        existingClient.setTypeCni(clientDto.getTypeCni());
        existingClient.setNumCni(clientDto.getNumCni());
        existingClient.setDateNaissance(clientDto.getDateNaissance());
        existingClient.setLieuNaissance(clientDto.getLieuNaissance());
        existingClient.setProfession(clientDto.getProfession());
        existingClient.setScoreEpargne(clientDto.getScoreEpargne());

        // On ne met à jour les chemins que s'ils sont fournis dans le DTO
        if(clientDto.getPhotoPath() != null) existingClient.setPhotoPath(clientDto.getPhotoPath());
        if(clientDto.getCniRectoPath() != null) existingClient.setCniRectoPath(clientDto.getCniRectoPath());
        if(clientDto.getCniVersoPath() != null) existingClient.setCniVersoPath(clientDto.getCniVersoPath());

        assignerRelations(existingClient, clientDto);

        Client updatedClient = clientRepository.save(existingClient);
        log.info("Client numéro {} mis à jour avec succès", numeroClient);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    @Transactional
    public void delete(Long numClient) {
        log.info("Suppression du client numéro : {}", numClient);
        Client client = clientRepository.findById(numClient)
                .orElseThrow(() -> new RuntimeException("Impossible de supprimer : Client inexistant (ID: " + numClient + ")"));

        // Suppression des fichiers physiques avant de supprimer l'entrée en base
        fileStorageService.deleteFile(client.getPhotoPath());
        fileStorageService.deleteFile(client.getCniRectoPath());
        fileStorageService.deleteFile(client.getCniVersoPath());

        clientRepository.deleteById(numClient);
        log.info("Client numéro {} supprimé avec succès", numClient);
    }

    @Override
    public ClientDto getByNumeroClient(Long numeroClient) {
        return getById(numeroClient);
    }

    @Override
    @Transactional
    public ClientDto updateByCodeClient(String codeClient, ClientDto clientDto) {
        Client existingClient = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + codeClient));
        return update(existingClient.getNumeroClient(), clientDto);
    }

    @Override
    @Transactional
    public void deleteByCodeClient(String codeClient) {
        Client client = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> new RuntimeException("Impossible de supprimer : Client inexistant (Code: " + codeClient + ")"));
        this.delete(client.getNumeroClient());
    }

    @Transactional
    public java.util.Map<String, Integer> importClientsFromCSV(MultipartFile file) {
        log.info("Début de l'importation CSV");
        int successCount = 0;
        int errorCount = 0;
        
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<ClientDto> csvToBean = new CsvToBeanBuilder<ClientDto>(reader)
                    .withType(ClientDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<ClientDto> dtos = csvToBean.parse();

            // Récupération du rôle CLIENT (ID 3) une seule fois
            com.collecte_epargne.collecte_epargne.entities.Role roleClient = roleRepository.findById(3)
                    .orElseThrow(() -> new RuntimeException("Role Client (ID 3) introuvable en base"));

            for (ClientDto dto : dtos) {
                try {
                    String login = dto.getLoginUtilisateur();
                    if (login == null || login.isEmpty()) {
                        log.warn("Ligne ignorée : login utilisateur manquant");
                        errorCount++;
                        continue;
                    }

                    // 1. Création automatique de l'utilisateur s'il n'existe pas
                    if (!utilisateurRepository.existsById(login)) {
                        Utilisateur newUser = new Utilisateur();
                        newUser.setLogin(login);
                        newUser.setNom(dto.getNom() != null ? dto.getNom() : "Client");
                        newUser.setPrenom(dto.getPrenom() != null ? dto.getPrenom() : "Nouveau");
                        newUser.setTelephone(dto.getTelephone() != null ? dto.getTelephone() : "00000000");
                        newUser.setEmail(dto.getEmail() != null ? dto.getEmail() : login + "@savely.com");
                        newUser.setPassword("Password123"); // Mot de passe par défaut
                        newUser.setStatut(com.collecte_epargne.collecte_epargne.utils.StatutGenerique.ACTIF);
                        newUser.setDateCreation(java.time.Instant.now());
                        newUser.setRole(roleClient);

                        utilisateurRepository.save(newUser);
                        log.info("Utilisateur créé automatiquement pour l'import : {}", login);
                    }

                    // Pour l'import CSV, on peut mettre des valeurs par défaut pour les chemins d'images
                    if (dto.getPhotoPath() == null) dto.setPhotoPath("uploads/clients/default.png");
                    if (dto.getCniRectoPath() == null) dto.setCniRectoPath("uploads/clients/default_recto.png");
                    if (dto.getCniVersoPath() == null) dto.setCniVersoPath("uploads/clients/default_verso.png");

                    this.save(dto);
                    successCount++;
                } catch (Exception e) {
                    log.error("Erreur lors de l'importation de la ligne pour l'utilisateur {} : {}", dto.getLoginUtilisateur(), e.getMessage());
                    errorCount++;
                }
            }
            log.info("Importation CSV terminée. Succès: {}, Erreurs: {}", successCount, errorCount);
            
            java.util.Map<String, Integer> stats = new java.util.HashMap<>();
            stats.put("success", successCount);
            stats.put("error", errorCount);
            return stats;
            
        } catch (Exception e) {
            log.error("Erreur critique lors de l'importation CSV", e);
            throw new RuntimeException("Erreur lors de la lecture du fichier CSV : " + e.getMessage());
        }
    }

    @Override
    public ClientDto getByLogin(String login) {
        Client client = clientRepository.findByUtilisateurLogin(login)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le login : " + login));
        return clientMapper.toDto(client);
    }

    @Override
    public List<ClientDto> getClientsByAgence(Integer idAgence) {
        return clientRepository.findByCollecteurAssigne_AgenceZone_IdAgence(idAgence).stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }
}