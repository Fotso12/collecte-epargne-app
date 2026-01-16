class UserModel {
  final String login;
  final String nom;
  final String prenom;
  final String email;
  final String telephone;
  final int idRole;
  final String codeRole;
  final String nomRole;
  // Champ 'role' tel que renvoyé par le backend (ex: "COLLECTEUR")
  final String roleRaw;

  UserModel({
    required this.login,
    required this.nom,
    required this.prenom,
    required this.email,
    required this.telephone,
    required this.idRole,
    required this.codeRole,
    required this.nomRole,
    required this.roleRaw,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) => UserModel(
    login: (json['login'] ?? '').toString(),
    nom: (json['nom'] ?? '').toString(),
    prenom: (json['prenom'] ?? '').toString(),
    email: (json['email'] ?? '').toString(),
    telephone: (json['telephone'] ?? '').toString(),
    idRole: (json['idRole'] as num?)?.toInt() ?? 0,
    // codeRole contient souvent l'abréviation (ex: COLL), role contient la valeur lisible (ex: COLLECTEUR)
    codeRole: (json['codeRole'] ?? '').toString(),
    nomRole: (json['nomRole'] ?? '').toString(),
    roleRaw: (json['role'] ?? json['codeRole'] ?? '').toString(),
  );

  Map<String, dynamic> toJson() => {
    'login': login,
    'nom': nom,
    'prenom': prenom,
    'email': email,
    'telephone': telephone,
    'idRole': idRole,
    'codeRole': codeRole,
    'nomRole': nomRole,
    'role': roleRaw,
  };

  // Getters utiles
  String get fullName => '$prenom $nom'.trim();
  // Retourne d'abord la valeur lisible `role` (ex: COLLECTEUR), sinon `codeRole` (ex: COLL)
  String get role => roleRaw.isNotEmpty ? roleRaw : codeRole;
}
