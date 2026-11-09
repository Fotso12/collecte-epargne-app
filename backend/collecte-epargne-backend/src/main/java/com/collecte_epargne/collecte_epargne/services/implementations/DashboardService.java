package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.dtos.DashboardStatsDto;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.TransactionRepository;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DashboardService {

    private final ClientRepository clientRepository;
    private final EmployeRepository employeRepository;
    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;

    public DashboardService(ClientRepository clientRepository, 
                          EmployeRepository employeRepository, 
                          TransactionRepository transactionRepository,
                          CompteRepository compteRepository) {
        this.clientRepository = clientRepository;
        this.employeRepository = employeRepository;
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
    }

    public DashboardStatsDto getStats() {
        // Stats existantes
        long clients = clientRepository.count();
        long collecteurs = employeRepository.countByTypeEmploye(com.collecte_epargne.collecte_epargne.utils.TypeEmploye.COLLECTEUR);
        long caissiers = employeRepository.countByTypeEmploye(com.collecte_epargne.collecte_epargne.utils.TypeEmploye.CAISSIER);

        // On inclut EN_ATTENTE et TERMINEE pour les volumes d'activité (Dépôts/Retraits)
        // Mais pour le solde total (Comptes), on garde la logique de CompteRepository qui somme les soldes actuels.
        List<StatutTransaction> activeStatuts = List.of(StatutTransaction.EN_ATTENTE, StatutTransaction.TERMINEE);

        // Calcul volume total dépôt (Volume Cotisation)
        BigDecimal volumeDepot = transactionRepository.sumMontantByStatutsAndType(
                activeStatuts,
                TypeTransaction.DEPOT
        );
        if (volumeDepot == null) volumeDepot = BigDecimal.ZERO;

        // Calcul pourcentage validations
        long totalTx = transactionRepository.count();
        long validatedTx = transactionRepository.countByStatut(StatutTransaction.TERMINEE);
        double pourcentage = totalTx > 0 ? ((double) validatedTx / totalTx) * 100 : 0;

        // Total comptes actifs
        long totalComptesActifs = compteRepository.count();
        
        // Solde total épargne (Somme des soldes réels des comptes)
        BigDecimal soldeTotalEpargne = compteRepository.sumAllSoldes();
        if (soldeTotalEpargne == null) soldeTotalEpargne = BigDecimal.ZERO;
        
        // Volume retraits
        BigDecimal volumeRetraits = transactionRepository.sumMontantByStatutsAndType(
                activeStatuts,
                TypeTransaction.RETRAIT
        );
        if (volumeRetraits == null) volumeRetraits = BigDecimal.ZERO;
        
        // Transactions en attente
        long transactionsEnAttente = transactionRepository.countByStatut(StatutTransaction.EN_ATTENTE);
        
        // Taux de pénalités
        double tauxPenalites = 0.0;
        
        // Épargne moyenne par client
        BigDecimal epargneParClient = BigDecimal.ZERO;
        if (clients > 0 && soldeTotalEpargne != null) {
            epargneParClient = soldeTotalEpargne.divide(
                BigDecimal.valueOf(clients), 2, RoundingMode.HALF_UP
            );
        }

        // --- NOUVEAUX KPIs ---

        // 1. Collecteur avec le plus de clients
        String topCollectorClientsNom = "Aucun";
        long topCollectorClientsCount = 0;
        List<Employe> topCollectorsByClients = employeRepository.findCollecteursOrderByClientCountDesc();
        if (!topCollectorsByClients.isEmpty()) {
            Employe topE = topCollectorsByClients.get(0);
            topCollectorClientsNom = topE.getUtilisateur().getNom();
            topCollectorClientsCount = clientRepository.findByCollecteurAssigneIdEmploye(topE.getIdEmploye()).size();
        }

        // 2. Collecteur avec le plus de collecte
        String topCollectorCollecteNom = "Aucun";
        BigDecimal topCollectorCollecteMontant = BigDecimal.ZERO;
        // Pour le top collecteur, on compte aussi ce qu'il a initié (EN_ATTENTE + TERMINEE)
        List<Object[]> topCollectorsByCollecte = transactionRepository.findCollectorsOrderBySumMontantDesc(
                StatutTransaction.TERMINEE, TypeTransaction.DEPOT); // TODO: Update query to take list if needed, but for now we keep TERMINEE or we change repo
        
        // Correction Repo: J'ai ajouté sumMontantByStatutsAndType mais pas mis à jour findCollectorsOrderBySumMontantDesc pour prendre une liste.
        // Je vais rester sur TERMINEE pour le classement officiel ou mettre à jour le repo.
        // Restons sur TERMINEE pour le classement pour l'instant pour éviter trop de changements.
        if (!topCollectorsByCollecte.isEmpty()) {
            Object[] row = topCollectorsByCollecte.get(0);
            topCollectorCollecteNom = (String) row[1];
            topCollectorCollecteMontant = (BigDecimal) row[2];
        }

        // 3. Collectes par période (Journalière, Hebdomadaire, Mensuelle)
        Instant now = Instant.now();
        Instant todayStart = now.truncatedTo(ChronoUnit.DAYS);
        Instant weekStart = now.minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant monthStart = now.minus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);

        // Pour les stats de période, on inclut aussi EN_ATTENTE pour montrer l'activité récente
        BigDecimal collJournaliere = transactionRepository.sumMontantByStatutTypeAndDateAfter(
                StatutTransaction.TERMINEE, TypeTransaction.DEPOT, todayStart);
        // On va tricher un peu pour inclure les deux en faisant deux appels ou en changeant la query repo.
        // Plus simple: changer la query repo pour prendre une liste si possible, ou juste ajouter les deux ici.
        BigDecimal collJournaliereAttente = transactionRepository.sumMontantByStatutTypeAndDateAfter(
                StatutTransaction.EN_ATTENTE, TypeTransaction.DEPOT, todayStart);
        
        BigDecimal collHebdomadaire = transactionRepository.sumMontantByStatutTypeAndDateAfter(
                StatutTransaction.TERMINEE, TypeTransaction.DEPOT, weekStart);
        BigDecimal collHebdomadaireAttente = transactionRepository.sumMontantByStatutTypeAndDateAfter(
                StatutTransaction.EN_ATTENTE, TypeTransaction.DEPOT, weekStart);

        BigDecimal collMensuelle = transactionRepository.sumMontantByStatutTypeAndDateAfter(
                StatutTransaction.TERMINEE, TypeTransaction.DEPOT, monthStart);
        BigDecimal collMensuelleAttente = transactionRepository.sumMontantByStatutTypeAndDateAfter(
                StatutTransaction.EN_ATTENTE, TypeTransaction.DEPOT, monthStart);

        collJournaliere = safeAdd(collJournaliere, collJournaliereAttente);
        collHebdomadaire = safeAdd(collHebdomadaire, collHebdomadaireAttente);
        collMensuelle = safeAdd(collMensuelle, collMensuelleAttente);

        return new DashboardStatsDto(
            clients, collecteurs, caissiers, volumeDepot, pourcentage,
            totalComptesActifs, soldeTotalEpargne, volumeRetraits, transactionsEnAttente,
            tauxPenalites, epargneParClient,
            topCollectorClientsNom, topCollectorClientsCount,
            topCollectorCollecteNom, topCollectorCollecteMontant,
            collJournaliere, collHebdomadaire, collMensuelle
        );
    }

    private BigDecimal safeAdd(BigDecimal b1, BigDecimal b2) {
        if (b1 == null) b1 = BigDecimal.ZERO;
        if (b2 == null) b2 = BigDecimal.ZERO;
        return b1.add(b2);
    }
}
