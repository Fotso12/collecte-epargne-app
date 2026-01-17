
class ClientModel {
  int? numeroClient;
  String? codeClient;
  String? nom;
  String? prenom;
  String? email;
  String? telephone;
  String? adresse;
  String? ville;
  String? typeCni;
  String? numCni;
  DateTime? dateNaissance;
  String? lieuNaissance;
  String? profession;
  String? photoPath;
  String? cniRectoPath;
  String? cniVersoPath;
  String? statut;
  String? codeCollecteurAssigne;
  String? nomCollecteur;
  int? idAgence;
  int? scoreEpargne;
  DateTime? dateCreation;

  ClientModel({
    this.numeroClient,
    this.codeClient,
    this.nom,
    this.prenom,
    this.email,
    this.telephone,
    this.adresse,
    this.ville,
    this.typeCni,
    this.numCni,
    this.dateNaissance,
    this.lieuNaissance,
    this.profession,
    this.photoPath,
    this.cniRectoPath,
    this.cniVersoPath,
    this.statut,
    this.codeCollecteurAssigne,
    this.nomCollecteur,
    this.idAgence,
    this.scoreEpargne,
    this.dateCreation,
  });

  factory ClientModel.fromJson(Map<String, dynamic> json) => ClientModel(
    numeroClient: json['numeroClient'] as int?,
    codeClient: json['codeClient']?.toString(),
    nom: json['nom']?.toString(),
    prenom: json['prenom']?.toString(),
    email: json['email']?.toString(),
    telephone: json['telephone']?.toString(),
    adresse: json['adresse']?.toString(),
    ville: json['ville']?.toString(),
    typeCni: json['typeCni']?.toString(),
    numCni: json['numCni']?.toString(),
    dateNaissance: json['dateNaissance'] != null
        ? DateTime.parse(json['dateNaissance'].toString())
        : null,
    lieuNaissance: json['lieuNaissance']?.toString(),
    profession: json['profession']?.toString(),
    photoPath: json['photoPath']?.toString(),
    cniRectoPath: json['cniRectoPath']?.toString(),
    cniVersoPath: json['cniVersoPath']?.toString(),
    statut: json['statut']?.toString(),
    codeCollecteurAssigne: json['codeCollecteurAssigne']?.toString(),
    nomCollecteur: json['nomCollecteur']?.toString(),
    idAgence: json['idAgence'] as int?,
    scoreEpargne: json['scoreEpargne'] as int?,
    dateCreation: json['dateCreation'] != null
        ? DateTime.parse(json['dateCreation'].toString())
        : null,
  );

  Map<String, dynamic> toJson() => {
    'numeroClient': numeroClient,
    'codeClient': codeClient,
    'nom': nom,
    'prenom': prenom,
    'email': email,
    'telephone': telephone,
    'adresse': adresse,
    'ville': ville,
    'typeCni': typeCni,
    'numCni': numCni,
    'dateNaissance': dateNaissance?.toIso8601String(),
    'lieuNaissance': lieuNaissance,
    'profession': profession,
    'photoPath': photoPath,
    'cniRectoPath': cniRectoPath,
    'cniVersoPath': cniVersoPath,
    'statut': statut,
    'codeCollecteurAssigne': codeCollecteurAssigne,
    'nomCollecteur': nomCollecteur,
    'idAgence': idAgence,
    'scoreEpargne': scoreEpargne,
    'dateCreation': dateCreation?.toIso8601String(),
  };

  // Getters utiles
  String get fullName => '${prenom ?? ''} ${nom ?? ''}'.trim();

  bool get isApproved => statut == 'ACTIF';

  bool get hasCollector =>
      codeCollecteurAssigne != null && codeCollecteurAssigne != '0000';
}
