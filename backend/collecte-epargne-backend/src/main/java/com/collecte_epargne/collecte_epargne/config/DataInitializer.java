package com.collecte_epargne.collecte_epargne.config;

import com.collecte_epargne.collecte_epargne.entities.Institution;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.repositories.InstitutionRepository;
import com.collecte_epargne.collecte_epargne.repositories.RoleRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepository, 
            InstitutionRepository institutionRepository,
            UtilisateurRepository utilisateurRepository) {
        return args -> {
            // Créer l'institution par défaut si elle n'existe pas
            if (institutionRepository.count() == 0) {
                Institution institution = new Institution();
                institution.setName("Institution par Défaut");
                institution.setCode("DEF001");
                institution.setContactEmail("contact@institution.com");
                institution.setContactPhone("+2250100000001");
                institution.setTimezone("Africa/Abidjan");
                institutionRepository.save(institution);
                System.out.println("✅ Institution par défaut créée");
            }
            
            // Vérifier et créer les rôles individuellement
            String[][] rolesToCreate = {
                {"admin", "Administrateur", "Accès complet au système"},
                {"supervisor", "Superviseur", "Supervise les agents collecteurs"},
                {"collector", "Agent collecteur", "Agent de terrain pour la collecte d'épargne"},
                {"caissier", "Caissier", "Caissier pour les opérations bancaires"},
                {"auditor", "Auditeur", "Contrôle et audit des opérations"},
                {"client", "Client", "Compte client épargnant (ajouté pour inscription app mobile)"}
            };

            for (String[] roleData : rolesToCreate) {
                if (roleRepository.findByCode(roleData[0]).isEmpty()) {
                    Role role = new Role();
                    role.setCode(roleData[0]);
                    role.setNom(roleData[1]);
                    role.setDescription(roleData[2]);
                    roleRepository.save(role);
                    System.out.println("✅ Rôle créé : " + roleData[0]);
                }
            }

            // Créer un utilisateur admin par défaut pour se connecter
            if (utilisateurRepository.findByEmail("admin@savings.local").isEmpty()) {
                var adminRoleOpt = roleRepository.findByCode("admin");
                
                if (adminRoleOpt.isPresent()) {
                    Utilisateur admin = new Utilisateur();
                    admin.setLogin("admin");
                    admin.setRole(adminRoleOpt.get());
                    admin.setNom("Admin");
                    admin.setPrenom("Principal");
                    admin.setEmail("admin@savings.local");
                    admin.setTelephone("+2250100000000");
                    admin.setPassword("admin123"); // TODO: Hasher avec bcrypt
                    admin.setStatut(StatutGenerique.ACTIF);
                    utilisateurRepository.save(admin);
                    
                    System.out.println("✅ Utilisateur admin créé:");
                    System.out.println("   Email: admin@savings.local");
                    System.out.println("   Password: admin123");
                    System.out.println("   ⚠️  CHANGEZ CE MOT DE PASSE EN PRODUCTION!");
                } else {
                    System.err.println("❌ Erreur critique : Impossible de créer l'utilisateur admin car le rôle 'admin' n'a pas pu être créé.");
                }
            } else {
                System.out.println("✅ Utilisateur admin déjà présent");
            }
        };
    }
}

