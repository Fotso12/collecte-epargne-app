export enum TypeCNI {
    PASSEPORT = 'PASSEPORT',
    CARTE_IDENTITE = 'CARTE_IDENTITE',
    PERMIS_CONDUIRE = 'PERMIS_CONDUIRE'
}

export enum StatutGenerique {
    ACTIF = 'ACTIF',
    INACTIF = 'INACTIF',
    BLOQUE = 'BLOQUE',
    EN_ATTENTE = 'EN_ATTENTE'
}


export interface ClientDto {
    codeClient?: string;
    numeroClient: number;
    adresse?: string;
    typeCni: TypeCNI;
    numCni: string;
    photoPath?: string;
    cniRectoPath?: string;
    cniVersoPath?: string;
    dateNaissance?: string;
    lieuNaissance?: string;
    profession?: string;
    scoreEpargne?: number;
    loginUtilisateur: string;
    codeCollecteurAssigne?: string;
    nomCollecteur?: string;
    nom?: string;
    prenom?: string;
    telephone?: string;
    statut?: StatutGenerique; 
    dateCreation?: string | Date;
}