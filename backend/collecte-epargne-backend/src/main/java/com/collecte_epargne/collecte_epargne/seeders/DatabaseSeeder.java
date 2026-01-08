package com.collecte_epargne.collecte_epargne.seeders;

import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TypeCompteRepository typeCompteRepository;
    @Autowired
    private AgenceZoneRepository agenceZoneRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionOfflineRepository transactionOfflineRepository;
    @Autowired
    private CompteCotisationRepository compteCotisationRepository;
    @Autowired
    private PlanCotisationRepository planCotisationRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private RapportCollecteurRepository rapportCollecteurRepository;
    @Autowired
    private RecuRepository recuRepository;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedTypeComptes();
        seedAgenceZones();
        seedUtilisateurs();
        seedEmployes();
        seedClients();
        seedComptes();
        seedPlanCotisations();
        seedCompteCotisations();
        seedTransactions();
        seedTransactionsOffline();
        seedNotifications();
        seedRapportsCollecteur();
        seedRecus();
        System.out.println("Database seeding completed successfully!");
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            Role superviseur = new Role();
            superviseur.setCode("SUP");
            superviseur.setNom("SUPERVISEUR");
            superviseur.setDescription("Rôle superviseur");

            Role client = new Role();
            client.setCode("CLI");
            client.setNom("CLIENT");
            client.setDescription("Rôle client");

            Role collecteur = new Role();
            collecteur.setCode("COLL");
            collecteur.setNom("COLLECTEUR");
            collecteur.setDescription("Rôle collecteur");

            Role caissier = new Role();
            caissier.setCode("CAIS");
            caissier.setNom("CAISSIER");
            caissier.setDescription("Rôle caissier");

            roleRepository.saveAll(Arrays.asList(superviseur, client, collecteur, caissier));
        }
    }

    private void seedTypeComptes() {
        if (typeCompteRepository.count() == 0) {
            TypeCompte epargne = new TypeCompte();
            epargne.setCode("EPARGNE");
            epargne.setNom("Compte Épargne");
            epargne.setTauxInteret(new BigDecimal("2.5"));
            epargne.setSoldeMinimum(new BigDecimal("100.00"));
            epargne.setAutoriserRetrait(true);

            TypeCompte courant = new TypeCompte();
            courant.setCode("COURANT");
            courant.setNom("Compte Courant");
            courant.setTauxInteret(new BigDecimal("0.0"));
            courant.setSoldeMinimum(new BigDecimal("0.00"));
            courant.setAutoriserRetrait(true);

            typeCompteRepository.saveAll(Arrays.asList(epargne, courant));
        }
    }

    private void seedAgenceZones() {
        if (agenceZoneRepository.count() == 0) {
            AgenceZone agence1 = new AgenceZone();
            agence1.setCode("AG001");
            agence1.setNom("Agence Centrale");
            agence1.setVille("Dakar");
            // Correction : Ajout des champs obligatoires pour validation
            agence1.setQuartier("Plateau");
            agence1.setTelephone("+221 33 123 45 67");
            agence1.setAdresse("123 Avenue de la République");
            agence1.setStatut(StatutGenerique.ACTIF);
            agence1.setDateCreation(Instant.now());

            agenceZoneRepository.save(agence1);
        }
    }

    private void seedUtilisateurs() {
        if (utilisateurRepository.count() == 0) {
            Role superviseurRole = roleRepository.findByCode("SUP").orElse(null);
            Role clientRole = roleRepository.findByCode("CLI").orElse(null);
            Role collecteurRole = roleRepository.findByCode("COLL").orElse(null);

            Utilisateur superviseur = new Utilisateur();
            superviseur.setLogin("superviseur1");
            superviseur.setRole(superviseurRole);
            superviseur.setNom("Dupont");
            superviseur.setPrenom("Jean");
            superviseur.setPassword("password123");
            superviseur.setTelephone("+221 77 123 45 67");
            superviseur.setEmail("superviseur1@example.com");
            superviseur.setStatut(StatutGenerique.ACTIF);
            superviseur.setDateCreation(Instant.now());

            Utilisateur client1 = new Utilisateur();
            client1.setLogin("client1");
            client1.setRole(clientRole);
            client1.setNom("Diallo");
            client1.setPrenom("Fatou");
            client1.setPassword("password123");
            client1.setTelephone("+221 77 234 56 78");
            client1.setEmail("client1@example.com");
            client1.setStatut(StatutGenerique.ACTIF);
            client1.setDateCreation(Instant.now());

            Utilisateur collecteur1 = new Utilisateur();
            collecteur1.setLogin("collecteur1");
            collecteur1.setRole(collecteurRole);
            collecteur1.setNom("Sarr");
            collecteur1.setPrenom("Mamadou");
            collecteur1.setPassword("password123");
            collecteur1.setTelephone("+221 77 345 67 89");
            collecteur1.setEmail("collecteur1@example.com");
            collecteur1.setStatut(StatutGenerique.ACTIF);
            collecteur1.setDateCreation(Instant.now());

            utilisateurRepository.saveAll(Arrays.asList(superviseur, client1, collecteur1));
        }
    }

    private void seedEmployes() {
        if (employeRepository.count() == 0) {
            Utilisateur superviseurUser = utilisateurRepository.findById("superviseur1").orElse(null);
            Utilisateur collecteurUser = utilisateurRepository.findById("collecteur1").orElse(null);
            AgenceZone agence1 = agenceZoneRepository.findAll().get(0);

            // 1. Créer le Superviseur
            Employe superviseur = new Employe();
            superviseur.setMatricule("SUP001");
            superviseur.setDateEmbauche(LocalDate.now().minusYears(5));
            superviseur.setTypeEmploye(TypeEmploye.SUPERVISEUR);
            superviseur.setCommissionTaux(new BigDecimal("5.0"));
            superviseur.setUtilisateur(superviseurUser);
            superviseur.setAgenceZone(agence1);

            // Méthode 2 : Auto-référence pour le superviseur racine
            superviseur.setSuperviseur(superviseur);
            superviseur = employeRepository.save(superviseur);

            // 2. Créer le Collecteur
            Employe collecteur = new Employe();
            collecteur.setMatricule("COL001");
            collecteur.setDateEmbauche(LocalDate.now().minusYears(2));
            collecteur.setTypeEmploye(TypeEmploye.COLLECTEUR);
            collecteur.setCommissionTaux(new BigDecimal("3.0"));
            collecteur.setUtilisateur(collecteurUser);
            collecteur.setAgenceZone(agence1);
            collecteur.setSuperviseur(superviseur);

            employeRepository.save(collecteur);
        }
    }

    private void seedClients() {
        if (clientRepository.count() == 0) {
            Utilisateur clientUser = utilisateurRepository.findById("client1").orElse(null);
            Employe collecteur = employeRepository.findByMatricule("COL001").orElse(null);

            Client client1 = new Client();
            client1.setCodeClient("CLI001");
            client1.setAdresse("123 Rue de la Paix, Dakar");
            client1.setTypeCni(TypeCNI.CARTE_IDENTITE);
            client1.setNumCni("123456789");
            client1.setPhotoPath("/uploads/photos/client1.jpg");
            client1.setCniRectoPath("/uploads/cni/recto_client1.jpg");
            client1.setCniVersoPath("/uploads/cni/verso_client1.jpg");
            client1.setDateNaissance(LocalDate.of(1990, 5, 15));
            client1.setLieuNaissance("Dakar");
            client1.setProfession("Enseignant");
            client1.setScoreEpargne(85);
            client1.setUtilisateur(clientUser);
            client1.setCollecteurAssigne(collecteur);

            clientRepository.save(client1);
        }
    }

    private void seedComptes() {
        if (compteRepository.count() == 0) {
            Client client1 = clientRepository.findByNumeroClient(1L).orElse(null);
            TypeCompte epargneType = typeCompteRepository.findByCode("EPARGNE").orElse(null);

            Compte compte1 = new Compte();
            compte1.setIdCompte("CPT001");
            compte1.setNumCompte("NUMCPT001");
            compte1.setSolde(new BigDecimal("500.00"));
            compte1.setSoldeDisponible(new BigDecimal("500.00"));
            compte1.setDateOuverture(LocalDate.now());
            compte1.setClient(client1);
            compte1.setTypeCompte(epargneType);
            compte1.setStatut(StatutCompte.ACTIF);

            compteRepository.save(compte1);
        }
    }

    private void seedTransactions() {
        if (transactionRepository.count() == 0) {
            Compte compte1 = compteRepository.findById("CPT001").orElse(null);
            Employe collecteur = employeRepository.findByMatricule("COL001").orElse(null);
            Employe superviseur = employeRepository.findByMatricule("SUP001").orElse(null);

            Transaction transaction1 = new Transaction();
            transaction1.setIdTransaction("TRX001");
            transaction1.setReference("REF001");
            transaction1.setSoldeAvant(new BigDecimal("500.00"));
            transaction1.setSoldeApres(new BigDecimal("700.00"));
            transaction1.setCompte(compte1);
            transaction1.setInitiateur(collecteur);
            transaction1.setCaissierValidateur(collecteur);
            transaction1.setSuperviseurValidateur(superviseur);
            transaction1.setTypeTransaction(TypeTransaction.DEPOT);
            transaction1.setMontant(new BigDecimal("200.00"));
            transaction1.setStatut(StatutTransaction.VALIDEE_CAISSE); // Adapté selon votre enum (VALIDE/EN_ATTENTE/TERMINEE)
            transaction1.setDateTransaction(Instant.now());

            transactionRepository.save(transaction1);
        }
    }

    private void seedTransactionsOffline() {
        if (transactionOfflineRepository.count() == 0) {
            Compte compte1 = compteRepository.findById("CPT001").orElse(null);
            Client client1 = clientRepository.findByNumeroClient(1L).orElse(null);
            Employe collecteur = employeRepository.findByMatricule("COL001").orElse(null);

            TransactionOffline offline1 = new TransactionOffline();
            offline1.setIdOffline("OFF001");
            offline1.setMontant(new BigDecimal("50.00"));
            offline1.setTypeTransaction(TypeTransaction.RETRAIT);
            offline1.setStatutSynchro(StatutSynchroOffline.EN_ATTENTE);
            offline1.setEmploye(collecteur);
            offline1.setClient(client1);
            offline1.setCompte(compte1);
            offline1.setDateTransaction(Instant.now());

            transactionOfflineRepository.save(offline1);
        }
    }

    private void seedPlanCotisations() {
        if (planCotisationRepository.count() == 0) {
            PlanCotisation plan1 = new PlanCotisation();
            plan1.setIdPlan("PLAN001");
            plan1.setNom("Plan Épargne Mensuel");
            plan1.setMontantAttendu(new BigDecimal("1000.00"));
            plan1.setFrequence(FrequenceCotisation.MENSUEL);
            plan1.setDureeJours(365);
            plan1.setDateDebut(LocalDate.now());
            plan1.setDateFin(LocalDate.now().plusDays(365));
            plan1.setTauxPenaliteRetard(new BigDecimal("2.0"));
            plan1.setStatut(StatutPlanCotisation.ACTIF);

            planCotisationRepository.save(plan1);
        }
    }

    private void seedCompteCotisations() {
        if (compteCotisationRepository.count() == 0) {
            Compte compte1 = compteRepository.findById("CPT001").orElse(null);
            PlanCotisation plan1 = planCotisationRepository.findById("PLAN001").orElse(null);

            CompteCotisation cotisation1 = new CompteCotisation();
            cotisation1.setId("COT001");
            cotisation1.setDateAdhesion(LocalDate.now());
            cotisation1.setMontantTotalVerse(new BigDecimal("200.00"));
            cotisation1.setNombreVersements(2);
            cotisation1.setNombreRetards(0);
            cotisation1.setProchaineEcheance(LocalDate.now().plusMonths(1));
            cotisation1.setStatut(StatutPlanCotisation.ACTIF);
            cotisation1.setCompte(compte1);
            cotisation1.setPlanCotisation(plan1);

            compteCotisationRepository.save(cotisation1);
        }
    }

    private void seedNotifications() {
        if (notificationRepository.count() == 0) {
            Transaction transaction1 = transactionRepository.findById("TRX001").orElse(null);

            Notification notification1 = new Notification();
            notification1.setIdNotification("NOT001");
            notification1.setCodeClient("CLI001");
            notification1.setTransaction(transaction1);
            notification1.setType(TypeNotification.EMAIL);
            notification1.setCategorie(CategorieNotification.TRANSACTION);
            notification1.setTitre("Dépôt effectué");
            notification1.setMessage("Votre dépôt de 200.00 FCFA a été effectué avec succès.");
            notification1.setStatut("ENVOYE");
            notification1.setDateCreation(Instant.now());
            notification1.setDateEnvoi(Instant.now());

            notificationRepository.save(notification1);
        }
    }

    private void seedRapportsCollecteur() {
        if (rapportCollecteurRepository.count() == 0) {
            RapportCollecteur rapport1 = new RapportCollecteur();
            rapport1.setIdRapport("RAP001");
            rapport1.setIdEmploye("COL001");
            rapport1.setDateRapport(LocalDate.now());
            rapport1.setTotalDepot(new BigDecimal("250.00"));
            rapport1.setTotalRetrait(new BigDecimal("0.00"));
            rapport1.setNombreTransactions(2);
            rapport1.setNombreClientsVisites(1);
            rapport1.setSoldeCollecteur(new BigDecimal("250.00"));
            rapport1.setStatutRapport(StatutTransaction.TERMINEE);
            rapport1.setDateGeneration(Instant.now());
            rapport1.setCommentaireSuperviseur("Rapport validé.");

            rapportCollecteurRepository.save(rapport1);
        }
    }

    private void seedRecus() {
        if (recuRepository.count() == 0) {
            Transaction transaction1 = transactionRepository.findById("TRX001").orElse(null);

            Recu recu1 = new Recu();
            recu1.setIdRecu("REC001");
            recu1.setTransaction(transaction1);
            recu1.setFormat(FormatRecu.PDF);
            recu1.setContenu("Contenu du reçu en format texte.");
            recu1.setFichierPath("/uploads/recus/recu001.pdf");
            recu1.setDateGeneration(Instant.now());

            recuRepository.save(recu1);
        }
    }
}
