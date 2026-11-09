package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.CreateInstitutionRequest;
import com.collecte_epargne.collecte_epargne.dtos.CreateUserRequest;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller pour les actions admin
 * Gestion des agences, caissiers, collecteurs, superviseurs, auditeurs
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final InstitutionRepository institutionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final EmployeRepository employeRepository;
    private final AgenceZoneRepository agenceZoneRepository;

    public AdminController(
            InstitutionRepository institutionRepository,
            UtilisateurRepository utilisateurRepository,
            RoleRepository roleRepository,
            EmployeRepository employeRepository,
            AgenceZoneRepository agenceZoneRepository
    ) {
        this.institutionRepository = institutionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.employeRepository = employeRepository;
        this.agenceZoneRepository = agenceZoneRepository;
    }

    /**
     * Créer une nouvelle institution/agence
     */
    @PostMapping("/institutions")
    public ResponseEntity<?> createInstitution(@Valid @RequestBody CreateInstitutionRequest request) {
        try {
            System.out.println("📥 Création institution: " + request.getName() + " (" + request.getCode() + ")");
            
            // Vérifier unicité du code
            if (institutionRepository.findByCode(request.getCode()).isPresent()) {
                System.out.println("❌ Code déjà existant: " + request.getCode());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Une institution avec ce code existe déjà"));
            }

            Institution institution = new Institution();
            institution.setName(request.getName());
            institution.setCode(request.getCode());
            institution.setContactEmail(request.getContactEmail());
            institution.setContactPhone(request.getContactPhone());
            institution.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Africa/Abidjan");

            Institution saved = institutionRepository.save(institution);
            System.out.println("✅ Institution créée avec ID: " + saved.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Institution créée avec succès",
                            "id", saved.getId(),
                            "name", saved.getName(),
                            "code", saved.getCode()
                    ));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    /**
     * Lister toutes les institutions
     */
    @GetMapping("/institutions")
    public ResponseEntity<List<Map<String, Object>>> getAllInstitutions() {
        System.out.println("📤 Récupération de toutes les institutions");
        List<Institution> allInstitutions = institutionRepository.findAll();
        System.out.println("✅ " + allInstitutions.size() + " institution(s) trouvée(s)");
        
        List<Map<String, Object>> institutions = allInstitutions.stream()
                .map(inst -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", inst.getId());
                    map.put("name", inst.getName());
                    map.put("code", inst.getCode());
                    map.put("contactEmail", inst.getContactEmail() != null ? inst.getContactEmail() : "");
                    map.put("contactPhone", inst.getContactPhone() != null ? inst.getContactPhone() : "");
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(institutions);
    }

    /**
     * Modifier une institution/agence
     */
    @PutMapping("/institutions/{id}")
    public ResponseEntity<?> updateInstitution(
            @PathVariable Long id,
            @Valid @RequestBody CreateInstitutionRequest request) {
        try {
            System.out.println("📥 Modification institution ID: " + id);
            
            Institution institution = institutionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Institution non trouvée"));
            
            // Vérifier unicité du code si changé
            if (!institution.getCode().equals(request.getCode())) {
                if (institutionRepository.findByCode(request.getCode()).isPresent()) {
                    System.out.println("❌ Code déjà existant: " + request.getCode());
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Une institution avec ce code existe déjà"));
                }
            }
            
            institution.setName(request.getName());
            institution.setCode(request.getCode());
            institution.setContactEmail(request.getContactEmail());
            institution.setContactPhone(request.getContactPhone());
            institution.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Africa/Abidjan");
            
            Institution saved = institutionRepository.save(institution);
            System.out.println("✅ Institution modifiée avec ID: " + saved.getId());
            
            return ResponseEntity.ok(Map.of(
                    "message", "Institution modifiée avec succès",
                    "id", saved.getId(),
                    "name", saved.getName(),
                    "code", saved.getCode()
            ));
        } catch (RuntimeException e) {
            System.out.println("❌ Erreur lors de la modification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la modification: " + e.getMessage()));
        }
    }

    /**
     * Supprimer une institution/agence
     */
    @DeleteMapping("/institutions/{id}")
    public ResponseEntity<?> deleteInstitution(@PathVariable Long id) {
        try {
            System.out.println("📥 Suppression institution ID: " + id);
            
            Institution institution = institutionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Institution non trouvée"));
            
            // Vérifier s'il y a des employés assignés à cette institution (via AgenceZone)
            // Chercher l'AgenceZone correspondante
            AgenceZone agenceZone = agenceZoneRepository.findAll().stream()
                    .filter(az -> az.getCode().equals(institution.getCode()))
                    .findFirst()
                    .orElse(null);
            
            if (agenceZone != null) {
                long employeeCount = employeRepository.findAll().stream()
                        .filter(e -> e.getAgenceZone() != null && e.getAgenceZone().getIdAgence().equals(agenceZone.getIdAgence()))
                        .count();
                if (employeeCount > 0) {
                    System.out.println("❌ Impossible de supprimer: " + employeeCount + " employé(s) assigné(s)");
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Impossible de supprimer cette institution car elle a des employés assignés"));
                }
            }
            
            institutionRepository.delete(institution);
            System.out.println("✅ Institution supprimée avec ID: " + id);
            
            return ResponseEntity.ok(Map.of("message", "Institution supprimée avec succès"));
        } catch (RuntimeException e) {
            System.out.println("❌ Erreur lors de la suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    /**
     * Créer un utilisateur (caissier, collecteur, superviseur, auditeur)
     */
    @PostMapping("/users")
    @Transactional
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            System.out.println("📥 Création utilisateur: " + request.getFullName() + " (" + request.getRoleCode() + ")");
            
            // Vérifier unicité de l'email
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                System.out.println("❌ Email déjà utilisé: " + request.getEmail());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cet email est déjà utilisé"));
            }

            // Vérifier que l'institution existe
            Institution institution = institutionRepository.findById(request.getInstitutionId())
                    .orElseThrow(() -> new RuntimeException("Institution non trouvée"));

            // Récupérer le rôle
            Role role = roleRepository.findByCode(request.getRoleCode())
                    .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + request.getRoleCode()));
            System.out.println("✅ Rôle trouvé: " + role.getNom());

            // Générer login (limiter à 50 caractères max)
            String emailPrefix = request.getEmail().split("@")[0];
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
            String generatedLogin = (emailPrefix + "_" + timestamp);
            if (generatedLogin.length() > 50) {
                generatedLogin = generatedLogin.substring(0, 50);
            }
            System.out.println("✅ Login généré: " + generatedLogin);

            // Séparer nom et prénom
            String[] nameParts = request.getFullName().split(" ", 2);
            String prenom = nameParts[0];
            String nom = nameParts.length > 1 ? nameParts[1] : "";

            // Créer l'utilisateur
            Utilisateur user = new Utilisateur();
            user.setLogin(generatedLogin);
            user.setRole(role);
            user.setNom(nom.isEmpty() ? prenom : nom);
            user.setPrenom(prenom);
            user.setEmail(request.getEmail());
            user.setTelephone(request.getPhone());
            user.setPassword(request.getPassword()); // TODO: Hasher avec bcrypt
            user.setStatut(StatutGenerique.ACTIF);

            System.out.println("💾 Sauvegarde utilisateur...");
            Utilisateur savedUser = utilisateurRepository.save(user);
            System.out.println("✅ Utilisateur sauvegardé avec login: " + savedUser.getLogin());

            // Si c'est un employé (caissier, collecteur, superviseur), créer l'entrée employe
            if (request.getRoleCode().equals("caissier") || 
                request.getRoleCode().equals("collector") || 
                request.getRoleCode().equals("supervisor")) {
                
                System.out.println("👤 Création entrée Employe pour: " + request.getRoleCode());
                
                try {
                    // Vérifier si un employe existe déjà pour cet utilisateur
                    if (employeRepository.findByUtilisateurLogin(savedUser.getLogin()).isPresent()) {
                        System.out.println("⚠️ Un employe existe déjà pour ce login: " + savedUser.getLogin());
                        throw new RuntimeException("Un employe existe déjà pour cet utilisateur");
                    }
                    
                    Employe employe = new Employe();
                    employe.setUtilisateur(savedUser);
                    
                    // Matricule ou badge code (limiter à 50 caractères)
                    String matricule = request.getMatricule() != null && !request.getMatricule().isEmpty() ? request.getMatricule() :
                                      request.getBadgeCode() != null && !request.getBadgeCode().isEmpty() ? request.getBadgeCode() :
                                      generatedLogin;
                    if (matricule.length() > 50) {
                        matricule = matricule.substring(0, 50);
                    }
                    
                    // Vérifier l'unicité du matricule
                    if (employeRepository.findByMatricule(matricule).isPresent()) {
                        System.out.println("⚠️ Matricule déjà utilisé: " + matricule);
                        // Générer un matricule unique
                        matricule = matricule + "_" + System.currentTimeMillis();
                        if (matricule.length() > 50) {
                            matricule = matricule.substring(0, 50);
                        }
                    }
                    
                    employe.setMatricule(matricule);
                    employe.setDateEmbauche(LocalDate.now());
                    
                    // Type employé selon le rôle
                    if (request.getRoleCode().equals("caissier")) {
                        employe.setTypeEmploye(TypeEmploye.CAISSIER);
                    } else if (request.getRoleCode().equals("collector")) {
                        employe.setTypeEmploye(TypeEmploye.COLLECTEUR);
                    } else if (request.getRoleCode().equals("supervisor")) {
                        employe.setTypeEmploye(TypeEmploye.SUPERVISEUR);
                    }
                    
                    System.out.println("💾 Sauvegarde employe avec matricule: " + matricule + ", login: " + savedUser.getLogin());
                    System.out.println("💾 Type employe: " + employe.getTypeEmploye());
                    
                    // Recharger l'utilisateur depuis la base pour s'assurer qu'il est attaché à la session Hibernate
                    // Cela évite les problèmes de détachement d'entité
                    Utilisateur attachedUser = utilisateurRepository.findById(savedUser.getLogin())
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé après création: " + savedUser.getLogin()));
                    
                    System.out.println("✅ Utilisateur rechargé: " + attachedUser.getLogin());
                    
                    employe.setUtilisateur(attachedUser);
                    
                    System.out.println("💾 Tentative de sauvegarde employe...");
                    System.out.println("   - Matricule: " + employe.getMatricule());
                    System.out.println("   - Type: " + employe.getTypeEmploye());
                    System.out.println("   - Login utilisateur: " + employe.getUtilisateur().getLogin());
                    
                    Employe savedEmploye = employeRepository.save(employe);
                    System.out.println("✅ Employe sauvegardé avec ID: " + savedEmploye.getIdEmploye());
                    System.out.println("✅ Employe sauvegardé avec succès, ID: " + employe.getIdEmploye());
                } catch (Exception e) {
                    System.out.println("❌ Erreur lors de la création de l'employe: " + e.getMessage());
                    System.out.println("❌ Type d'erreur: " + e.getClass().getName());
                    e.printStackTrace();
                    // Supprimer l'utilisateur créé si l'employe ne peut pas être créé
                    try {
                        utilisateurRepository.delete(savedUser);
                        System.out.println("🗑️ Utilisateur supprimé suite à l'erreur");
                    } catch (Exception deleteEx) {
                        System.out.println("⚠️ Erreur lors de la suppression de l'utilisateur: " + deleteEx.getMessage());
                    }
                    throw new RuntimeException("Erreur lors de la création de l'employe: " + e.getMessage(), e);
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Utilisateur créé avec succès",
                            "login", savedUser.getLogin(),
                            "email", savedUser.getEmail(),
                            "role", role.getNom()
                    ));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    /**
     * Lister tous les utilisateurs
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = utilisateurRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("login", user.getLogin());
                    map.put("nom", user.getNom());
                    map.put("prenom", user.getPrenom());
                    map.put("email", user.getEmail());
                    map.put("phone", user.getTelephone() != null ? user.getTelephone() : "");
                    map.put("roleCode", user.getRole().getCode());
                    map.put("roleName", user.getRole().getNom());
                    map.put("statut", user.getStatut() != null ? user.getStatut().toString() : "ACTIF");
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer les détails d'un utilisateur
     */
    @GetMapping("/users/{login}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String login) {
        Utilisateur user = utilisateurRepository.findById(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Map<String, Object> map = new HashMap<>();
        map.put("login", user.getLogin());
        map.put("nom", user.getNom());
        map.put("prenom", user.getPrenom());
        map.put("email", user.getEmail());
        map.put("phone", user.getTelephone() != null ? user.getTelephone() : "");
        map.put("roleCode", user.getRole().getCode());
        map.put("roleName", user.getRole().getNom());
        map.put("statut", user.getStatut() != null ? user.getStatut().toString() : "ACTIF");
        
        // Informations employé si applicable
        if (user.getEmploye() != null) {
            Map<String, Object> employeInfo = new HashMap<>();
            employeInfo.put("idEmploye", user.getEmploye().getIdEmploye());
            employeInfo.put("matricule", user.getEmploye().getMatricule());
            employeInfo.put("typeEmploye", user.getEmploye().getTypeEmploye().toString());
            map.put("employe", employeInfo);
        }

        return ResponseEntity.ok(map);
    }

    /**
     * Modifier un utilisateur
     */
    @PutMapping("/users/{login}")
    public ResponseEntity<?> updateUser(
            @PathVariable String login,
            @Valid @RequestBody CreateUserRequest request) {
        try {
            Utilisateur user = utilisateurRepository.findById(login)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Vérifier unicité de l'email si changé
            if (!user.getEmail().equals(request.getEmail())) {
                if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Cet email est déjà utilisé"));
                }
            }

            // Récupérer le rôle si changé
            if (!user.getRole().getCode().equals(request.getRoleCode())) {
                Role role = roleRepository.findByCode(request.getRoleCode())
                        .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + request.getRoleCode()));
                user.setRole(role);
            }

            // Séparer nom et prénom
            String[] nameParts = request.getFullName().split(" ", 2);
            String prenom = nameParts[0];
            String nom = nameParts.length > 1 ? nameParts[1] : "";

            // Mettre à jour les informations
            user.setNom(nom.isEmpty() ? prenom : nom);
            user.setPrenom(prenom);
            user.setEmail(request.getEmail());
            user.setTelephone(request.getPhone());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                user.setPassword(request.getPassword()); // TODO: Hasher avec bcrypt
            }

            Utilisateur savedUser = utilisateurRepository.save(user);

            // Mettre à jour les informations employé si applicable
            if (user.getEmploye() != null && 
                (request.getRoleCode().equals("caissier") || 
                 request.getRoleCode().equals("collector") || 
                 request.getRoleCode().equals("supervisor"))) {
                
                Employe employe = user.getEmploye();
                
                // Mettre à jour matricule/badge code
                if (request.getMatricule() != null && !request.getMatricule().isEmpty()) {
                    employe.setMatricule(request.getMatricule());
                } else if (request.getBadgeCode() != null && !request.getBadgeCode().isEmpty()) {
                    employe.setMatricule(request.getBadgeCode());
                }
                
                employeRepository.save(employe);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Utilisateur modifié avec succès",
                    "login", savedUser.getLogin(),
                    "email", savedUser.getEmail()
            ));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la modification: " + e.getMessage()));
        }
    }

    /**
     * Changer le statut d'un utilisateur (ACTIF/INACTIF)
     */
    @PatchMapping("/users/{login}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable String login,
            @RequestBody Map<String, String> request) {
        try {
            String statutStr = request.get("statut");
            if (statutStr == null || statutStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le statut est requis (ACTIF ou INACTIF)"));
            }

            Utilisateur user = utilisateurRepository.findById(login)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Ne pas permettre de désactiver l'admin par défaut
            if (("admin".equals(user.getLogin()) || "admin@savings.local".equals(user.getEmail())) 
                && "INACTIF".equals(statutStr)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Impossible de désactiver l'administrateur par défaut"));
            }

            StatutGenerique newStatut;
            try {
                newStatut = StatutGenerique.valueOf(statutStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Statut invalide. Valeurs acceptées: ACTIF, INACTIF, SUSPENDU"));
            }

            user.setStatut(newStatut);
            utilisateurRepository.save(user);

            System.out.println("✅ Statut utilisateur " + login + " changé en " + newStatut);

            return ResponseEntity.ok(Map.of(
                    "message", "Statut utilisateur modifié avec succès",
                    "login", login,
                    "statut", newStatut.toString()
            ));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du changement de statut: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du changement de statut: " + e.getMessage()));
        }
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/users/{login}")
    public ResponseEntity<?> deleteUser(@PathVariable String login) {
        try {
            Utilisateur user = utilisateurRepository.findById(login)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Ne pas permettre la suppression de l'admin par défaut
            if ("admin".equals(user.getLogin()) || "admin@savings.local".equals(user.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Impossible de supprimer l'administrateur par défaut"));
            }

            // Supprimer l'employé associé si existe
            if (user.getEmploye() != null) {
                employeRepository.delete(user.getEmploye());
            }

            utilisateurRepository.delete(user);

            System.out.println("✅ Utilisateur supprimé: " + login);

            return ResponseEntity.ok(Map.of(
                    "message", "Utilisateur supprimé avec succès",
                    "login", login
            ));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    /**
     * Récupérer les détails d'une institution
     */
    @GetMapping("/institutions/{id}")
    public ResponseEntity<Map<String, Object>> getInstitutionDetails(@PathVariable Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution non trouvée"));

        Map<String, Object> details = new HashMap<>();
        details.put("id", institution.getId());
        details.put("name", institution.getName());
        details.put("code", institution.getCode());
        details.put("contactEmail", institution.getContactEmail() != null ? institution.getContactEmail() : "");
        details.put("contactPhone", institution.getContactPhone() != null ? institution.getContactPhone() : "");
        details.put("timezone", institution.getTimezone() != null ? institution.getTimezone() : "Africa/Abidjan");

        return ResponseEntity.ok(details);
    }

    /**
     * Récupérer les employés d'une institution
     * Note: Pour l'instant, on récupère tous les employés qui n'ont pas d'agence assignée
     * ou qui ont une agence liée à cette institution
     */
    @GetMapping("/institutions/{id}/employees")
    public ResponseEntity<List<Map<String, Object>>> getInstitutionEmployees(@PathVariable Long id) {
        // Vérifier que l'institution existe
        if (!institutionRepository.existsById(id)) {
            throw new RuntimeException("Institution non trouvée");
        }

        // Récupérer tous les employés (superviseurs et collecteurs)
        List<Employe> allEmployees = employeRepository.findAll();
        
        // Filtrer pour ne garder que les superviseurs et collecteurs
        List<Map<String, Object>> employees = allEmployees.stream()
                .filter(emp -> emp.getTypeEmploye() == TypeEmploye.SUPERVISEUR || 
                              emp.getTypeEmploye() == TypeEmploye.COLLECTEUR)
                .map(emp -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idEmploye", emp.getIdEmploye());
                    map.put("matricule", emp.getMatricule());
                    map.put("typeEmploye", emp.getTypeEmploye().toString());
                    map.put("dateEmbauche", emp.getDateEmbauche().toString());
                    
                    // Informations de l'utilisateur
                    if (emp.getUtilisateur() != null) {
                        map.put("login", emp.getUtilisateur().getLogin());
                        map.put("nom", emp.getUtilisateur().getNom());
                        map.put("prenom", emp.getUtilisateur().getPrenom());
                        map.put("email", emp.getUtilisateur().getEmail());
                        map.put("telephone", emp.getUtilisateur().getTelephone());
                    }
                    
                    // Vérifier si l'employé est affecté à cette institution
                    // Pour l'instant, on vérifie si l'agenceZone existe
                    boolean isAssigned = emp.getAgenceZone() != null;
                    map.put("isAssigned", isAssigned);
                    if (isAssigned) {
                        map.put("agenceId", emp.getAgenceZone().getIdAgence());
                        map.put("agenceNom", emp.getAgenceZone().getNom());
                    }
                    
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(employees);
    }

    /**
     * Affecter un employé à une institution
     * Crée une AgenceZone pour l'institution si elle n'existe pas
     */
    @PostMapping("/institutions/{institutionId}/assign-employee")
    public ResponseEntity<?> assignEmployeeToInstitution(
            @PathVariable Long institutionId,
            @RequestBody Map<String, Object> request) {
        try {
            Integer employeeId = (Integer) request.get("employeeId");
            if (employeeId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "employeeId est requis"));
            }

            Institution institution = institutionRepository.findById(institutionId)
                    .orElseThrow(() -> new RuntimeException("Institution non trouvée"));

            Employe employee = employeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

            // Vérifier que l'employé est un superviseur ou collecteur
            if (employee.getTypeEmploye() != TypeEmploye.SUPERVISEUR && 
                employee.getTypeEmploye() != TypeEmploye.COLLECTEUR) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Seuls les superviseurs et collecteurs peuvent être affectés à une agence"));
            }

            // Créer ou récupérer une AgenceZone pour cette institution
            AgenceZone agenceZone = agenceZoneRepository.findAll().stream()
                    .filter(az -> az.getCode().equals(institution.getCode()))
                    .findFirst()
                    .orElse(null);

            if (agenceZone == null) {
                agenceZone = new AgenceZone();
                agenceZone.setCode(institution.getCode());
                agenceZone.setNom(institution.getName());
                agenceZone.setTelephone(institution.getContactPhone());
                agenceZone.setStatut(StatutGenerique.ACTIF);
                agenceZone = agenceZoneRepository.save(agenceZone);
                System.out.println("✅ AgenceZone créée pour l'institution: " + institution.getName());
            }

            // Affecter l'employé à l'agence
            employee.setAgenceZone(agenceZone);
            employeRepository.save(employee);

            System.out.println("✅ Employé " + employee.getMatricule() + " affecté à l'institution " + institution.getName());

            return ResponseEntity.ok(Map.of(
                    "message", "Employé affecté avec succès",
                    "employeeId", employee.getIdEmploye(),
                    "institutionId", institution.getId()
            ));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'affectation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'affectation: " + e.getMessage()));
        }
    }

    /**
     * Retirer un employé d'une institution
     */
    @PostMapping("/institutions/{institutionId}/unassign-employee")
    public ResponseEntity<?> unassignEmployeeFromInstitution(
            @PathVariable Long institutionId,
            @RequestBody Map<String, Object> request) {
        try {
            Integer employeeId = (Integer) request.get("employeeId");
            if (employeeId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "employeeId est requis"));
            }

            Employe employee = employeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

            employee.setAgenceZone(null);
            employeRepository.save(employee);

            System.out.println("✅ Employé " + employee.getMatricule() + " retiré de son agence");

            return ResponseEntity.ok(Map.of(
                    "message", "Employé retiré avec succès",
                    "employeeId", employee.getIdEmploye()
            ));
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du retrait: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du retrait: " + e.getMessage()));
        }
    }
}

