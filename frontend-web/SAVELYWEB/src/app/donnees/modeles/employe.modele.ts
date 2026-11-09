export interface EmployeDto {
  matricule: string;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  loginUtilisateur: string;
  typeEmploye: string;
  commissionTaux?: number; // Optionnel pour éviter l'erreur "undefined"
  statut: string;          // Ajouté pour le template
  dateEmbauche: string;
  idAgence: number;
}

export enum TypeEmploye {
  CAISSIER = 'CAISSIER',
  COLLECTEUR = 'COLLECTEUR',
  SUPERVISEUR = 'SUPERVISEUR'
}