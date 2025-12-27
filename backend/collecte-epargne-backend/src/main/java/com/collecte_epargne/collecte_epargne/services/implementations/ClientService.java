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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
            String login = Objects.requireNonNull(dto.getLoginUtilisateur());
            Utilisateur utilisateur = utilisateurRepository.findById(login)
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
    @SuppressWarnings("null")
    public ClientDto save(ClientDto clientDto) {
<<<<<<< HEAD
        Objects.requireNonNull(clientDto, "clientDto ne doit pas être null");
        if (clientDto.getNumeroClient() == null || clientDto.getNumeroClient().isEmpty()) {
=======
        if (clientDto.getNumeroClient() == null) {
>>>>>>> e7d8b8a8ef19a82cdbc5dd8aa8c4525106492910
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
<<<<<<< HEAD
    public ClientDto getById(String codeClient) {
        Objects.requireNonNull(codeClient, "codeClient ne doit pas être null");
        Client client = clientRepository.findById(codeClient)
=======
    public ClientDto getById(Long numeroClient) {
        Client client = clientRepository.findById(numeroClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le numéro : " + numeroClient));
        return clientMapper.toDto(client);
    }

    @Override
    public ClientDto getByCodeClient(String codeClient) {

        Client client = clientRepository.findByCodeClient(codeClient)
>>>>>>> e7d8b8a8ef19a82cdbc5dd8aa8c4525106492910
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + codeClient));
        return clientMapper.toDto(client);
    }

    @Override
<<<<<<< HEAD
    public ClientDto update(String codeClient, ClientDto clientDto) {
        Objects.requireNonNull(codeClient, "codeClient ne doit pas être null");
        Objects.requireNonNull(clientDto, "clientDto ne doit pas être null");
        Client existingClient = clientRepository.findById(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé pour la mise à jour : " + codeClient));
=======
    public ClientDto update(Long numeroClient, ClientDto clientDto) {
        Client existingClient = clientRepository.findById(numeroClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé pour la mise à jour : " + numeroClient));
>>>>>>> e7d8b8a8ef19a82cdbc5dd8aa8c4525106492910

        // Mettre à jour les champs non-relationnels
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
<<<<<<< HEAD
    public void delete(String codeClient) {
        Objects.requireNonNull(codeClient, "codeClient ne doit pas être null");
        if (!clientRepository.existsById(codeClient)) {
            throw new RuntimeException("Client inexistant : " + codeClient);
=======
    public void delete(Long numClient) {
        if (!clientRepository.existsById(numClient)) {
            throw new RuntimeException("Client inexistant : " + numClient);
>>>>>>> e7d8b8a8ef19a82cdbc5dd8aa8c4525106492910
        }
        clientRepository.deleteById(numClient);
    }

    @Override
<<<<<<< HEAD
    public ClientDto getByNumeroClient(String numeroClient) {
        Objects.requireNonNull(numeroClient, "numeroClient ne doit pas être null");
=======
    public ClientDto getByNumeroClient(Long numeroClient) {
>>>>>>> e7d8b8a8ef19a82cdbc5dd8aa8c4525106492910
        Client client = clientRepository.findByNumeroClient(numeroClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le numéro : " + numeroClient));
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto updateByCodeClient(String codeClient, ClientDto clientDto) {
        Client existingClient = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé pour la mise à jour : " + codeClient));

        // Mettre à jour les champs non-relationnels
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
    public void deleteByCodeClient(String codeClient) {
        if (!clientRepository.existsByCodeClient(codeClient)) {
            throw new RuntimeException("Client inexistant : " + codeClient);
        }
        clientRepository.deleteByCodeClient(codeClient);
    }

}