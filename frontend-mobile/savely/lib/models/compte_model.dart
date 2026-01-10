class CompteModel {
  final String idCompte;
  final String numCompte;
  final double? solde;
  final double? soldeDisponible;
  final DateTime dateOuverture;
  final DateTime? dateDerniereTransaction;
  final double? tauxPenalite;
  final double? tauxBonus;
  final String? statut;
  final String? motifBlocage;
  final DateTime? dateCloture;
  final String codeClient;
  final int idTypeCompte;
  final String? typeCompteNom; // Nom du type de compte (épargne, courant, etc.)

  CompteModel({
    required this.idCompte,
    required this.numCompte,
    this.solde,
    this.soldeDisponible,
    required this.dateOuverture,
    this.dateDerniereTransaction,
    this.tauxPenalite,
    this.tauxBonus,
    this.statut,
    this.motifBlocage,
    this.dateCloture,
    required this.codeClient,
    required this.idTypeCompte,
    this.typeCompteNom,
  });

  factory CompteModel.fromJson(Map<String, dynamic> json) {
    return CompteModel(
      idCompte: (json['idCompte'] ?? '').toString(),
      numCompte: (json['numCompte'] ?? '').toString(),
      solde: json['solde'] != null ? (json['solde'] as num).toDouble() : null,
      soldeDisponible: json['soldeDisponible'] != null 
          ? (json['soldeDisponible'] as num).toDouble() 
          : null,
      dateOuverture: json['dateOuverture'] != null
          ? DateTime.parse(json['dateOuverture'] as String)
          : DateTime.now(),
      dateDerniereTransaction: json['dateDerniereTransaction'] != null
          ? DateTime.parse(json['dateDerniereTransaction'] as String)
          : null,
      tauxPenalite: json['tauxPenalite'] != null 
          ? (json['tauxPenalite'] as num).toDouble() 
          : null,
      tauxBonus: json['tauxBonus'] != null 
          ? (json['tauxBonus'] as num).toDouble() 
          : null,
      statut: json['statut']?.toString(),
      motifBlocage: json['motifBlocage']?.toString(),
      dateCloture: json['dateCloture'] != null
          ? DateTime.parse(json['dateCloture'] as String)
          : null,
      codeClient: (json['codeClient'] ?? '').toString(),
      idTypeCompte: (json['idTypeCompte'] as int?) ?? 0,
      typeCompteNom: json['typeCompteNom']?.toString(),
    );
  }

  Map<String, dynamic> toJson() => {
        'idCompte': idCompte,
        'numCompte': numCompte,
        'solde': solde,
        'soldeDisponible': soldeDisponible,
        'dateOuverture': dateOuverture.toIso8601String(),
        'dateDerniereTransaction': dateDerniereTransaction?.toIso8601String(),
        'tauxPenalite': tauxPenalite,
        'tauxBonus': tauxBonus,
        'statut': statut,
        'motifBlocage': motifBlocage,
        'dateCloture': dateCloture?.toIso8601String(),
        'codeClient': codeClient,
        'idTypeCompte': idTypeCompte,
        'typeCompteNom': typeCompteNom,
      };

  // Méthodes utilitaires
  String get formattedSolde => solde != null 
      ? '${solde!.toStringAsFixed(0)} FCFA' 
      : '0 FCFA';

  String get formattedSoldeDisponible => soldeDisponible != null 
      ? '${soldeDisponible!.toStringAsFixed(0)} FCFA' 
      : '0 FCFA';

  String get formattedDateOuverture {
    final months = [
      'Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun',
      'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'
    ];
    return '${dateOuverture.day} ${months[dateOuverture.month - 1]} ${dateOuverture.year}';
  }

  String get formattedDerniereTransaction {
    if (dateDerniereTransaction == null) return 'Aucune';
    final months = [
      'Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun',
      'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'
    ];
    return '${dateDerniereTransaction!.day} ${months[dateDerniereTransaction!.month - 1]} ${dateDerniereTransaction!.year}';
  }

  bool get isActif => statut == null || statut!.toUpperCase() == 'ACTIF';
  bool get isBloque => statut != null && statut!.toUpperCase().contains('BLOQUE');
  bool get isCloture => statut != null && statut!.toUpperCase().contains('CLOTURE') || dateCloture != null;
}

