package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.SuperviseurKpiDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.TransactionRepository;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.StatusValidation;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuperviseurKpiService {

    private static final Logger log = LoggerFactory.getLogger(SuperviseurKpiService.class);

    private final TransactionRepository transactionRepository;
    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;

    public SuperviseurKpiService(
            TransactionRepository transactionRepository,
            EmployeRepository employeRepository,
            ClientRepository clientRepository) {
        this.transactionRepository = transactionRepository;
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * Récupère tous les KPIs pour le superviseur filtrés par agence
     */
    public SuperviseurKpiDto getKpis(com.collecte_epargne.collecte_epargne.entities.AgenceZone agence) {
        log.info("Calcul des KPIs superviseur pour l'agence {}", agence.getNom());
        
        SuperviseurKpiDto kpis = new SuperviseurKpiDto();
        
        // Meilleur collecteur de l'agence (par montant collecté)
        kpis.setMeilleurCollecteur(getMeilleurCollecteur(agence));
        
        // Collecteur avec le plus de clients dans l'agence
        kpis.setCollecteurPlusClients(getCollecteurAvecPlusDeClients(agence));
        
        // Statistiques de collecte de l'agence
        kpis.setCollecteJournaliere(getCollecteParPeriode(agence, 1));
        kpis.setCollecteHebdomadaire(getCollecteParPeriode(agence, 7));
        kpis.setCollecteMensuelle(getCollecteParPeriode(agence, 30));
        
        // Caisse de l'agence (total des dépôts validés dans l'agence)
        kpis.setCaisseEntreprise(getCaisseEntreprise(agence));
        
        // Gains du superviseur
        kpis.setGainSuperviseur(calculateSuperviseurEarnings(agence));
        
        // Clients en retard dans l'agence
        kpis.setClientsEnRetard(getClientsEnRetard(agence));
        
        // Statistiques générales de l'agence
        kpis.setNombreTransactions(transactionRepository.countByInitiateur_AgenceZone(agence));
        kpis.setNombreClientsActifs(clientRepository.countByCollecteurAssigne_AgenceZone(agence));
        
        return kpis;
    }

    /**
     * Trouve le meilleur collecteur de l'agence par montant collecté
     */
    private SuperviseurKpiDto.CollecteurStatsDto getMeilleurCollecteur(com.collecte_epargne.collecte_epargne.entities.AgenceZone agence) {
        log.info("Recherche du meilleur collecteur pour l'agence {}", agence.getNom());
        
        List<Employe> collecteurs = employeRepository.findByAgenceZoneAndTypeEmploye(agence, TypeEmploye.COLLECTEUR);
        
        SuperviseurKpiDto.CollecteurStatsDto meilleur = null;
        BigDecimal maxMontant = BigDecimal.ZERO;
        
        for (Employe collecteur : collecteurs) {
            // Calculer le total des transactions validées pour ce collecteur (30 derniers jours)
            List<Transaction> transactions = transactionRepository.findByInitiateurAndDateTransactionBetween(
                collecteur,
                Instant.now().minus(30, ChronoUnit.DAYS),
                Instant.now()
            );
            
            BigDecimal total = transactions.stream()
                .filter(t -> t.getStatusValidation() == StatusValidation.VALIDEE)
                .filter(t -> t.getTypeTransaction() == TypeTransaction.DEPOT || 
                            t.getTypeTransaction() == TypeTransaction.COTISATION)
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (total.compareTo(maxMontant) > 0) {
                maxMontant = total;
                meilleur = new SuperviseurKpiDto.CollecteurStatsDto();
                meilleur.setMatricule(collecteur.getMatricule());
                meilleur.setNom(collecteur.getUtilisateur().getNom());
                meilleur.setPrenom(collecteur.getUtilisateur().getPrenom());
                meilleur.setMontantCollecte(total);
                meilleur.setNombreTransactions((long) transactions.size());
            }
        }
        
        return meilleur;
    }

    /**
     * Trouve le collecteur avec le plus de clients dans l'agence
     */
    private SuperviseurKpiDto.CollecteurStatsDto getCollecteurAvecPlusDeClients(com.collecte_epargne.collecte_epargne.entities.AgenceZone agence) {
        log.info("Recherche du collecteur avec le plus de clients pour l'agence {}", agence.getNom());
        
        List<Employe> collecteurs = employeRepository.findByAgenceZoneAndTypeEmploye(agence, TypeEmploye.COLLECTEUR);
        
        SuperviseurKpiDto.CollecteurStatsDto meilleur = null;
        long maxClients = 0;
        
        for (Employe collecteur : collecteurs) {
            long nombreClients = clientRepository.countByCollecteurAssigne(collecteur);
            
            if (nombreClients > maxClients) {
                maxClients = nombreClients;
                meilleur = new SuperviseurKpiDto.CollecteurStatsDto();
                meilleur.setMatricule(collecteur.getMatricule());
                meilleur.setNom(collecteur.getUtilisateur().getNom());
                meilleur.setPrenom(collecteur.getUtilisateur().getPrenom());
                meilleur.setNombreClients(nombreClients);
                
                // Calculer aussi son montant total collecté (30 derniers jours) pour info
                List<Transaction> transactions = transactionRepository.findByInitiateurAndDateTransactionBetween(
                    collecteur,
                    Instant.now().minus(30, ChronoUnit.DAYS),
                    Instant.now()
                );
                BigDecimal total = transactions.stream()
                    .filter(t -> t.getStatusValidation() == StatusValidation.VALIDEE)
                    .map(Transaction::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                meilleur.setMontantCollecte(total);
            }
        }
        
        return meilleur;
    }

    /**
     * Calcule la collecte pour une période donnée (en jours) pour l'agence
     */
    private BigDecimal getCollecteParPeriode(com.collecte_epargne.collecte_epargne.entities.AgenceZone agence, int jours) {
        Instant debut = Instant.now().minus(jours, ChronoUnit.DAYS);
        Instant fin = Instant.now();
        
        List<Transaction> transactions = transactionRepository.findByInitiateur_AgenceZoneAndDateTransactionBetween(
            agence, debut, fin
        ).stream()
            .filter(t -> t.getStatusValidation() == StatusValidation.VALIDEE)
            .filter(t -> t.getTypeTransaction() == TypeTransaction.DEPOT || 
                        t.getTypeTransaction() == TypeTransaction.COTISATION)
            .collect(Collectors.toList());
        
        return transactions.stream()
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule la caisse de l'agence (total des dépôts validés dans l'agence)
     */
    private BigDecimal getCaisseEntreprise(com.collecte_epargne.collecte_epargne.entities.AgenceZone agence) {
        List<Transaction> transactions = transactionRepository.findByInitiateur_AgenceZone(agence).stream()
            .filter(t -> t.getStatusValidation() == StatusValidation.VALIDEE)
            .collect(Collectors.toList());
        
        BigDecimal totalDepots = transactions.stream()
            .filter(t -> t.getTypeTransaction() == TypeTransaction.DEPOT || 
                        t.getTypeTransaction() == TypeTransaction.COTISATION)
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRetraits = transactions.stream()
            .filter(t -> t.getTypeTransaction() == TypeTransaction.RETRAIT)
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalDepots.subtract(totalRetraits);
    }

    /**
     * Calcule les gains du superviseur pour l'agence (commission de 5% sur les collectes mensuelles)
     */
    private BigDecimal calculateSuperviseurEarnings(com.collecte_epargne.collecte_epargne.entities.AgenceZone agence) {
        BigDecimal collecteMensuelle = getCollecteParPeriode(agence, 30);
        // Commission de 5% pour le superviseur
        return collecteMensuelle.multiply(new BigDecimal("0.05"));
    }

    /**
     * Récupère la liste des clients en retard de cotisation dans l'agence
     */
    private List<SuperviseurKpiDto.ClientRetardDto> getClientsEnRetard(com.collecte_epargne.collecte_epargne.entities.AgenceZone agence) {
        log.info("Recherche des clients en retard pour l'agence {}", agence.getNom());
        
        List<SuperviseurKpiDto.ClientRetardDto> clientsEnRetard = new ArrayList<>();
        
        // Pour simplifier, on considère qu'un client est en retard s'il n'a pas fait de cotisation depuis 30 jours
        Instant dateLimit = Instant.now().minus(30, ChronoUnit.DAYS);
        
        List<Client> clients = clientRepository.findByCollecteurAssigne_AgenceZone(agence);
        
        for (Client client : clients) {
            // Optimisation: Utiliser le repository pour chercher uniquement les transactions de ce client
            List<Transaction> transactions = transactionRepository.findByCompte_ClientAndTypeTransactionAndStatusValidationOrderByDateTransactionDesc(
                client, TypeTransaction.COTISATION, StatusValidation.VALIDEE
            );
            
            if (transactions.isEmpty() || transactions.get(0).getDateTransaction().isBefore(dateLimit)) {
                SuperviseurKpiDto.ClientRetardDto retard = new SuperviseurKpiDto.ClientRetardDto();
                retard.setCodeClient(client.getCodeClient());
                retard.setNom(client.getUtilisateur().getNom());
                retard.setPrenom(client.getUtilisateur().getPrenom());
                retard.setTelephone(client.getUtilisateur().getTelephone());
                
                // Calculer les jours de retard
                Instant derniereTransaction = transactions.isEmpty() ? 
                    client.getUtilisateur().getDateCreation() : 
                    transactions.get(0).getDateTransaction();
                long joursRetard = ChronoUnit.DAYS.between(derniereTransaction, Instant.now());
                retard.setJoursRetard((int) joursRetard);
                
                // Montant dû (exemple: 5000 FCFA par mois)
                BigDecimal montantMensuel = new BigDecimal("5000");
                retard.setMontantDu(montantMensuel);
                
                // Intérêts de retard (1% par jour de retard)
                BigDecimal interets = montantMensuel
                    .multiply(new BigDecimal("0.01"))
                    .multiply(new BigDecimal(joursRetard));
                retard.setInteretsRetard(interets);
                
                clientsEnRetard.add(retard);
            }
        }
        
        return clientsEnRetard;
    }
}
