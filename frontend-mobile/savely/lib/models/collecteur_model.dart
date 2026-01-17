class CollecteurModel {
  // Identifiants
  final int idEmploye;
  final String matricule; // Code unique du collecteur (ex: "0000")

  // Informations personnelles (via relation Utilisateur)
  final String? nom;
  final String? prenom;
  final String? email;
  final String? telephone;

  // Rôle
  final String typeEmploye; // COLLECTEUR

  // Commission
  final double? commissionTaux;

  // Dates
  final DateTime dateEmbauche;

  // Hiérarchie
  final int? idSuperviseur;
  final String? nomSuperviseur;
  final int idAgence;
  final String? nomAgence;

  // KPIs (de CollecteurKPIDTO)
  final double montantCollecte;
  final int nombreClients;
  final int nombreTransactions;
  final double gainsMoyens;

  CollecteurModel({
    required this.idEmploye,
    required this.matricule,
    this.nom,
    this.prenom,
    this.email,
    this.telephone,
    required this.typeEmploye,
    this.commissionTaux,
    required this.dateEmbauche,
    this.idSuperviseur,
    this.nomSuperviseur,
    required this.idAgence,
    this.nomAgence,
    required this.montantCollecte,
    required this.nombreClients,
    required this.nombreTransactions,
    required this.gainsMoyens,
  });

  factory CollecteurModel.fromJson(Map<String, dynamic> json) {
    // helper to parse integers that may be encoded as String or num
    int _parseInt(dynamic v, {int fallback = 0}) {
      if (v == null) return fallback;
      if (v is int) return v;
      if (v is num) return v.toInt();
      if (v is String) return int.tryParse(v) ?? fallback;
      return fallback;
    }

    double _parseDouble(dynamic v, {double fallback = 0.0}) {
      if (v == null) return fallback;
      if (v is double) return v;
      if (v is num) return v.toDouble();
      if (v is String) return double.tryParse(v) ?? fallback;
      return fallback;
    }

    return CollecteurModel(
      idEmploye: _parseInt(json['idEmploye'] ?? json['idCollecteur'] ?? 0),
      matricule: (json['matricule'] ?? '').toString(),
      nom: json['nom']?.toString(),
      prenom: json['prenom']?.toString(),
      email: json['email']?.toString(),
      telephone: json['telephone']?.toString(),
      typeEmploye: (json['typeEmploye'] ?? 'COLLECTEUR').toString(),
      commissionTaux: json['commissionTaux'] != null
          ? _parseDouble(json['commissionTaux'], fallback: 0.0)
          : null,
      dateEmbauche: json['dateEmbauche'] != null
          ? DateTime.parse(json['dateEmbauche'] as String)
          : DateTime.now(),
      idSuperviseur: json['idSuperviseur'] != null
          ? _parseInt(json['idSuperviseur'])
          : null,
      nomSuperviseur: json['nomSuperviseur']?.toString(),
      idAgence: _parseInt(json['idAgence'] ?? 0),
      nomAgence: json['nomAgence']?.toString(),
      montantCollecte: json['montantCollecte'] != null
          ? _parseDouble(json['montantCollecte'], fallback: 0.0)
          : 0.0,
      nombreClients: _parseInt(json['nombreClients'] ?? 0),
      nombreTransactions: _parseInt(json['nombreTransactions'] ?? 0),
      gainsMoyens: json['gainsMoyens'] != null
          ? _parseDouble(json['gainsMoyens'], fallback: 0.0)
          : 0.0,
    );
  }

  Map<String, dynamic> toJson() => {
    'idEmploye': idEmploye,
    'idCollecteur': idEmploye, // For compatibility
    'matricule': matricule,
    'nom': nom,
    'prenom': prenom,
    'email': email,
    'telephone': telephone,
    'typeEmploye': typeEmploye,
    'commissionTaux': commissionTaux,
    'dateEmbauche': dateEmbauche.toIso8601String(),
    'idSuperviseur': idSuperviseur,
    'nomSuperviseur': nomSuperviseur,
    'idAgence': idAgence,
    'nomAgence': nomAgence,
    'montantCollecte': montantCollecte,
    'nombreClients': nombreClients,
    'nombreTransactions': nombreTransactions,
    'gainsMoyens': gainsMoyens,
  };

  // Getters utiles
  String get fullName => '${prenom ?? ''} ${nom ?? ''}'.trim();

  String get formattedMontantCollecte =>
      '${montantCollecte.toStringAsFixed(0)} FCFA';

  String get formattedGainsMoyens => '${gainsMoyens.toStringAsFixed(0)} FCFA';

  String get formattedDateEmbauche {
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
    return '${dateEmbauche.day} ${months[dateEmbauche.month - 1]} ${dateEmbauche.year}';
  }

  // KPIs
  double get performanceScore {
    // Score basé sur nombre de clients et transactions
    if (nombreClients == 0) return 0.0;
    return (nombreTransactions / nombreClients) * 10;
  }

  String get performanceLevel {
    final score = performanceScore;
    if (score >= 15) return 'Excellent';
    if (score >= 10) return 'Très bon';
    if (score >= 5) return 'Bon';
    if (score >= 2) return 'Satisfaisant';
    return 'À améliorer';
  }
}
