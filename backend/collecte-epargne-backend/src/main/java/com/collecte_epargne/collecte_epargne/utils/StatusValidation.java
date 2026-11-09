package com.collecte_epargne.collecte_epargne.utils;

/**
 * COMMENT CLEF: Cycle de vie d'une transaction = workflow validation par caissier
 * EN_ATTENTE -> VALIDEE (caissier approuve, crédite compte)
 * EN_ATTENTE -> REJETEE (caissier refuse, motif enregistré)
 */
public enum StatusValidation {
    EN_ATTENTE,    // Créée par collecteur, attends caissier
    VALIDEE,       // Caissier a approuvé, compte client crédité
    REJETEE        // Caissier a rejeté (motif enregistré, solde non crédité)
}
