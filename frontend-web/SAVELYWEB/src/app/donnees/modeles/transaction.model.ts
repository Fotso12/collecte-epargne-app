import { StatutTransaction, TypeTransaction, ModeTransaction } from './enums';

export interface TransactionDto {
  idTransaction: string;
  idCompte: string;
  idEmployeInitiateur: string;
  idCaissierValidateur?: string;
  idSuperviseurValidateur?: string;
  reference: string;
  typeTransaction: TypeTransaction; // Utilise l'Enum
  montant: number;
  soldeAvant: number;
  soldeApres: number;
  description: string;
  dateTransaction: string;
  dateValidationCaisse?: string;
  dateValidationSuperviseur?: string;
  motifRejet?: string;
  statut: StatutTransaction; 
  modeTransaction: ModeTransaction; 
  signatureClient?: string;
  hashTransaction?: string;
  nomInitiateur?: string; 
  nomCaissier?: string;   
}