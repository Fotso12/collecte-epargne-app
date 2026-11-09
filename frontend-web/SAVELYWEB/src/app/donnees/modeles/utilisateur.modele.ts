import { StatutGenerique } from "./enums";

export interface UtilisateurCreationRequestDto {
  login: string;
  idRole: number;
  nom: string;
  prenom: string;
  telephone: string;
  email: string;
  password?: string;
  statut: StatutGenerique;
}

export interface UtilisateurDto {
  login: string;
  idRole: number;
  idEmploye?: number;
  codeClient?: string;
  nom: string;
  prenom: string;
  telephone: string;
  email: string;
  statut: StatutGenerique;
}