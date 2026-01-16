/// Enum pour les types de pièce d'identité
enum TypeCni {
  CNI('CNI'),
  PASSPORT('PASSPORT'),
  PERMIS_CONDUIRE('PERMIS_CONDUIRE'),
  AUTRE('AUTRE');

  final String value;
  const TypeCni(this.value);

  factory TypeCni.fromString(String value) {
    return TypeCni.values.firstWhere(
      (e) => e.value == value,
      orElse: () => TypeCni.AUTRE,
    );
  }
}

/// Enum pour les statuts de compte
enum StatutCompte {
  OUVERT('OUVERT'),
  BLOQUE('BLOQUE'),
  CLOTURE('CLOTURE'),
  GELE('GELE'),
  SUSPENDU('SUSPENDU');

  final String value;
  const StatutCompte(this.value);

  factory StatutCompte.fromString(String value) {
    return StatutCompte.values.firstWhere(
      (e) => e.value == value,
      orElse: () => StatutCompte.OUVERT,
    );
  }
}

/// Enum pour le statut d'approbation de compte
enum StatusApprobation {
  EN_ATTENTE('EN_ATTENTE'),
  APPROUVE('APPROUVE'),
  REJETE('REJETE');

  final String value;
  const StatusApprobation(this.value);

  factory StatusApprobation.fromString(String value) {
    return StatusApprobation.values.firstWhere(
      (e) => e.value == value,
      orElse: () => StatusApprobation.EN_ATTENTE,
    );
  }
}

/// Enum pour les types de transaction
enum TypeTransaction {
  DEPOT('DEPOT'),
  RETRAIT('RETRAIT'),
  COTISATION('COTISATION'),
  INTERET('INTERET'),
  PENALITE('PENALITE');

  final String value;
  const TypeTransaction(this.value);

  factory TypeTransaction.fromString(String value) {
    return TypeTransaction.values.firstWhere(
      (e) => e.value == value,
      orElse: () => TypeTransaction.DEPOT,
    );
  }
}

/// Enum pour le statut de transaction
enum StatutTransaction {
  EN_ATTENTE('EN_ATTENTE'),
  VALIDEE_CAISSE('VALIDEE_CAISSE'),
  VALIDEE_SUPERVISEUR('VALIDEE_SUPERVISEUR'),
  TERMINEE('TERMINEE'),
  ANNULEE('ANNULEE'),
  REJETEE('REJETEE');

  final String value;
  const StatutTransaction(this.value);

  factory StatutTransaction.fromString(String value) {
    return StatutTransaction.values.firstWhere(
      (e) => e.value == value,
      orElse: () => StatutTransaction.EN_ATTENTE,
    );
  }
}

/// Enum pour le statut de validation
enum StatusValidation {
  EN_ATTENTE('EN_ATTENTE'),
  VALIDEE('VALIDEE'),
  REJETEE('REJETEE');

  final String value;
  const StatusValidation(this.value);

  factory StatusValidation.fromString(String value) {
    return StatusValidation.values.firstWhere(
      (e) => e.value == value,
      orElse: () => StatusValidation.EN_ATTENTE,
    );
  }
}

/// Enum pour le mode de transaction
enum ModeTransaction {
  LIQUIDE('LIQUIDE'),
  CHEQUE('CHEQUE'),
  VIREMENT('VIREMENT'),
  MOBILE_MONEY('MOBILE_MONEY');

  final String value;
  const ModeTransaction(this.value);

  factory ModeTransaction.fromString(String value) {
    return ModeTransaction.values.firstWhere(
      (e) => e.value == value,
      orElse: () => ModeTransaction.LIQUIDE,
    );
  }
}

/// Enum pour le statut générique (client, utilisateur)
enum StatutGenerique {
  ACTIF('ACTIF'),
  INACTIF('INACTIF'),
  SUSPENDU('SUSPENDU');

  final String value;
  const StatutGenerique(this.value);

  factory StatutGenerique.fromString(String value) {
    return StatutGenerique.values.firstWhere(
      (e) => e.value == value,
      orElse: () => StatutGenerique.ACTIF,
    );
  }
}

/// Enum pour les types d'employé
enum TypeEmploye {
  COLLECTEUR('COLLECTEUR'),
  CAISSIER('CAISSIER'),
  SUPERVISEUR('SUPERVISEUR'),
  ADMIN('ADMIN');

  final String value;
  const TypeEmploye(this.value);

  factory TypeEmploye.fromString(String value) {
    return TypeEmploye.values.firstWhere(
      (e) => e.value == value,
      orElse: () => TypeEmploye.COLLECTEUR,
    );
  }
}
