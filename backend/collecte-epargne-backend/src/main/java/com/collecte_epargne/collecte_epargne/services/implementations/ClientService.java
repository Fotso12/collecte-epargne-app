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
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    // Déclaration du logger selon votre structure UtilisateurService
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    public ClientService(ClientRepository clientRepository,
                         ClientMapper clientMapper,
                         UtilisateurRepository utilisateurRepository,
                         EmployeRepository employeRepository,
                         CodeGenerator codeGenerator) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.employeRepository = employeRepository;
        this.codeGenerator = codeGenerator;
    }

    private void assignerRelations(Client client, ClientDto dto) {
        // 1. Liaison avec l'Utilisateur
        if (dto.getLoginUtilisateur() != null) {
            String login = Objects.requireNonNull(dto.getLoginUtilisateur());
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
        } else {
            client.setCollecteurAssigne(null);
        }
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {
        log.info("Tentative de sauvegarde d'un nouveau client");
        Objects.requireNonNull(clientDto, "clientDto ne doit pas être null");

        if (clientRepository.findByNumeroClient(clientDto.getNumeroClient()).isPresent()) {
            log.warn("Échec sauvegarde : Le numéro client {} existe déjà", clientDto.getNumeroClient());
            throw new RuntimeException("Un client avec ce numéro client existe déjà.");
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
        existingClient.setPhotoPath(clientDto.getPhotoPath());
        existingClient.setCniRectoPath(clientDto.getCniRectoPath());
        existingClient.setCniVersoPath(clientDto.getCniVersoPath());

        assignerRelations(existingClient, clientDto);

        Client updatedClient = clientRepository.save(existingClient);
        log.info("Client numéro {} mis à jour avec succès", numeroClient);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    @Transactional
    public void delete(Long numClient) {
        log.info("Suppression du client numéro : {}", numClient);
        if (!clientRepository.existsById(numClient)) {
            log.error("Suppression échouée : Client {} introuvable", numClient);
            throw new RuntimeException("Impossible de supprimer : Client inexistant (ID: " + numClient + ")");
        }
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
        log.info("Mise à jour du client via code : {}", codeClient);
        Client existingClient = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> {
                    log.error("Mise à jour échouée : Code {} introuvable", codeClient);
                    return new RuntimeException("Client non trouvé avec le code : " + codeClient);
                });

        return update(existingClient.getNumeroClient(), clientDto);
    }

    @Override
    @Transactional
    public void deleteByCodeClient(String codeClient) {
        log.info("Suppression du client via code : {}", codeClient);
        if (!clientRepository.existsByCodeClient(codeClient)) {
            log.error("Suppression échouée : Code {} introuvable", codeClient);
            throw new RuntimeException("Impossible de supprimer : Client inexistant (Code: " + codeClient + ")");
        }
        clientRepository.deleteByCodeClient(codeClient);
        log.info("Client avec code {} supprimé avec succès", codeClient);
    }

    @Override
    public ClientDto getByLogin(String login) {
        Client client = clientRepository.findByUtilisateurLogin(login)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le login : " + login));
        return clientMapper.toDto(client);
    }
}