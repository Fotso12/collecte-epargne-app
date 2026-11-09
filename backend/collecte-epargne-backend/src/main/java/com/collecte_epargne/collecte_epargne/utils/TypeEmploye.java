package com.collecte_epargne.collecte_epargne.utils;

/**
 * COMMENT CLEF: Détermine le rôle de l'employé = restrictions d'accès dans le système
 * Chaque type a des permissions spécifiques et isolement de données différent
 */
public enum TypeEmploye {
    COLLECTEUR,      // Collecte argent auprès des clients, crée transactions
    CAISSIER,        // Valide/rejette transactions des collecteurs de son agence
    SUPERVISEUR,     // Approuve comptes clients, voir KPIs globaux de son institution
    AUDITOR          // Accès lecture-seule aux rapports et transactions
}
