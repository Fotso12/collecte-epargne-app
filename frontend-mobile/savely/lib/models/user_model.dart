class UserModel {
  final String login;
  final String nom;
  final String prenom;
  final String email;
  final String telephone;
  final int idRole;
  final String codeRole;
  final String nomRole;

  UserModel({
    required this.login,
    required this.nom,
    required this.prenom,
    required this.email,
    required this.telephone,
    required this.idRole,
    required this.codeRole,
    required this.nomRole,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) => UserModel(
        login: (json['login'] ?? '').toString(),
        nom: (json['nom'] ?? '').toString(),
        prenom: (json['prenom'] ?? '').toString(),
        email: (json['email'] ?? '').toString(),
        telephone: (json['telephone'] ?? '').toString(),
        idRole: (json['idRole'] as num?)?.toInt() ?? 0,
        codeRole: (json['codeRole'] ?? '').toString(),
        nomRole: (json['nomRole'] ?? '').toString(),
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
      };
}

