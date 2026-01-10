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
            
            // Vérifier si les rôles existent déjà
            if (roleRepository.count() == 0) {
                // Créer les rôles selon collector.sql
                Role admin = new Role();
                admin.setCode("admin");
                admin.setNom("Administrateur");
                admin.setDescription("Accès complet au système");
                roleRepository.save(admin);

                Role supervisor = new Role();
                supervisor.setCode("supervisor");
                supervisor.setNom("Superviseur");
                supervisor.setDescription("Supervise les agents collecteurs");
                roleRepository.save(supervisor);

                Role collector = new Role();
                collector.setCode("collector");
                collector.setNom("Agent collecteur");
                collector.setDescription("Agent de terrain pour la collecte d'épargne");
                roleRepository.save(collector);

                Role caissier = new Role();
                caissier.setCode("caissier");
                caissier.setNom("Caissier");
                caissier.setDescription("Caissier pour les opérations bancaires");
                roleRepository.save(caissier);

                Role auditor = new Role();
                auditor.setCode("auditor");
                auditor.setNom("Auditeur");
                auditor.setDescription("Contrôle et audit des opérations");
                roleRepository.save(auditor);

                Role client = new Role();
                client.setCode("client");
                client.setNom("Client");
                client.setDescription("Compte client épargnant (ajouté pour inscription app mobile)");
                roleRepository.save(client);

                System.out.println("✅ Rôles initialisés avec succès : 6 rôles créés");
            } else {
                System.out.println("✅ Rôles déjà présents : " + roleRepository.count() + " rôle(s)");
            }
            
            // Créer un utilisateur admin par défaut pour se connecter
            if (utilisateurRepository.findByEmail("admin@savings.local").isEmpty()) {
                Role adminRole = roleRepository.findByCode("admin")
                        .orElseThrow(() -> new RuntimeException("Rôle admin non trouvé"));
                
                Utilisateur admin = new Utilisateur();
                admin.setLogin("admin");
                admin.setRole(adminRole);
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
                System.out.println("✅ Utilisateur admin déjà présent");
            }
        };
    }
}

