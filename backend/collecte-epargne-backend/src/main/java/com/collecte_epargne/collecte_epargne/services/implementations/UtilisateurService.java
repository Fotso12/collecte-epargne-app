package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.UtilisateurMapper;
import com.collecte_epargne.collecte_epargne.repositories.RoleRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.UtilisateurInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UtilisateurService implements UtilisateurInterface {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final RoleRepository roleRepository;
    // Injection du service d'envoi d'emails pour notifier les nouveaux utilisateurs
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UtilisateurService.class);

    // Pour la relation Role

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper, RoleRepository roleRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // Méthode utilitaire pour attacher l'entité Role (inchangée)
    private void assignerRelations(Utilisateur utilisateur, UtilisateurDto dto) {
        if (dto.getIdRole() != null) {
            Integer idRole = Objects.requireNonNull(dto.getIdRole());
            Role role = roleRepository.findById(idRole)
                    .orElseThrow(() -> new RuntimeException("Rôle non trouvé avec l'ID : " + dto.getIdRole()));
            utilisateur.setRole(role);
        }
    }

    /**
     * Cette méthode est modifiée pour prendre un DTO standard et le mot de passe en clair.
     */
    @Override
    @SuppressWarnings("null")
    public UtilisateurDto save(UtilisateurDto utilisateurDto, String password) {
        log.info("Sauvegarde d'utilisateur avec login: {}", utilisateurDto.getLogin());
        Objects.requireNonNull(utilisateurDto, "utilisateurDto ne doit pas être null");
        if (utilisateurDto.getLogin() == null || utilisateurDto.getLogin().isEmpty() || password == null) {
            throw new IllegalArgumentException("Le login et le mot de passe sont obligatoires.");
        }

        if (utilisateurRepository.existsById(utilisateurDto.getLogin())) {
            throw new RuntimeException("Un utilisateur avec ce login existe déjà.");
        }

        Utilisateur utilisateurToSave = utilisateurMapper.toEntity(utilisateurDto);

        // 💥 HACHAGE DU MOT DE PASSE (SÉCURITÉ)
        utilisateurToSave.setPassword(passwordEncoder.encode(password));

        utilisateurToSave.setDateCreation(Instant.now());

        assignerRelations(utilisateurToSave, utilisateurDto);

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateurToSave);
        log.info("Utilisateur sauvegardé avec succès avec login: {}", savedUtilisateur.getLogin());

        // ===== ENVOI D'EMAIL AUTOMATIQUE =====
        // Après la création réussie de l'utilisateur, on envoie un email avec ses identifiants
        try {
            // Appel du service EmailService pour envoyer l'email avec login et mot de passe en clair
            emailService.sendUserCredentialsEmail(savedUtilisateur.getEmail(), savedUtilisateur.getLogin(), password);
        } catch (Exception e) {
            // Gestion d'erreur : si l'envoi d'email échoue, on log l'erreur mais on ne fait pas échouer la création
            // Cela permet à l'utilisateur d'être créé même si le serveur d'email est indisponible
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
        // ===== FIN ENVOI D'EMAIL =====

        return utilisateurMapper.toDto(savedUtilisateur);
    }

    @Override
    public List<UtilisateurDto> getAll() {
        log.info("Récupération de tous les utilisateurs");
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurDto getByLogin(String login) {
        Objects.requireNonNull(login, "login ne doit pas être null");
        log.info("Récupération d'utilisateur avec login: {}", login);
        Utilisateur utilisateur = utilisateurRepository.findById(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le login : " + login));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    public UtilisateurDto getByEmail(String email) {
        Objects.requireNonNull(email, "email ne doit pas être null");
        log.info("Récupération d'utilisateur avec email: {}", email);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    public UtilisateurDto update(String login, UtilisateurDto utilisateurDto) {
        Objects.requireNonNull(login, "login ne doit pas être null");
        Objects.requireNonNull(utilisateurDto, "utilisateurDto ne doit pas être null");
        log.info("Mise à jour d'utilisateur avec login: {}", login);
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
        log.info("Utilisateur mis à jour avec succès avec login: {}", updatedUtilisateur.getLogin());
        return utilisateurMapper.toDto(updatedUtilisateur);
    }

    @Override
    public void updatePassword(String login, String newPassword) {
        Objects.requireNonNull(login, "login ne doit pas être null");
        log.info("Mise à jour du mot de passe pour l'utilisateur avec login: {}", login);
        Utilisateur existingUtilisateur = utilisateurRepository.findById(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + login));

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe est invalide.");
        }

        // 💥 HACHAGE DU MOT DE PASSE (SÉCURITÉ)
        existingUtilisateur.setPassword(passwordEncoder.encode(newPassword));

        utilisateurRepository.save(existingUtilisateur);
        log.info("Mot de passe mis à jour pour l'utilisateur avec login: {}", login);
    }

//    @Override
//    public UtilisateurDto updateStatut(String login, String statut) {
//        Objects.requireNonNull(login, "login ne doit pas être null");
//        Objects.requireNonNull(statut, "statut ne doit pas être null");
//        log.info("Mise à jour du statut pour l'utilisateur avec login: {}", login);
//        Utilisateur existingUtilisateur = utilisateurRepository.findById(login)
//                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + login));
//
//        try {
//            StatutGenerique statutEnum = StatutGenerique.valueOf(statut.toUpperCase());
//            existingUtilisateur.setStatut(statutEnum);
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Statut invalide : " + statut + ". Valeurs possibles : " +
//                    java.util.Arrays.toString(StatutGenerique.values()));
//        }
//
//        Utilisateur updatedUtilisateur = utilisateurRepository.save(existingUtilisateur);
//        log.info("Statut mis à jour pour l'utilisateur avec login: {}", login);
//        return utilisateurMapper.toDto(updatedUtilisateur);
//    }

    @Override
    public void delete(String login) {
        Objects.requireNonNull(login, "login ne doit pas être null");
        log.info("Suppression d'utilisateur avec login: {}", login);
        if (!utilisateurRepository.existsById(login)) {
            throw new RuntimeException("Utilisateur inexistant : " + login);
        }
        // Note: Grâce à CascadeType.ALL dans l'entité Utilisateur,
        // la suppression de l'utilisateur entraînera la suppression de l'Employe/Client associé.
        utilisateurRepository.deleteById(login);
        log.info("Utilisateur supprimé avec login: {}", login);
    }
}