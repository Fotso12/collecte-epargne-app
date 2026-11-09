package com.collecte_epargne.collecte_epargne.utils;

/**
 * COMMENT CLEF: Cycle de vie d'un compte client = approbation superviseur
 * EN_ATTENTE -> APPROUVE (superviseur OK, compte actif)
 * EN_ATTENTE -> REJETE (superviseur refuse, motif enregistré)
 */
public enum StatusApprobation {
    EN_ATTENTE,    // Créé par client/caissier, attends superviseur
    APPROUVE,      // Superviseur OK = compte actif, peut faire transactions
    REJETE         // Superviseur refuse (motif enregistré, compte inactif)
}
