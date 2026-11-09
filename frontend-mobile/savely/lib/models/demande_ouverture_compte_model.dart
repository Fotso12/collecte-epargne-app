class DemandeOuvertureCompteModel {
  final int? idDemande;
  final String codeClient;
  final int idTypeCompte;
  final String? nomTypeCompte;
  final int? idSuperviseurValidateur;
  final String statut; // EN_ATTENTE, VALIDEE, REJETEE
  final double? montantInitial;
  final String? motif;
  final String? motifRejet;
  final DateTime? dateDemande;
  final DateTime? dateValidation;
  final String? nomClient;
  final String? prenomClient;
  final String? emailClient;

  DemandeOuvertureCompteModel({
    this.idDemande,
    required this.codeClient,
    required this.idTypeCompte,
    this.nomTypeCompte,
    this.idSuperviseurValidateur,
    required this.statut,
    this.montantInitial,
    this.motif,
    this.motifRejet,
    this.dateDemande,
    this.dateValidation,
    this.nomClient,
    this.prenomClient,
    this.emailClient,
  });

  factory DemandeOuvertureCompteModel.fromJson(Map<String, dynamic> json) {
    return DemandeOuvertureCompteModel(
      idDemande: json['idDemande'] != null ? (json['idDemande'] as num).toInt() : null,
      codeClient: (json['codeClient'] ?? '').toString(),
      idTypeCompte: (json['idTypeCompte'] as int?) ?? 0,
      nomTypeCompte: json['nomTypeCompte']?.toString(),
      idSuperviseurValidateur: json['idSuperviseurValidateur'] != null 
          ? (json['idSuperviseurValidateur'] as num).toInt() 
          : null,
      statut: (json['statut'] ?? 'EN_ATTENTE').toString(),
      montantInitial: json['montantInitial'] != null 
          ? (json['montantInitial'] as num).toDouble() 
          : null,
      motif: json['motif']?.toString(),
      motifRejet: json['motifRejet']?.toString(),
      dateDemande: json['dateDemande'] != null
          ? DateTime.parse(json['dateDemande'] as String)
          : null,
      dateValidation: json['dateValidation'] != null
          ? DateTime.parse(json['dateValidation'] as String)
          : null,
      nomClient: json['nomClient']?.toString(),
      prenomClient: json['prenomClient']?.toString(),
      emailClient: json['emailClient']?.toString(),
    );
  }

  Map<String, dynamic> toJson() => {
        if (idDemande != null) 'idDemande': idDemande,
        'codeClient': codeClient,
        'idTypeCompte': idTypeCompte,
        if (nomTypeCompte != null) 'nomTypeCompte': nomTypeCompte,
        if (idSuperviseurValidateur != null) 'idSuperviseurValidateur': idSuperviseurValidateur,
        'statut': statut,
        if (montantInitial != null) 'montantInitial': montantInitial,
        if (motif != null) 'motif': motif,
        if (motifRejet != null) 'motifRejet': motifRejet,
        if (dateDemande != null) 'dateDemande': dateDemande!.toIso8601String(),
        if (dateValidation != null) 'dateValidation': dateValidation!.toIso8601String(),
        if (nomClient != null) 'nomClient': nomClient,
        if (prenomClient != null) 'prenomClient': prenomClient,
        if (emailClient != null) 'emailClient': emailClient,
      };

  bool get isEnAttente => statut.toUpperCase() == 'EN_ATTENTE';
  bool get isValidee => statut.toUpperCase() == 'VALIDEE';
  bool get isRejetee => statut.toUpperCase() == 'REJETEE';
}

