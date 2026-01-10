import { StatutGenerique } from "./enums";

export interface AgenceZoneDto {
  idAgence?: number;
  code: string;
  nom: string;
  ville?: string;
  quartier?: string;
  adresse?: string;
  telephone?: string;
  description?: string;
  statut?: StatutGenerique;
  dateCreation?: Date | string;
}

// Interface pour les données liées que vous souhaitez afficher
export interface EntitesAgence {
  employes: any[];
  clients: any[];
  utilisateurs: any[];
}