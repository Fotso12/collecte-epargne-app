import 'package:flutter/material.dart';

class TransactionModel {
  final String idTransaction;
  final String reference;

  // Montants et balances
  final double montant;
  final double soldeAvant;
  final double soldeApres;

  // Workflow validation
  final String? statusValidation; // EN_ATTENTE, VALIDEE, REJETEE
  final String?
  statut; // EN_ATTENTE, VALIDEE_CAISSE, VALIDEE_SUPERVISEUR, TERMINEE, ANNULEE, REJETEE
  final String? modeTransaction; // LIQUIDE, CHEQUE, VIREMENT, MOBILE_MONEY
  final String?
  typeTransaction; // DEPOT, RETRAIT, COTISATION, INTERET, PENALITE

  // Dates
  final DateTime dateTransaction;
  final DateTime? dateValidationCaisse;
  final DateTime? dateValidationSuperviseur;

  // Acteurs
  final String? idEmployeInitiateur; // Collecteur qui a créé la transaction
  final String? idCaissierValidateur;
  final String? idSuperviseurValidateur;
  final String idCompte;

  // Rejet
  final String? motifRejet;

  // Sécurité
  final String? signatureClient;
  final String? hashTransaction;

  // Description
  final String? description;

  // Noms des acteurs (pour affichage)
  final String? nomInitiateur;
  final String? nomCaissier;
  final String? nomSuperviseur;

  TransactionModel({
    required this.idTransaction,
    required this.reference,
    required this.montant,
    required this.soldeAvant,
    required this.soldeApres,
    this.statusValidation,
    this.statut,
    this.modeTransaction,
    this.typeTransaction,
    required this.dateTransaction,
    this.dateValidationCaisse,
    this.dateValidationSuperviseur,
    this.idEmployeInitiateur,
    this.idCaissierValidateur,
    this.idSuperviseurValidateur,
    required this.idCompte,
    this.motifRejet,
    this.signatureClient,
    this.hashTransaction,
    this.description,
    this.nomInitiateur,
    this.nomCaissier,
    this.nomSuperviseur,
  });

  factory TransactionModel.fromJson(Map<String, dynamic> json) {
    return TransactionModel(
      idTransaction: (json['idTransaction'] ?? '').toString(),
      reference: (json['reference'] ?? '').toString(),
      montant: json['montant'] != null
          ? (json['montant'] as num).toDouble()
          : 0.0,
      soldeAvant: json['soldeAvant'] != null
          ? (json['soldeAvant'] as num).toDouble()
          : 0.0,
      soldeApres: json['soldeApres'] != null
          ? (json['soldeApres'] as num).toDouble()
          : 0.0,
      statusValidation: json['statusValidation']?.toString(),
      statut: json['statut']?.toString(),
      modeTransaction: json['modeTransaction']?.toString(),
      typeTransaction: json['typeTransaction']?.toString(),
      dateTransaction: json['dateTransaction'] != null
          ? DateTime.parse(json['dateTransaction'] as String)
          : DateTime.now(),
      dateValidationCaisse: json['dateValidationCaisse'] != null
          ? DateTime.parse(json['dateValidationCaisse'] as String)
          : null,
      dateValidationSuperviseur: json['dateValidationSuperviseur'] != null
          ? DateTime.parse(json['dateValidationSuperviseur'] as String)
          : null,
      idEmployeInitiateur: json['idEmployeInitiateur']?.toString(),
      idCaissierValidateur: json['idCaissierValidateur']?.toString(),
      idSuperviseurValidateur: json['idSuperviseurValidateur']?.toString(),
      idCompte: (json['idCompte'] ?? '').toString(),
      motifRejet: json['motifRejet']?.toString(),
      signatureClient: json['signatureClient']?.toString(),
      hashTransaction: json['hashTransaction']?.toString(),
      description: json['description']?.toString(),
      nomInitiateur: json['nomInitiateur']?.toString(),
      nomCaissier: json['nomCaissier']?.toString(),
      nomSuperviseur: json['nomSuperviseur']?.toString(),
    );
  }

  Map<String, dynamic> toJson() => {
    'idTransaction': idTransaction,
    'reference': reference,
    'montant': montant,
    'soldeAvant': soldeAvant,
    'soldeApres': soldeApres,
    'statusValidation': statusValidation,
    'statut': statut,
    'modeTransaction': modeTransaction,
    'typeTransaction': typeTransaction,
    'dateTransaction': dateTransaction.toIso8601String(),
    'dateValidationCaisse': dateValidationCaisse?.toIso8601String(),
    'dateValidationSuperviseur': dateValidationSuperviseur?.toIso8601String(),
    'idEmployeInitiateur': idEmployeInitiateur,
    'idCaissierValidateur': idCaissierValidateur,
    'idSuperviseurValidateur': idSuperviseurValidateur,
    'idCompte': idCompte,
    'motifRejet': motifRejet,
    'signatureClient': signatureClient,
    'hashTransaction': hashTransaction,
    'description': description,
    'nomInitiateur': nomInitiateur,
    'nomCaissier': nomCaissier,
    'nomSuperviseur': nomSuperviseur,
  };

  // Getters utiles
  String get formattedMontant => '${montant.toStringAsFixed(0)} FCFA';

  bool get isPending =>
      statusValidation == 'EN_ATTENTE' || statut == 'EN_ATTENTE';
  bool get isValidated =>
      statusValidation == 'VALIDEE' ||
      statut == 'VALIDEE_CAISSE' ||
      statut == 'VALIDEE_SUPERVISEUR';
  bool get isRejected => statusValidation == 'REJETEE' || statut == 'REJETEE';
  bool get isCompleted => statut == 'TERMINEE';
  bool get isCancelled => statut == 'ANNULEE';

  String get displayStatus {
    if (isPending) return 'En attente';
    if (isValidated) return 'Validée';
    if (isRejected) return 'Rejetée';
    if (isCompleted) return 'Terminée';
    if (isCancelled) return 'Annulée';
    return statut ?? 'Unknown';
  }

  String get displayValidationStatus {
    switch (statusValidation) {
      case 'EN_ATTENTE':
        return 'En attente de validation';
      case 'VALIDEE':
        return 'Validée';
      case 'REJETEE':
        return 'Rejetée';
      default:
        return statusValidation ?? 'Unknown';
    }
  }

  String get displayType {
    switch (typeTransaction) {
      case 'DEPOT':
        return 'Dépôt';
      case 'RETRAIT':
        return 'Retrait';
      case 'COTISATION':
        return 'Cotisation';
      case 'INTERET':
        return 'Intérêt';
      case 'PENALITE':
        return 'Pénalité';
      default:
        return typeTransaction ?? 'Unknown';
    }
  }

  String get formattedDate {
    final months = [
      'Jan',
      'Fév',
      'Mar',
      'Avr',
      'Mai',
      'Jun',
      'Jul',
      'Aoû',
      'Sep',
      'Oct',
      'Nov',
      'Déc',
    ];
    return '${dateTransaction.day} ${months[dateTransaction.month - 1]} ${dateTransaction.year}';
  }

  Color getStatusColor() {
    if (isPending) return const Color(0xFFFFA500); // Orange
    if (isValidated) return const Color(0xFF28A745); // Green
    if (isRejected) return const Color(0xFFDC3545); // Red
    if (isCompleted) return const Color(0xFF17A2B8); // Cyan
    if (isCancelled) return const Color(0xFF6C757D); // Gray
    return const Color(0xFF000000); // Black
  }
}
