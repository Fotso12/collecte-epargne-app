package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour les KPIs du superviseur
 */
public class SuperviseurKpiDto {
    
    // Meilleur collecteur
    private CollecteurStatsDto meilleurCollecteur;
    
    // Collecteur avec le plus de clients
    private CollecteurStatsDto collecteurPlusClients;
    
    // Statistiques de collecte
    private BigDecimal collecteJournaliere;
    private BigDecimal collecteHebdomadaire;
    private BigDecimal collecteMensuelle;
    
    // Finances
    private BigDecimal caisseEntreprise;
    private BigDecimal gainSuperviseur;
    
    // Clients en retard
    private List<ClientRetardDto> clientsEnRetard;
    
    // Nombre total de transactions
    private Long nombreTransactions;
    
    // Nombre de clients actifs
    private Long nombreClientsActifs;

    // Getters and Setters
    public CollecteurStatsDto getMeilleurCollecteur() {
        return meilleurCollecteur;
    }

    public void setMeilleurCollecteur(CollecteurStatsDto meilleurCollecteur) {
        this.meilleurCollecteur = meilleurCollecteur;
    }

    public CollecteurStatsDto getCollecteurPlusClients() {
        return collecteurPlusClients;
    }

    public void setCollecteurPlusClients(CollecteurStatsDto collecteurPlusClients) {
        this.collecteurPlusClients = collecteurPlusClients;
    }

    public BigDecimal getCollecteJournaliere() {
        return collecteJournaliere;
    }

    public void setCollecteJournaliere(BigDecimal collecteJournaliere) {
        this.collecteJournaliere = collecteJournaliere;
    }

    public BigDecimal getCollecteHebdomadaire() {
        return collecteHebdomadaire;
    }

    public void setCollecteHebdomadaire(BigDecimal collecteHebdomadaire) {
        this.collecteHebdomadaire = collecteHebdomadaire;
    }

    public BigDecimal getCollecteMensuelle() {
        return collecteMensuelle;
    }

    public void setCollecteMensuelle(BigDecimal collecteMensuelle) {
        this.collecteMensuelle = collecteMensuelle;
    }

    public BigDecimal getCaisseEntreprise() {
        return caisseEntreprise;
    }

    public void setCaisseEntreprise(BigDecimal caisseEntreprise) {
        this.caisseEntreprise = caisseEntreprise;
    }

    public BigDecimal getGainSuperviseur() {
        return gainSuperviseur;
    }

    public void setGainSuperviseur(BigDecimal gainSuperviseur) {
        this.gainSuperviseur = gainSuperviseur;
    }

    public List<ClientRetardDto> getClientsEnRetard() {
        return clientsEnRetard;
    }

    public void setClientsEnRetard(List<ClientRetardDto> clientsEnRetard) {
        this.clientsEnRetard = clientsEnRetard;
    }

    public Long getNombreTransactions() {
        return nombreTransactions;
    }

    public void setNombreTransactions(Long nombreTransactions) {
        this.nombreTransactions = nombreTransactions;
    }

    public Long getNombreClientsActifs() {
        return nombreClientsActifs;
    }

    public void setNombreClientsActifs(Long nombreClientsActifs) {
        this.nombreClientsActifs = nombreClientsActifs;
    }

    /**
     * DTO pour les statistiques d'un collecteur
     */
    public static class CollecteurStatsDto {
        private String matricule;
        private String nom;
        private String prenom;
        private BigDecimal montantCollecte;
        private Long nombreClients;
        private Long nombreTransactions;

        public String getMatricule() {
            return matricule;
        }

        public void setMatricule(String matricule) {
            this.matricule = matricule;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public BigDecimal getMontantCollecte() {
            return montantCollecte;
        }

        public void setMontantCollecte(BigDecimal montantCollecte) {
            this.montantCollecte = montantCollecte;
        }

        public Long getNombreClients() {
            return nombreClients;
        }

        public void setNombreClients(Long nombreClients) {
            this.nombreClients = nombreClients;
        }

        public Long getNombreTransactions() {
            return nombreTransactions;
        }

        public void setNombreTransactions(Long nombreTransactions) {
            this.nombreTransactions = nombreTransactions;
        }
    }

    /**
     * DTO pour un client en retard de cotisation
     */
    public static class ClientRetardDto {
        private String codeClient;
        private String nom;
        private String prenom;
        private String telephone;
        private Integer joursRetard;
        private BigDecimal montantDu;
        private BigDecimal interetsRetard;

        public String getCodeClient() {
            return codeClient;
        }

        public void setCodeClient(String codeClient) {
            this.codeClient = codeClient;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public Integer getJoursRetard() {
            return joursRetard;
        }

        public void setJoursRetard(Integer joursRetard) {
            this.joursRetard = joursRetard;
        }

        public BigDecimal getMontantDu() {
            return montantDu;
        }

        public void setMontantDu(BigDecimal montantDu) {
            this.montantDu = montantDu;
        }

        public BigDecimal getInteretsRetard() {
            return interetsRetard;
        }

        public void setInteretsRetard(BigDecimal interetsRetard) {
            this.interetsRetard = interetsRetard;
        }
    }
}
