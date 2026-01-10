package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.DashboardStatsDto;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.TransactionRepository;
import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import com.collecte_epargne.collecte_epargne.utils.TypeTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final ClientRepository clientRepository;
    private final EmployeRepository employeRepository;
    private final TransactionRepository transactionRepository;

    public DashboardService(ClientRepository clientRepository, EmployeRepository employeRepository, TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.employeRepository = employeRepository;
        this.transactionRepository = transactionRepository;
    }

    public DashboardStatsDto getStats() {
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

        return new DashboardStatsDto(clients, collecteurs, caissiers, volumeDepot, pourcentage);
    }
}
