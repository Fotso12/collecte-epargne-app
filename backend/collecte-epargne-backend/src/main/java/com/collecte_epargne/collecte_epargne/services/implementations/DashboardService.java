package com.collecte_epargne.collecte_epargne.services.implementations;

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

        // Calcul volume total dépôt
        BigDecimal volumeDepot = transactionRepository.sumMontantByStatutAndType(
                StatutTransaction.TERMINEE,
                TypeTransaction.DEPOT
        );
        if (volumeDepot == null) volumeDepot = BigDecimal.ZERO;

        // Calcul pourcentage validations
        long totalTx = transactionRepository.count();
        long validatedTx = transactionRepository.countByStatut(StatutTransaction.TERMINEE);
        double pourcentage = totalTx > 0 ? ((double) validatedTx / totalTx) * 100 : 0;

        // NOUVEAUX KPIs
        
        // Total comptes actifs
        long totalComptesActifs = compteRepository.count();
        
        // Solde total épargne (somme de tous les soldes)
        BigDecimal soldeTotalEpargne = compteRepository.sumAllSoldes();
        if (soldeTotalEpargne == null) soldeTotalEpargne = BigDecimal.ZERO;
        
        // Volume retraits
        BigDecimal volumeRetraits = transactionRepository.sumMontantByStatutAndType(
                StatutTransaction.TERMINEE,
                TypeTransaction.RETRAIT
        );
        if (volumeRetraits == null) volumeRetraits = BigDecimal.ZERO;
        
        // Transactions en attente
        long transactionsEnAttente = transactionRepository.countByStatut(StatutTransaction.EN_ATTENTE);
        
        // Taux de pénalités (simulé - à calculer selon la logique métier)
        // TODO: Implémenter le calcul réel basé sur les retards de cotisation
        double tauxPenalites = 0.0;
        
        // Épargne moyenne par client
        BigDecimal epargneParClient = BigDecimal.ZERO;
        if (clients > 0 && soldeTotalEpargne != null) {
            epargneParClient = soldeTotalEpargne.divide(
                BigDecimal.valueOf(clients), 2, RoundingMode.HALF_UP
            );
        }

        return new DashboardStatsDto(
            clients, 
            collecteurs, 
            caissiers, 
            volumeDepot, 
            pourcentage,
            totalComptesActifs,
            soldeTotalEpargne,
            volumeRetraits,
            transactionsEnAttente,
            tauxPenalites,
            epargneParClient
        );
    }
}
