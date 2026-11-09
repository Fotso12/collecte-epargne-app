package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * COMMENT CLEF: Service Gains - Calcul et enregistrement des gains par rôle
 * Logique métier: Collecteur 5%, Caissier 2%, Superviseur 1%, Institution 10%
 * Tous les calculs utilisent BigDecimal pour précision financière
 */
@Service
@Transactional
public class GainsService {
    
    private static final Logger log = LoggerFactory.getLogger(GainsService.class);
    
    // COMMENT CLEF: Commissions FIXES par rôle (en %)
    private static final BigDecimal COMMISSION_COLLECTEUR = new BigDecimal("0.05");     // 5%
    private static final BigDecimal COMMISSION_CAISSIER = new BigDecimal("0.02");       // 2%
    private static final BigDecimal COMMISSION_SUPERVISEUR = new BigDecimal("0.01");    // 1%
    private static final BigDecimal COMMISSION_INSTITUTION = new BigDecimal("0.10");    // 10%
    
    @Value("${app.frais.ouverture-compte:5000}")
    private BigDecimal fraisOuvertureCompte;
    
    @Value("${app.taux.epargne:2}")
    private BigDecimal tauxEpargneAnnuel;
    
    private final EmployeRepository employeRepository;

    public GainsService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    /**
     * COMMENT CLEF: Calcule gains COLLECTEUR = montant × 5%
     */
    public BigDecimal calculerGainsCollecteur(BigDecimal montantCollecte) {
        if (montantCollecte == null || montantCollecte.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return montantCollecte.multiply(COMMISSION_COLLECTEUR)
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * COMMENT CLEF: Calcule gains CAISSIER = montant × 2%
     */
    public BigDecimal calculerGainsCaissier(BigDecimal montantValide) {
        if (montantValide == null || montantValide.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return montantValide.multiply(COMMISSION_CAISSIER)
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * COMMENT CLEF: Calcule gains SUPERVISEUR = montant × 1%
     */
    public BigDecimal calculerGainsSuperviseur(BigDecimal montantCollecte) {
        if (montantCollecte == null || montantCollecte.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return montantCollecte.multiply(COMMISSION_SUPERVISEUR)
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * COMMENT CLEF: Calcule gains INSTITUTION = montant × 10%
     */
    public BigDecimal calculerGainsInstitution(BigDecimal montantCollecte) {
        if (montantCollecte == null || montantCollecte.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return montantCollecte.multiply(COMMISSION_INSTITUTION)
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * COMMENT CLEF: Applique frais ouverture compte
     */
    public BigDecimal obtenirFraisOuvertureCompte() {
        return fraisOuvertureCompte;
    }

    /**
     * COMMENT CLEF: Calcule intérêt épargne annuel = solde × taux%
     */
    public BigDecimal calculerInteretEpargne(BigDecimal solde, int nombreJours) {
        if (solde == null || solde.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Intérêt annuel appliqué proportionnellement aux jours
        BigDecimal tauxJournalier = tauxEpargneAnnuel.divide(new BigDecimal("365"), 4, RoundingMode.HALF_UP);
        BigDecimal interet = solde.multiply(tauxJournalier)
            .multiply(new BigDecimal(nombreJours))
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        log.info("Intérêt épargne calculé: {} (solde: {}, jours: {}, taux: {}%)", 
            interet, solde, nombreJours, tauxEpargneAnnuel);
        
        return interet;
    }

    /**
     * COMMENT CLEF: Enregistre gains pour un employé
     * À implémenter avec table Gains en BD
     */
    public void enregistrerGains(Integer idEmploye, BigDecimal montantGains, 
                                 String typeGain, String idTransaction, Integer idAgence) {
        log.info("Enregistrement gains - Employé: {}, Type: {}, Montant: {}, Transaction: {}", 
            idEmploye, typeGain, montantGains, idTransaction);
        
        // COMMENT CLEF: À implémenter - Sauvegarder dans table Gains
        // gainsRepository.save(new Gains(idEmploye, montantGains, typeGain, idTransaction, idAgence));
        
        // For now, just log
    }

    /**
     * COMMENT CLEF: Calcul complet de distribution gains pour une transaction
     * Retourne breakdown: Collecteur, Caissier, Superviseur, Institution
     */
    public GainsBreakdownDTO calculerDistributionGains(BigDecimal montantTransaction) {
        GainsBreakdownDTO breakdown = new GainsBreakdownDTO();
        breakdown.setMontantTransaction(montantTransaction);
        breakdown.setGainsCollecteur(calculerGainsCollecteur(montantTransaction));
        breakdown.setGainsCaissier(calculerGainsCaissier(montantTransaction));
        breakdown.setGainsSuperviseur(calculerGainsSuperviseur(montantTransaction));
        breakdown.setGainsInstitution(calculerGainsInstitution(montantTransaction));
        
        // COMMENT CLEF: Vérifier que la somme des gains ne dépasse pas le montant
        BigDecimal totalGains = breakdown.getGainsCollecteur()
            .add(breakdown.getGainsCaissier())
            .add(breakdown.getGainsSuperviseur())
            .add(breakdown.getGainsInstitution());
        
        log.debug("Distribution gains - Montant: {}, Collecteur: {}, Caissier: {}, Superviseur: {}, Institution: {}, Total: {}",
            montantTransaction, breakdown.getGainsCollecteur(), breakdown.getGainsCaissier(),
            breakdown.getGainsSuperviseur(), breakdown.getGainsInstitution(), totalGains);
        
        return breakdown;
    }

    /**
     * COMMENT CLEF: DTO pour breakdown des gains
     */
    public static class GainsBreakdownDTO {
        private BigDecimal montantTransaction;
        private BigDecimal gainsCollecteur;
        private BigDecimal gainsCaissier;
        private BigDecimal gainsSuperviseur;
        private BigDecimal gainsInstitution;

        public BigDecimal getMontantTransaction() { return montantTransaction; }
        public void setMontantTransaction(BigDecimal montantTransaction) { this.montantTransaction = montantTransaction; }

        public BigDecimal getGainsCollecteur() { return gainsCollecteur; }
        public void setGainsCollecteur(BigDecimal gainsCollecteur) { this.gainsCollecteur = gainsCollecteur; }

        public BigDecimal getGainsCaissier() { return gainsCaissier; }
        public void setGainsCaissier(BigDecimal gainsCaissier) { this.gainsCaissier = gainsCaissier; }

        public BigDecimal getGainsSuperviseur() { return gainsSuperviseur; }
        public void setGainsSuperviseur(BigDecimal gainsSuperviseur) { this.gainsSuperviseur = gainsSuperviseur; }

        public BigDecimal getGainsInstitution() { return gainsInstitution; }
        public void setGainsInstitution(BigDecimal gainsInstitution) { this.gainsInstitution = gainsInstitution; }
    }
}
