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

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedTypeComptes();
        seedAgenceZones();
        seedUtilisateurs();
        seedEmployes();
        seedClients();
        seedComptes();
        seedTransactions();
        seedTransactionsOffline();
        System.out.println("Database seeding completed!");
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            Role superviseur = new Role();
            superviseur.setCode("SUP");
            superviseur.setNom("SUPERVISEUR");
            superviseur.setDescription("Rôle superviseur avec tous les droits");

            Role client = new Role();
            client.setCode("CLI");
            client.setNom("CLIENT");
            client.setDescription("Rôle client standard");

            Role collecteur = new Role();
            collecteur.setCode("COLLECTEUR");
            collecteur.setNom("COLLECTEUR");
            collecteur.setDescription("Rôle pour les collecteurs d'épargne");

            Role caissier = new Role();
            caissier.setCode("CAISSIER");
            caissier.setNom("CAISSIER");
            caissier.setDescription("Rôle pour les caissier d'agence");

            roleRepository.saveAll(Arrays.asList(superviseur, client, collecteur, caissier));
        }
    }

    private void seedTypeComptes() {
        if (typeCompteRepository.count() == 0) {
            TypeCompte epargne = new TypeCompte();
            epargne.setCode("EPARGNE");
            epargne.setNom("Compte Épargne");
            epargne.setDescription("Compte pour l'épargne personnelle");
            epargne.setTauxInteret(new BigDecimal("2.5"));
            epargne.setSoldeMinimum(new BigDecimal("100.00"));
            epargne.setFraisOuverture(new BigDecimal("10.00"));
            epargne.setFraisCloture(new BigDecimal("5.00"));
            epargne.setAutoriserRetrait(true);
            epargne.setDureeBlocageJours(0);

            TypeCompte courant = new TypeCompte();
            courant.setCode("COURANT");
            courant.setNom("Compte Courant");
            courant.setDescription("Compte pour les transactions courantes");
            courant.setTauxInteret(new BigDecimal("0.0"));
            courant.setSoldeMinimum(new BigDecimal("0.00"));
            courant.setFraisOuverture(new BigDecimal("0.00"));
            courant.setFraisCloture(new BigDecimal("0.00"));
            courant.setAutoriserRetrait(true);
            courant.setDureeBlocageJours(0);

            typeCompteRepository.saveAll(Arrays.asList(epargne, courant));
        }
    }

    private void seedAgenceZones() {
        if (agenceZoneRepository.count() == 0) {
            AgenceZone agence1 = new AgenceZone();
            agence1.setCode("AG001");
            agence1.setNom("Agence Centrale");
            agence1.setVille("Dakar");
            agence1.setQuartier("Plateau");
            agence1.setAdresse("123 Avenue de la République");
            agence1.setTelephone("+221 33 123 45 67");
            agence1.setDescription("Agence principale de Dakar");
            agence1.setStatut(StatutGenerique.ACTIF);
            agence1.setDateCreation(Instant.now());

            AgenceZone agence2 = new AgenceZone();
            agence2.setCode("AG002");
            agence2.setNom("Agence Nord");
            agence2.setVille("Saint-Louis");
            agence2.setQuartier("Centre");
            agence2.setAdresse("456 Rue de la Gare");
            agence2.setTelephone("+221 33 987 65 43");
            agence2.setDescription("Agence de Saint-Louis");
            agence2.setStatut(StatutGenerique.ACTIF);
            agence2.setDateCreation(Instant.now());

            agenceZoneRepository.saveAll(Arrays.asList(agence1, agence2));
        }
    }

    private void seedUtilisateurs() {
        if (utilisateurRepository.count() == 0) {
            Role superviseurRole = roleRepository.findByCode("SUP").orElse(null);
            Role clientRole = roleRepository.findByCode("CLI").orElse(null);
            Role collecteurRole = roleRepository.findByCode("COLLECTEUR").orElse(null);
            Role caissierRole = roleRepository.findByCode("CAISSIER").orElse(null);

            Utilisateur superviseur = new Utilisateur();
            superviseur.setLogin("superviseur1");
            superviseur.setRole(superviseurRole);
            superviseur.setNom("Dupont");
            superviseur.setPrenom("Jean");
            superviseur.setTelephone("+221 77 123 45 67");
            superviseur.setEmail("jean.dupont@example.com");
            superviseur.setPassword("password123");
            superviseur.setStatut(StatutGenerique.ACTIF);
            superviseur.setDateCreation(Instant.now());

            Utilisateur client1 = new Utilisateur();
            client1.setLogin("client1");
            client1.setRole(clientRole);
            client1.setNom("Diallo");
            client1.setPrenom("Fatou");
            client1.setTelephone("+221 77 987 65 43");
            client1.setEmail("fatou.diallo@example.com");
            client1.setPassword("password123");
            client1.setStatut(StatutGenerique.ACTIF);
            client1.setDateCreation(Instant.now());

            Utilisateur collecteur1 = new Utilisateur();
            collecteur1.setLogin("collecteur1");
            collecteur1.setRole(collecteurRole);
            collecteur1.setNom("Sarr");
            collecteur1.setPrenom("Mamadou");
            collecteur1.setTelephone("+221 77 555 44 33");
            collecteur1.setEmail("mamadou.sarr@example.com");
            collecteur1.setPassword("password123");
            collecteur1.setStatut(StatutGenerique.ACTIF);
            collecteur1.setDateCreation(Instant.now());

            utilisateurRepository.saveAll(Arrays.asList(superviseur, client1, collecteur1));
        }
    }

    private void seedEmployes() {
        if (employeRepository.count() == 0) {
            Utilisateur superviseurUser = utilisateurRepository.findById("superviseur1").orElse(null);
            Utilisateur collecteurUser = utilisateurRepository.findById("collecteur1").orElse(null);
            AgenceZone agence1 = agenceZoneRepository.findById(1).orElse(null);

            Employe superviseur = new Employe();
            superviseur.setMatricule("SUP001");
            superviseur.setDateEmbauche(LocalDate.now().minusYears(5));
            superviseur.setTypeEmploye(TypeEmploye.SUPERVISEUR);
            superviseur.setCommissionTaux(new BigDecimal("5.0"));
            superviseur.setUtilisateur(superviseurUser);
            superviseur.setAgenceZone(agence1);

            Employe collecteur = new Employe();
            collecteur.setMatricule("COL001");
            collecteur.setDateEmbauche(LocalDate.now().minusYears(2));
            collecteur.setTypeEmploye(TypeEmploye.COLLECTEUR);
            collecteur.setCommissionTaux(new BigDecimal("3.0"));
            collecteur.setUtilisateur(collecteurUser);
            collecteur.setAgenceZone(agence1);
            collecteur.setSuperviseur(superviseur);

            employeRepository.saveAll(Arrays.asList(superviseur, collecteur));
        }
    }

    private void seedClients() {
        if (clientRepository.count() == 0) {
            Utilisateur clientUser = utilisateurRepository.findById("client1").orElse(null);
            Employe collecteur = employeRepository.findByMatricule("COL001").orElse(null);

            Client client1 = new Client();
            client1.setCodeClient("CLI001");
            client1.setNumeroClient("NUM001");
            client1.setAdresse("123 Rue de la Paix, Dakar");
            client1.setTypeCni(TypeCNI.CARTE_IDENTITE);
            client1.setNumCni("123456789");
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
            Client client1 = clientRepository.findByNumeroClient("NUM001").orElse(null);
            TypeCompte epargneType = typeCompteRepository.findByCode("EPARGNE").orElse(null);

            Compte compte1 = new Compte();
            compte1.setIdCompte("CPT001");
            compte1.setNumCompte("NUMCPT001");
            compte1.setSolde(new BigDecimal("500.00"));
            compte1.setSoldeDisponible(new BigDecimal("500.00"));
            compte1.setDateOuverture(LocalDate.now().minusMonths(6));
            compte1.setDateDerniereTransaction(Instant.now());
            compte1.setStatut(StatutCompte.ACTIF);
            compte1.setClient(client1);
            compte1.setTypeCompte(epargneType);

            compteRepository.save(compte1);
        }
    }

    private void seedTransactions() {
        if (transactionRepository.count() == 0) {
            Compte compte1 = compteRepository.findById("CPT001").orElse(null);
            Employe collecteur = employeRepository.findByMatricule("COL001").orElse(null);

            Transaction transaction1 = new Transaction();
            transaction1.setIdTransaction("TRX001");
            transaction1.setCompte(compte1);
            transaction1.setInitiateur(collecteur);
            transaction1.setCaissierValidateur(collecteur);
            transaction1.setSuperviseurValidateur(employeRepository.findByMatricule("SUP001").orElse(null));
            transaction1.setReference("REF001");
            transaction1.setTypeTransaction(TypeTransaction.DEPOT);
            transaction1.setMontant(new BigDecimal("200.00"));
            transaction1.setSoldeAvant(new BigDecimal("300.00"));
            transaction1.setSoldeApres(new BigDecimal("500.00"));
            transaction1.setDescription("Dépôt initial");
            transaction1.setDateTransaction(Instant.now());
            transaction1.setDateValidationCaisse(Instant.now());
            transaction1.setDateValidationSuperviseur(Instant.now());
            transaction1.setStatut(StatutTransaction.EN_ATTENTE);
            transaction1.setModeTransaction(ModeTransaction.ONLINE);

            transactionRepository.save(transaction1);
        }
    }

    private void seedTransactionsOffline() {
        if (transactionOfflineRepository.count() == 0) {
            Compte compte1 = compteRepository.findById("CPT001").orElse(null);
            Client client1 = clientRepository.findByNumeroClient("NUM001").orElse(null);
            Employe collecteur = employeRepository.findByMatricule("COL001").orElse(null);

            TransactionOffline offline1 = new TransactionOffline();
            offline1.setIdOffline("OFF001");
            offline1.setMontant(new BigDecimal("50.00"));
            offline1.setTypeTransaction(TypeTransaction.RETRAIT);
            offline1.setDateTransaction(Instant.now());
            offline1.setDescription("Retrait mobile");
            offline1.setLatitude(new BigDecimal("14.6937"));
            offline1.setLongitude(new BigDecimal("-17.4441"));
            offline1.setStatutSynchro(StatutSynchroOffline.EN_ATTENTE);
            offline1.setEmploye(collecteur);
            offline1.setClient(client1);
            offline1.setCompte(compte1);

            transactionOfflineRepository.save(offline1);
        }
    }
}
