class TypeCompteModel {
  final int id;
  final String code;
  final String nom;
  final String? description;
  final double? tauxInteret;
  final double? soldeMinimum;
  final double? fraisOuverture;
  final double? fraisCloture;
  final bool? autoriserRetrait;
  final int? dureeBlocageJours;

  TypeCompteModel({
    required this.id,
    required this.code,
    required this.nom,
    this.description,
    this.tauxInteret,
    this.soldeMinimum,
    this.fraisOuverture,
    this.fraisCloture,
    this.autoriserRetrait,
    this.dureeBlocageJours,
  });

  factory TypeCompteModel.fromJson(Map<String, dynamic> json) {
    return TypeCompteModel(
      id: (json['id'] as int?) ?? 0,
      code: (json['code'] ?? '').toString(),
      nom: (json['nom'] ?? '').toString(),
      description: json['description']?.toString(),
      tauxInteret: json['tauxInteret'] != null 
          ? (json['tauxInteret'] as num).toDouble() 
          : null,
      soldeMinimum: json['soldeMinimum'] != null 
          ? (json['soldeMinimum'] as num).toDouble() 
          : null,
      fraisOuverture: json['fraisOuverture'] != null 
          ? (json['fraisOuverture'] as num).toDouble() 
          : null,
      fraisCloture: json['fraisCloture'] != null 
          ? (json['fraisCloture'] as num).toDouble() 
          : null,
      autoriserRetrait: json['autoriserRetrait'] as bool?,
      dureeBlocageJours: json['dureeBlocageJours'] != null 
          ? (json['dureeBlocageJours'] as num).toInt() 
          : null,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'code': code,
        'nom': nom,
        if (description != null) 'description': description,
        if (tauxInteret != null) 'tauxInteret': tauxInteret,
        if (soldeMinimum != null) 'soldeMinimum': soldeMinimum,
        if (fraisOuverture != null) 'fraisOuverture': fraisOuverture,
        if (fraisCloture != null) 'fraisCloture': fraisCloture,
        if (autoriserRetrait != null) 'autoriserRetrait': autoriserRetrait,
        if (dureeBlocageJours != null) 'dureeBlocageJours': dureeBlocageJours,
      };
}

