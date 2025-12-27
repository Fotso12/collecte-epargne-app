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
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService implements ClientInterface {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UtilisateurRepository utilisateurRepository; // Pour la relation Utilisateur
    private final EmployeRepository employeRepository; // Pour la relation CollecteurAssigne

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, UtilisateurRepository utilisateurRepository, EmployeRepository employeRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.employeRepository = employeRepository;
    }

    // Méthode utilitaire pour attacher les entités relationnelles
    private void assignerRelations(Client client, ClientDto dto) {
        // 1. Utilisateur (LOGIN)
        if (dto.getLoginUtilisateur() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(dto.getLoginUtilisateur())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le login : " + dto.getLoginUtilisateur()));
            client.setUtilisateur(utilisateur);
        }

        // 2. Collecteur Assigné (ID Employé)
        if (dto.getCodeCollecteurAssigne() != null && !dto.getCodeCollecteurAssigne().isEmpty()) {
            Integer idCollecteur = Integer.parseInt(dto.getCodeCollecteurAssigne());
            Employe collecteur = employeRepository.findById(idCollecteur)
                    .orElseThrow(() -> new RuntimeException("Collecteur non trouvé avec l'ID : " + dto.getCodeCollecteurAssigne()));

            // Logique métier: s'assurer que l'employé est bien un collecteur
            if (collecteur.getTypeEmploye() != TypeEmploye.COLLECTEUR) {
                throw new IllegalArgumentException("L'employé assigné doit être un COLLECTEUR.");
            }

            client.setCollecteurAssigne(collecteur);
        } else {
            client.setCollecteurAssigne(null); // Dissociation si l'ID est null/vide
        }
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        if (clientDto.getNumeroClient() == null || clientDto.getNumeroClient().isEmpty()) {
            throw new IllegalArgumentException("Le numéro client est obligatoire.");
        }

        // Vérification de l'unicité
        if (clientRepository.findByNumeroClient(clientDto.getNumeroClient()).isPresent()) {
            throw new RuntimeException("Un client avec ce numéro client existe déjà.");
        }

        Client clientToSave = clientMapper.toEntity(clientDto);
        assignerRelations(clientToSave, clientDto);

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
    public ClientDto getById(String codeClient) {
        Client client = clientRepository.findById(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + codeClient));
        return clientMapper.toDto(client);
    }

    @Override
    public ClientDto update(String codeClient, ClientDto clientDto) {
        Client existingClient = clientRepository.findById(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé pour la mise à jour : " + codeClient));

        // Mettre à jour les champs non-relationnels
        existingClient.setNumeroClient(clientDto.getNumeroClient());
        existingClient.setAdresse(clientDto.getAdresse());
        existingClient.setTypeCni(clientDto.getTypeCni());
        existingClient.setNumCni(clientDto.getNumCni());
        existingClient.setDateNaissance(clientDto.getDateNaissance());
        existingClient.setLieuNaissance(clientDto.getLieuNaissance());
        existingClient.setProfession(clientDto.getProfession());
        existingClient.setScoreEpargne(clientDto.getScoreEpargne());

        // Mettre à jour les chemins des fichiers si fournis (omettant les validations complexes ici)
        existingClient.setPhotoPath(clientDto.getPhotoPath());
        existingClient.setCniRectoPath(clientDto.getCniRectoPath());
        existingClient.setCniVersoPath(clientDto.getCniVersoPath());

        // Mettre à jour les relations
        assignerRelations(existingClient, clientDto);

        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toDto(updatedClient);
    }

    @Override
    public void delete(String codeClient) {
        if (!clientRepository.existsById(codeClient)) {
            throw new RuntimeException("Client inexistant : " + codeClient);
        }
        clientRepository.deleteById(codeClient);
    }

    @Override
    public ClientDto getByNumeroClient(String numeroClient) {
        Client client = clientRepository.findByNumeroClient(numeroClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le numéro : " + numeroClient));
        return clientMapper.toDto(client);
    }
}