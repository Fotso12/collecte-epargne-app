package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.ClientRegistrationRequest;
import com.collecte_epargne.collecte_epargne.dtos.CollectorRegistrationRequest;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import java.time.Instant;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour l'inscription selon la structure savings_collector.sql
 * - Clients : table séparée (pas dans users)
 * - Collecteurs : créent un user + entrée collector
 */
@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final ClientSavingsRepository clientSavingsRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final InstitutionRepository institutionRepository;
    private final RoleRepository roleRepository;
    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;
    private final AgenceZoneRepository agenceZoneRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public RegistrationController(
            ClientSavingsRepository clientSavingsRepository,
            UtilisateurRepository utilisateurRepository,
            InstitutionRepository institutionRepository,
            RoleRepository roleRepository,
            EmployeRepository employeRepository,
            ClientRepository clientRepository,
            AgenceZoneRepository agenceZoneRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder
    ) {
        this.clientSavingsRepository = clientSavingsRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.institutionRepository = institutionRepository;
        this.roleRepository = roleRepository;
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
        this.agenceZoneRepository = agenceZoneRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscription d'un CLIENT (table client_savings)
     */
    @PostMapping("/client")
    @Transactional
    public ResponseEntity<?> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        try {
            // Vérifier unicité de l'email
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cet email est déjà utilisé"));
            }

            // Vérifier unicité du téléphone
            if (request.getPhone() != null && clientSavingsRepository.findByPhone(request.getPhone()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ce numéro de téléphone est déjà utilisé"));
            }

            // Vérifier unicité du numéro d'identité
            if (request.getIdentityNumber() != null && 
                clientSavingsRepository.findByIdentityNumber(request.getIdentityNumber()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ce numéro d'identité est déjà utilisé"));
            }

            // Vérifier la couverture de la ville
            if (request.getVille() != null && !agenceZoneRepository.existsByVille(request.getVille())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Votre ville (" + request.getVille() + ") n'est pas encore prise en compte par nos services."));
            }

            // Récupérer le rôle client
            Role clientRole = roleRepository.findByCode("client")
                    .orElseThrow(() -> new RuntimeException("Rôle client non trouvé"));

            // Séparer nom et prénom
            String[] nameParts = request.getFullName().split(" ", 2);
            String prenom = nameParts[0];
            String nom = nameParts.length > 1 ? nameParts[1] : "";

            // Générer un login unique basé sur l'email
            String emailPrefix = request.getEmail().split("@")[0];
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
            String generatedLogin = "CLI_" + emailPrefix + "_" + timestamp;

            // Créer l'utilisateur
            Utilisateur user = new Utilisateur();
            user.setLogin(generatedLogin);
            user.setRole(clientRole);
            user.setNom(nom.isEmpty() ? prenom : nom);
            user.setPrenom(prenom);
            user.setEmail(request.getEmail());
            user.setTelephone(request.getPhone());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setStatut(StatutGenerique.ACTIF);
            user.setDateCreation(Instant.now());

            Utilisateur savedUser = utilisateurRepository.save(user);

            // Générer un code client unique
            String codeClient = "CLI" + System.currentTimeMillis();
            while (clientRepository.findByCodeClient(codeClient).isPresent()) {
                codeClient = "CLI" + System.currentTimeMillis();
            }

            // Le numeroClient est auto-généré par la BDD (IDENTITY)
            // On ne le définit pas ici.

            // Créer l'entité Client
            Client client = new Client();
            client.setCodeClient(codeClient);
            // client.setNumeroClient(numeroClient); // Auto-generated
            client.setUtilisateur(savedUser);
            client.setAdresse(request.getAddress());
            client.setVille(request.getVille());
            // Définir le type CNI par défaut (CARTE_IDENTITE si non spécifié)
            if (request.getIdentityType() != null && request.getIdentityType().equalsIgnoreCase("PASSPORT")) {
                client.setTypeCni(com.collecte_epargne.collecte_epargne.utils.TypeCNI.PASSEPORT);
            } else {
                client.setTypeCni(com.collecte_epargne.collecte_epargne.utils.TypeCNI.CARTE_IDENTITE);
            }
            client.setNumCni(request.getIdentityNumber() != null && !request.getIdentityNumber().trim().isEmpty() 
                    ? request.getIdentityNumber() : null);
            client.setScoreEpargne(0);

            // Gérer l'affiliation au collecteur parrain
            if (request.getCollectorMatricule() != null && 
                !request.getCollectorMatricule().trim().isEmpty() && 
                !request.getCollectorMatricule().equals("0000")) {
                
                // Chercher l'employé par son matricule
                Employe collecteurEmploye = employeRepository.findByMatricule(request.getCollectorMatricule().trim())
                        .orElseThrow(() -> new RuntimeException("Aucun collecteur trouvé avec le matricule: " + request.getCollectorMatricule()));
                
                // Vérifier que l'employé est bien un collecteur
                if (collecteurEmploye.getTypeEmploye() != com.collecte_epargne.collecte_epargne.utils.TypeEmploye.COLLECTEUR) {
                    throw new RuntimeException("Le matricule fourni ne correspond pas à un collecteur");
                }
                
                // Assigner le collecteur au client
                client.setCollecteurAssigne(collecteurEmploye);
            }

            clientRepository.save(client);

            // Créer aussi un ClientSavings pour compatibilité
            Long institutionId = request.getInstitutionId() != null ? request.getInstitutionId() : 1L;
            Institution institution = institutionRepository.findById(institutionId)
                    .orElseGet(() -> {
                        Institution defInst = new Institution();
                        defInst.setName("Institution par Défaut");
                        defInst.setCode("DEF001");
                        defInst.setContactEmail("contact@institution.com");
                        return institutionRepository.save(defInst);
                    });

            ClientSavings clientSavings = new ClientSavings();
            clientSavings.setInstitution(institution);
            clientSavings.setFullName(request.getFullName());
            clientSavings.setPhone(request.getPhone());
            clientSavings.setIdentityType(request.getIdentityType());
            clientSavings.setIdentityNumber(request.getIdentityNumber());
            clientSavings.setAddress(request.getAddress());
            clientSavings.setStatus(ClientSavings.ClientStatus.ACTIVE);
            // Assigner le collecteur parrain si disponible, sinon null
            if (request.getCollectorMatricule() != null && 
                !request.getCollectorMatricule().trim().isEmpty() && 
                !request.getCollectorMatricule().equals("0000")) {
                try {
                    Employe collecteurEmploye = employeRepository.findByMatricule(request.getCollectorMatricule().trim())
                            .orElse(null);
                    if (collecteurEmploye != null && collecteurEmploye.getUtilisateur() != null) {
                        clientSavings.setCollector(collecteurEmploye.getUtilisateur());
                    }
                } catch (Exception e) {
                    // Si le collecteur n'est pas trouvé, on continue sans assigner de collecteur
                }
            }
            clientSavingsRepository.save(clientSavings);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Client créé avec succès",
                            "login", savedUser.getLogin(),
                            "email", savedUser.getEmail(),
                            "fullName", request.getFullName()
                    ));
        } catch (Exception e) {
            e.printStackTrace(); // Log l'erreur pour le débogage
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création du client: " + e.getMessage()));
        }
    }

    /**
     * Inscription d'un COLLECTEUR (table users + employe)
     */
    @PostMapping("/collector")
    public ResponseEntity<?> registerCollector(@Valid @RequestBody CollectorRegistrationRequest request) {
        try {
            // Vérifier unicité de l'email
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cet email est déjà utilisé"));
            }

            // Récupérer le rôle collector
            Role collectorRole = roleRepository.findByCode("collector")
                    .orElseThrow(() -> new RuntimeException("Rôle collector non trouvé"));

            // Générer login basé sur email
            String emailPrefix = request.getEmail().split("@")[0];
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
            String generatedLogin = emailPrefix + "_" + timestamp;

            // Créer l'utilisateur
            Utilisateur user = new Utilisateur();
            user.setLogin(generatedLogin);
            user.setRole(collectorRole);
            user.setNom(request.getFullName().split(" ")[request.getFullName().split(" ").length - 1]);
            user.setPrenom(request.getFullName().replace(user.getNom(), "").trim());
            user.setEmail(request.getEmail());
            user.setTelephone(request.getPhone());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setStatut(com.collecte_epargne.collecte_epargne.utils.StatutGenerique.ACTIF);

            Utilisateur savedUser = utilisateurRepository.save(user);

            // Créer l'employé (collecteur)
            Employe employe = new Employe();
            employe.setUtilisateur(savedUser);
            employe.setMatricule(request.getBadgeCode() != null ? request.getBadgeCode() : generatedLogin);
            employe.setDateEmbauche(java.time.LocalDate.now());
            employe.setTypeEmploye(com.collecte_epargne.collecte_epargne.utils.TypeEmploye.COLLECTEUR);

            employeRepository.save(employe);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Collecteur créé avec succès",
                            "login", savedUser.getLogin(),
                            "email", savedUser.getEmail()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création du collecteur: " + e.getMessage()));
        }
    }
}

