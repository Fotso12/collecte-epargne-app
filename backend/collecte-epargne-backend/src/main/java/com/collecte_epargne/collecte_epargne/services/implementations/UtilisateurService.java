package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.UtilisateurMapper;
import com.collecte_epargne.collecte_epargne.repositories.RoleRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.UtilisateurInterface;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UtilisateurService implements UtilisateurInterface {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final RoleRepository roleRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper, RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.roleRepository = roleRepository;
    }


    // Pour la relation Role

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper, RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.roleRepository = roleRepository;
    }

    // Méthode utilitaire pour attacher l'entité Role (inchangée)
    private void assignerRelations(Utilisateur utilisateur, UtilisateurDto dto) {
        if (dto.getIdRole() != null) {
            Role role = roleRepository.findById(dto.getIdRole())
                    .orElseThrow(() -> new RuntimeException("Rôle non trouvé avec l'ID : " + dto.getIdRole()));
            utilisateur.setRole(role);
        }
    }

    /**
     * Cette méthode est modifiée pour prendre un DTO standard et le mot de passe en clair.
     */
    @Override
    public UtilisateurDto save(UtilisateurDto utilisateurDto, String password) {
        if (utilisateurDto.getLogin() == null || utilisateurDto.getLogin().isEmpty() || password == null) {
            throw new IllegalArgumentException("Le login et le mot de passe sont obligatoires.");
        }

        if (utilisateurRepository.existsById(utilisateurDto.getLogin())) {
            throw new RuntimeException("Un utilisateur avec ce login existe déjà.");
        }

        Utilisateur utilisateurToSave = utilisateurMapper.toEntity(utilisateurDto);

        // 💥 HACHAGE DU MOT DE PASSE (SÉCURITÉ)
        // utilisateurToSave.setPassword(passwordEncoder.encode(password));
        utilisateurToSave.setPassword(password); // Actuellement en clair (NON SÉCURISÉ)

        utilisateurToSave.setDateCreation(Instant.now());

        assignerRelations(utilisateurToSave, utilisateurDto);

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateurToSave);
        return utilisateurMapper.toDto(savedUtilisateur);
    }

    @Override
    public List<UtilisateurDto> getAll() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurDto getByLogin(String login) {
        Utilisateur utilisateur = utilisateurRepository.findById(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le login : " + login));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    public UtilisateurDto getByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    public UtilisateurDto update(String login, UtilisateurDto utilisateurDto) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour la mise à jour : " + login));

        // Mise à jour des champs
        existingUtilisateur.setNom(utilisateurDto.getNom());
        existingUtilisateur.setPrenom(utilisateurDto.getPrenom());
        existingUtilisateur.setTelephone(utilisateurDto.getTelephone());
        existingUtilisateur.setEmail(utilisateurDto.getEmail());
        existingUtilisateur.setStatut(utilisateurDto.getStatut());

        assignerRelations(existingUtilisateur, utilisateurDto);

        Utilisateur updatedUtilisateur = utilisateurRepository.save(existingUtilisateur);
        return utilisateurMapper.toDto(updatedUtilisateur);
    }

    @Override
    public void updatePassword(String login, String newPassword) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + login));

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe est invalide.");
        }

        // 💥 HACHAGE DU MOT DE PASSE (SÉCURITÉ)
        // existingUtilisateur.setPassword(passwordEncoder.encode(newPassword));
        existingUtilisateur.setPassword(newPassword); // Actuellement en clair (NON SÉCURISÉ)

        utilisateurRepository.save(existingUtilisateur);
    }

    @Override
    public void delete(String login) {
        if (!utilisateurRepository.existsById(login)) {
            throw new RuntimeException("Utilisateur inexistant : " + login);
        }
        // Note: Grâce à CascadeType.ALL dans l'entité Utilisateur,
        // la suppression de l'utilisateur entraînera la suppression de l'Employe/Client associé.
        utilisateurRepository.deleteById(login);
    }
}