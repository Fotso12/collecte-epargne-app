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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService implements ClientInterface {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final EmployeRepository employeRepository;
    private final CodeGenerator codeGenerator;

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

    /**
     * Méthode utilitaire pour attacher les entités relationnelles (Utilisateur et Collecteur)
     */
    private void assignerRelations(Client client, ClientDto dto) {
        // 1. Liaison avec l'Utilisateur (via Login)
        if (dto.getLoginUtilisateur() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(dto.getLoginUtilisateur())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le login : " + dto.getLoginUtilisateur()));
            client.setUtilisateur(utilisateur);
        }

        // 2. Liaison avec le Collecteur Assigné (via ID Employé)
        if (dto.getCodeCollecteurAssigne() != null && !dto.getCodeCollecteurAssigne().isEmpty()) {
            try {
                Integer idCollecteur = Integer.parseInt(dto.getCodeCollecteurAssigne());
                Employe collecteur = employeRepository.findById(idCollecteur)
                        .orElseThrow(() -> new RuntimeException("Collecteur non trouvé avec l'ID : " + idCollecteur));

                // Logique métier: s'assurer que l'employé est bien un collecteur
                if (collecteur.getTypeEmploye() != TypeEmploye.COLLECTEUR) {
                    throw new IllegalArgumentException("L'employé assigné doit être de type COLLECTEUR.");
                }

                client.setCollecteurAssigne(collecteur);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("L'ID du collecteur doit être un nombre valide.");
            }
        } else {
            client.setCollecteurAssigne(null);
        }
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {

        // Vérification de l'unicité du numéro client
        if (clientRepository.findByNumeroClient(clientDto.getNumeroClient()).isPresent()) {
            throw new RuntimeException("Un client avec ce numéro client existe déjà.");
        }

        // Mapping DTO -> Entity
        Client clientToSave = clientMapper.toEntity(clientDto);

        // Assignation des relations (Utilisateur / Employé)
        assignerRelations(clientToSave, clientDto);

        // GÉNÉRATION AUTOMATIQUE DU CODE CLIENT (ex: CLT2025XXXXX)
        if (clientToSave.getCodeClient() == null || clientToSave.getCodeClient().isEmpty()) {
            clientToSave.setCodeClient(codeGenerator.generateClientCode());
        }

        Client savedClient = clientRepository.save(clientToSave);
        return clientMapper.toDto(savedClient);
    }

    @Override
    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDto getById(Long numeroClient) {
        Client client = clientRepository.findById(numeroClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le numéro : " + numeroClient));
        return clientMapper.toDto(client);
    }

    @Override
    public ClientDto getByCodeClient(String codeClient) {
        Client client = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + codeClient));
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto update(Long numeroClient, ClientDto clientDto) {
        Client existingClient = clientRepository.findById(numeroClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé pour la mise à jour : " + numeroClient));

        // Mise à jour des champs simples
        existingClient.setAdresse(clientDto.getAdresse());
        existingClient.setTypeCni(clientDto.getTypeCni());
        existingClient.setNumCni(clientDto.getNumCni());
        existingClient.setDateNaissance(clientDto.getDateNaissance());
        existingClient.setLieuNaissance(clientDto.getLieuNaissance());
        existingClient.setProfession(clientDto.getProfession());
        existingClient.setScoreEpargne(clientDto.getScoreEpargne());

        // Mise à jour des chemins de fichiers
        existingClient.setPhotoPath(clientDto.getPhotoPath());
        existingClient.setCniRectoPath(clientDto.getCniRectoPath());
        existingClient.setCniVersoPath(clientDto.getCniVersoPath());

        // Mise à jour des relations
        assignerRelations(existingClient, clientDto);

        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    @Transactional
    public void delete(Long numClient) {
        if (!clientRepository.existsById(numClient)) {
            throw new RuntimeException("Impossible de supprimer : Client inexistant (ID: " + numClient + ")");
        }
        clientRepository.deleteById(numClient);
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

        // Réutilisation de la logique d'update par ID pour éviter la duplication
        return update(existingClient.getNumeroClient(), clientDto);
    }

    @Override
    @Transactional
    public void deleteByCodeClient(String codeClient) {
        if (!clientRepository.existsByCodeClient(codeClient)) {
            throw new RuntimeException("Impossible de supprimer : Client inexistant (Code: " + codeClient + ")");
        }
        clientRepository.deleteByCodeClient(codeClient);
    }
}