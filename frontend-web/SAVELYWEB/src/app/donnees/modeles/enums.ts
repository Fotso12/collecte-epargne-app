/** * Fichier regroupant toutes les énumérations du système 
 * Aligné avec le package com.collecte_epargne.collecte_epargne.utils
 */

export enum CategorieNotification {
    TRANSACTION = 'TRANSACTION',
    RAPPEL_COTISATION = 'RAPPEL_COTISATION',
    ALERTE = 'ALERTE',
    PROMO = 'PROMO'
}

export enum FormatRecu {
    PDF = 'PDF',
    SMS = 'SMS',
    EMAIL = 'EMAIL'
}

export enum FrequenceCotisation {
    QUOTIDIEN = 'QUOTIDIEN',
    HEBDOMADAIRE = 'HEBDOMADAIRE',
    MENSUEL = 'MENSUEL'
}

export enum ModeTransaction {
    ONLINE = 'ONLINE',
    OFFLINE = 'OFFLINE'
}

export enum StatutCompte {
    ACTIF = 'ACTIF',
    INACTIF = 'INACTIF',
    BLOQUE = 'BLOQUE',
    CLOTURE = 'CLOTURE'
}

export enum StatutGenerique {
    ACTIF = 'ACTIF',
    INACTIF = 'INACTIF',
    SUSPENDU = 'SUSPENDU'
}

export enum StatutPlanCotisation {
    ACTIF = 'ACTIF',
    TERMINE = 'TERMINE',
    SUSPENDU = 'SUSPENDU'
}

export enum StatutSynchroOffline {
    EN_ATTENTE = 'EN_ATTENTE',
    SYNCHRONISE = 'SYNCHRONISE',
    ERREUR = 'ERREUR',
    SYNCHRONISEE = 'SYNCHRONISEE',
    DOUBLON = 'DOUBLON'
}

export enum StatutTransaction {
    EN_ATTENTE = 'EN_ATTENTE',
    VALIDEE_CAISSE = 'VALIDEE_CAISSE',
    VALIDEE_SUPERVISEUR = 'VALIDEE_SUPERVISEUR',
    TERMINEE = 'TERMINEE',
    ANNULEE = 'ANNULEE',
    REJETEE = 'REJETEE'
}

export enum TypeCNI {
    PASSEPORT = 'PASSEPORT',
    CARTE_IDENTITE = 'CARTE_IDENTITE',
    PERMIS_CONDUIRE = 'PERMIS_CONDUIRE'
}

export enum TypeEmploye {
    SUPERVISEUR = 'SUPERVISEUR',
    CAISSIER = 'CAISSIER',
    COLLECTEUR = 'COLLECTEUR'
}

export enum TypeNotification {
    SMS = 'SMS',
    EMAIL = 'EMAIL',
    PUSH = 'PUSH',
    IN_APP = 'IN_APP'
}

export enum TypeTransaction {
    DEPOT = 'DEPOT',
    RETRAIT = 'RETRAIT',
    REVERSEMENT = 'REVERSEMENT',
    PENALITE = 'PENALITE',
    BONUS = 'BONUS',
    INTERETS = 'INTERETS'
}