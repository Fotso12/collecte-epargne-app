export interface RoleDto {
    id?: number;
    code: string;
    nom: string;
    description?: string;
}

export interface TypeCompteDto {
    id?: number;
    code: string;
    nom: string;
    description?: string;
    tauxInteret?: number;
    soldeMinimum?: number;
    fraisOuverture?: number;
    fraisCloture?: number;
    autoriserRetrait?: boolean;
    dureeBlocageJours?: number;
}

export interface PlanCotisationDto {
    idPlan?: string;
    nom: string;
    montantAttendu: number;
    frequence: 'QUOTIDIEN' | 'HEBDOMADAIRE' | 'MENSUEL';
    dureeJours?: number;
    dateDebut: string;
    dateFin?: string;
    tauxPenaliteRetard?: number;
    statut?: 'ACTIF' | 'TERMINE' | 'SUSPENDU';
}

export interface NotificationDto {
    idNotification?: string;
    codeClient?: string;
    type: 'SMS' | 'EMAIL' | 'PUSH' | 'IN_APP';
    categorie: 'TRANSACTION' | 'RAPPEL_COTISATION' | 'ALERTE' | 'PROMO';
    titre: string;
    message: string;
    statut?: string;
    dateCreation?: string;
    dateEnvoi?: string;
    erreurEnvoi?: string;
}
