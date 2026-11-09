# 📱 Plan d'Implémentation Mobile - Savely App
## Adaptation du PLAN_IMPLEMENTATION_OPTIMISE.md pour Flutter

---

## 🎨 **PHASE 1: SETUP & ASSETS**

### 1.1 Créer les dossiers assets
```bash
mkdir -p frontend-mobile/savely/assets/images
mkdir -p frontend-mobile/savely/assets/icons
```

### 1.2 Mettre à jour pubspec.yaml
```yaml
flutter:
  uses-material-design: true
  assets:
    - assets/images/
    - assets/icons/
```

### 1.3 Créer le logo Savely (SVG → PNG)
**File:** `assets/images/logo-savely.png`

Structure du logo:
```
┌─────────────────────────────────────┐
│   [💚] SAVELY                       │
│     Épargne & Collecte              │
└─────────────────────────────────────┘

Couleurs:
- Vert principal: RGB(13, 138, 95) = #0D8A5F
- Blanc: RGB(255, 255, 255)
- Texte: RGB(33, 33, 33) = #212121

Dimensions: 512x512 px, exporté en PNG
```

**IMPORTANT:** Tu dois créer ce PNG avec:
- Un cœur stylisé en vert (emoji-like)
- Le texte "SAVELY" en gras
- Sous-titre "Épargne & Collecte" en petit
- Fond blanc transparent

---

## 📝 **PHASE 2: MODÈLES (MODELS)**

### 2.1 Créer ClientModel.dart
```dart
// File: lib/models/client_model.dart

class ClientModel {
  final String idClient;
  final String nom;
  final String prenom;
  final String email;
  final String telephone;
  final String adresse;
  final String codeClient;
  final DateTime dateCreation;
  final bool actif;

  ClientModel({
    required this.idClient,
    required this.nom,
    required this.prenom,
    required this.email,
    required this.telephone,
    required this.adresse,
    required this.codeClient,
    required this.dateCreation,
    this.actif = true,
  });

  factory ClientModel.fromJson(Map<String, dynamic> json) {
    return ClientModel(
      idClient: json['idClient'] ?? '',
      nom: json['nom'] ?? '',
      prenom: json['prenom'] ?? '',
      email: json['email'] ?? '',
      telephone: json['telephone'] ?? '',
      adresse: json['adresse'] ?? '',
      codeClient: json['codeClient'] ?? '',
      dateCreation: json['dateCreation'] != null 
        ? DateTime.parse(json['dateCreation']) 
        : DateTime.now(),
      actif: json['actif'] ?? true,
    );
  }

  Map<String, dynamic> toJson() => {
    'idClient': idClient,
    'nom': nom,
    'prenom': prenom,
    'email': email,
    'telephone': telephone,
    'adresse': adresse,
    'codeClient': codeClient,
  };
}
```

### 2.2 Créer CompteModel.dart
```dart
// File: lib/models/compte_model.dart

class CompteModel {
  final String idCompte;
  final String typeCompte;
  final double montantActuel;
  final double montantMin;
  final double montantMax;
  final String statusApprobation; // EN_ATTENTE, APPROUVE, REJETE
  final DateTime dateCreation;
  final DateTime? dateApprobation;
  final String? motifRejetApprobation;

  CompteModel({
    required this.idCompte,
    required this.typeCompte,
    required this.montantActuel,
    required this.montantMin,
    required this.montantMax,
    required this.statusApprobation,
    required this.dateCreation,
    this.dateApprobation,
    this.motifRejetApprobation,
  });

  factory CompteModel.fromJson(Map<String, dynamic> json) {
    return CompteModel(
      idCompte: json['idCompte'] ?? '',
      typeCompte: json['typeCompte'] ?? '',
      montantActuel: (json['montantActuel'] ?? 0).toDouble(),
      montantMin: (json['montantMin'] ?? 0).toDouble(),
      montantMax: (json['montantMax'] ?? 0).toDouble(),
      statusApprobation: json['statusApprobation'] ?? 'EN_ATTENTE',
      dateCreation: DateTime.parse(json['dateCreation'] ?? DateTime.now().toString()),
      dateApprobation: json['dateApprobation'] != null 
        ? DateTime.parse(json['dateApprobation']) 
        : null,
      motifRejetApprobation: json['motifRejetApprobation'],
    );
  }

  bool get isApproved => statusApprobation == 'APPROUVE';
  bool get isPending => statusApprobation == 'EN_ATTENTE';
  bool get isRejected => statusApprobation == 'REJETE';
}
```

### 2.3 Créer CollecteurModel.dart
```dart
// File: lib/models/collecteur_model.dart

class CollecteurModel {
  final String idEmploye;
  final String nom;
  final String prenom;
  final String email;
  final String telephone;
  final String matricule; // Assigné par défaut, modifiable par superviseur
  final String idAgence;
  final String nomAgence;
  final DateTime dateCreation;
  final bool actif;
  final double totalCollecte; // Montant total collecté
  final int nbClients;
  final int nbTransactions;

  CollecteurModel({
    required this.idEmploye,
    required this.nom,
    required this.prenom,
    required this.email,
    required this.telephone,
    required this.matricule,
    required this.idAgence,
    required this.nomAgence,
    required this.dateCreation,
    this.actif = true,
    this.totalCollecte = 0,
    this.nbClients = 0,
    this.nbTransactions = 0,
  });

  factory CollecteurModel.fromJson(Map<String, dynamic> json) {
    return CollecteurModel(
      idEmploye: json['idEmploye'] ?? '',
      nom: json['nom'] ?? '',
      prenom: json['prenom'] ?? '',
      email: json['email'] ?? '',
      telephone: json['telephone'] ?? '',
      matricule: json['matricule'] ?? '0000',
      idAgence: json['idAgence'] ?? '',
      nomAgence: json['nomAgence'] ?? '',
      dateCreation: DateTime.parse(json['dateCreation'] ?? DateTime.now().toString()),
      actif: json['actif'] ?? true,
      totalCollecte: (json['totalCollecte'] ?? 0).toDouble(),
      nbClients: json['nbClients'] ?? 0,
      nbTransactions: json['nbTransactions'] ?? 0,
    );
  }

  String get fullName => '$prenom $nom';
  String get displayMatricule => matricule.padLeft(4, '0');
}
```

### 2.4 Créer TransactionModel.dart
```dart
// File: lib/models/transaction_model.dart

class TransactionModel {
  final String idTransaction;
  final String reference;
  final double montant;
  final String typeTransaction;
  final String statusValidation; // EN_ATTENTE, VALIDEE, REJETEE
  final DateTime dateCreation;
  final DateTime? dateValidation;
  final String? motifRejet;
  final String idCompte;
  final String nomClient;
  final String nomInitiateur;
  final String? nomCaissier;

  TransactionModel({
    required this.idTransaction,
    required this.reference,
    required this.montant,
    required this.typeTransaction,
    required this.statusValidation,
    required this.dateCreation,
    required this.idCompte,
    required this.nomClient,
    required this.nomInitiateur,
    this.dateValidation,
    this.motifRejet,
    this.nomCaissier,
  });

  factory TransactionModel.fromJson(Map<String, dynamic> json) {
    return TransactionModel(
      idTransaction: json['idTransaction'] ?? '',
      reference: json['reference'] ?? '',
      montant: (json['montant'] ?? 0).toDouble(),
      typeTransaction: json['typeTransaction'] ?? 'DEPOT',
      statusValidation: json['statusValidation'] ?? 'EN_ATTENTE',
      dateCreation: DateTime.parse(json['dateCreation'] ?? DateTime.now().toString()),
      idCompte: json['idCompte'] ?? '',
      nomClient: json['nomClient'] ?? '',
      nomInitiateur: json['nomInitiateur'] ?? '',
      dateValidation: json['dateValidation'] != null 
        ? DateTime.parse(json['dateValidation']) 
        : null,
      motifRejet: json['motifRejet'],
      nomCaissier: json['nomCaissier'],
    );
  }

  bool get isPending => statusValidation == 'EN_ATTENTE';
  bool get isValidated => statusValidation == 'VALIDEE';
  bool get isRejected => statusValidation == 'REJETEE';

  String get formattedAmount => montant.toStringAsFixed(2).replaceAll('.', ',');
  String get formattedDate => 
    '${dateCreation.day}/${dateCreation.month}/${dateCreation.year}';
}
```

---

## 🔌 **PHASE 3: API SERVICES**

### 3.1 Mettre à jour ClientApi.dart (Inscription Client)
```dart
// File: lib/services/client_api.dart
// AJOUTER ces méthodes:

/// Inscription d'un nouveau client
static Future<Map<String, dynamic>> registerClient({
  required String nom,
  required String prenom,
  required String email,
  required String telephone,
  required String adresse,
  required String password,
}) async {
  final payload = {
    'nom': nom,
    'prenom': prenom,
    'email': email,
    'telephone': telephone,
    'adresse': adresse,
    'password': password,
  };

  final uri = _uri('/api/clients/register');
  print('📝 Inscription client: $email');
  
  try {
    final res = await AuthApi.getHttpClient().post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    ).timeout(
      const Duration(seconds: 10),
      onTimeout: () => throw Exception('Timeout: serveur non accessible'),
    );

    print('📥 Réponse: ${res.statusCode}');
    
    if (res.statusCode == 201) {
      return jsonDecode(res.body);
    } else {
      final error = jsonDecode(res.body);
      throw Exception(error['error'] ?? 'Erreur lors de l\'inscription');
    }
  } catch (e) {
    throw Exception('Erreur inscription: $e');
  }
}

/// Récupère le profil du client
static Future<ClientModel> getClientProfile(String clientId) async {
  final uri = _uri('/api/clients/$clientId/profile');
  
  try {
    final res = await AuthApi.getHttpClient().get(uri).timeout(
      const Duration(seconds: 10),
    );

    if (res.statusCode == 200) {
      return ClientModel.fromJson(jsonDecode(res.body));
    } else {
      throw Exception('Erreur lors de la récupération du profil');
    }
  } catch (e) {
    throw Exception('Erreur profil client: $e');
  }
}

/// Récupère les comptes du client
static Future<List<CompteModel>> getClientAccounts(String clientId) async {
  final uri = _uri('/api/clients/$clientId/accounts');
  
  try {
    final res = await AuthApi.getHttpClient().get(uri).timeout(
      const Duration(seconds: 10),
    );

    if (res.statusCode == 200) {
      final List<dynamic> data = jsonDecode(res.body);
      return data.map((e) => CompteModel.fromJson(e as Map<String, dynamic>)).toList();
    } else {
      throw Exception('Erreur lors de la récupération des comptes');
    }
  } catch (e) {
    throw Exception('Erreur comptes: $e');
  }
}
```

### 3.2 Créer CollecteurApi.dart (Profil Collecteur)
```dart
// File: lib/services/collecteur_api.dart

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'auth_api.dart';

class CollecteurApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();
  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère le profil du collecteur
  static Future<CollecteurModel> getProfile(String collecteurId) async {
    final uri = _uri('/api/collecteur/$collecteurId/profile');
    
    try {
      final res = await _client.get(uri).timeout(const Duration(seconds: 10));
      
      if (res.statusCode == 200) {
        return CollecteurModel.fromJson(jsonDecode(res.body));
      } else {
        throw Exception('Erreur chargement profil');
      }
    } catch (e) {
      throw Exception('Erreur profil collecteur: $e');
    }
  }

  /// Récupère les transactions du collecteur
  static Future<List<TransactionModel>> getTransactions(String collecteurId, {
    String? statusFilter,
    int limit = 50,
  }) async {
    String path = '/api/collecteur/$collecteurId/transactions?limit=$limit';
    if (statusFilter != null) {
      path += '&status=$statusFilter';
    }
    
    final uri = _uri(path);
    
    try {
      final res = await _client.get(uri).timeout(const Duration(seconds: 10));
      
      if (res.statusCode == 200) {
        final List<dynamic> data = jsonDecode(res.body);
        return data.map((e) => TransactionModel.fromJson(e as Map<String, dynamic>)).toList();
      } else {
        throw Exception('Erreur chargement transactions');
      }
    } catch (e) {
      throw Exception('Erreur transactions: $e');
    }
  }

  /// Crée une nouvelle transaction (collecte)
  static Future<TransactionModel> createTransaction({
    required String collecteurId,
    required String idCompte,
    required double montant,
    required String typeTransaction,
  }) async {
    final payload = {
      'idCompte': idCompte,
      'montant': montant,
      'typeTransaction': typeTransaction,
    };

    final uri = _uri('/api/collecteur/$collecteurId/transactions');
    
    try {
      final res = await _client.post(
        uri,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload),
      ).timeout(const Duration(seconds: 10));

      if (res.statusCode == 201) {
        return TransactionModel.fromJson(jsonDecode(res.body));
      } else {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur création transaction');
      }
    } catch (e) {
      throw Exception('Erreur transaction: $e');
    }
  }

  /// Récupère les statistiques du collecteur
  static Future<Map<String, dynamic>> getStats(String collecteurId) async {
    final uri = _uri('/api/collecteur/$collecteurId/stats');
    
    try {
      final res = await _client.get(uri).timeout(const Duration(seconds: 10));
      
      if (res.statusCode == 200) {
        return jsonDecode(res.body);
      } else {
        throw Exception('Erreur chargement stats');
      }
    } catch (e) {
      throw Exception('Erreur stats: $e');
    }
  }
}
```

---

## 🎨 **PHASE 4: ÉCRANS (SCREENS)**

### 4.1 Créer LoginScreen.dart (avec logo)
```dart
// File: lib/screens/login_screen.dart

import 'package:flutter/material.dart';
import '../services/auth_api.dart';
import '../models/user_model.dart';
import 'dashboards/collecteur_dashboard.dart';
import 'dashboards/client_dashboard.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _login() async {
    if (_emailController.text.isEmpty || _passwordController.text.isEmpty) {
      _showError('Veuillez remplir tous les champs');
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final user = await AuthApi.login(
        email: _emailController.text,
        password: _passwordController.text,
      );

      if (mounted) {
        if (user.nomRole == 'COLLECTEUR') {
          Navigator.of(context).pushReplacementNamed(
            '/collecteur-dashboard',
            arguments: user,
          );
        } else if (user.nomRole == 'CLIENT') {
          Navigator.of(context).pushReplacementNamed(
            '/client-dashboard',
            arguments: user,
          );
        }
      }
    } catch (e) {
      _showError('Erreur: ${e.toString()}');
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  void _showError(String message) {
    setState(() => _errorMessage = message);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
        duration: const Duration(seconds: 4),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 48),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              // Logo
              Image.asset(
                'assets/images/logo-savely.png',
                height: 120,
                width: 120,
              ),
              const SizedBox(height: 16),
              
              // Titre
              const Text(
                'SAVELY',
                style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF0D8A5F),
                ),
              ),
              const Text(
                'Épargne & Collecte',
                style: TextStyle(
                  fontSize: 14,
                  color: Colors.grey,
                ),
              ),
              const SizedBox(height: 48),

              // Formulaire
              TextField(
                controller: _emailController,
                decoration: InputDecoration(
                  labelText: 'Email',
                  hintText: 'exemple@email.com',
                  prefixIcon: const Icon(Icons.email_outlined),
                ),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 16),

              TextField(
                controller: _passwordController,
                obscureText: _obscurePassword,
                decoration: InputDecoration(
                  labelText: 'Mot de passe',
                  prefixIcon: const Icon(Icons.lock_outlined),
                  suffixIcon: IconButton(
                    icon: Icon(_obscurePassword 
                      ? Icons.visibility_off 
                      : Icons.visibility),
                    onPressed: () => setState(
                      () => _obscurePassword = !_obscurePassword
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 32),

              // Bouton Connexion
              SizedBox(
                width: double.infinity,
                height: 48,
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _login,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF0D8A5F),
                    disabledBackgroundColor: Colors.grey[400],
                  ),
                  child: _isLoading
                    ? const SizedBox(
                        height: 24,
                        width: 24,
                        child: CircularProgressIndicator(
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                          strokeWidth: 2,
                        ),
                      )
                    : const Text(
                        'Connexion',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                ),
              ),
              const SizedBox(height: 16),

              // Bouton Inscription
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text('Pas encore inscrit ? '),
                  TextButton(
                    onPressed: () => Navigator.pushNamed(context, '/register'),
                    child: const Text(
                      'S\'inscrire',
                      style: TextStyle(
                        color: Color(0xFF0D8A5F),
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),

              // Message d'erreur
              if (_errorMessage != null)
                Padding(
                  padding: const EdgeInsets.only(top: 16),
                  child: Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: Colors.red[50],
                      border: Border.all(color: Colors.red[300]!),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      _errorMessage!,
                      style: TextStyle(
                        color: Colors.red[700],
                        fontSize: 12,
                      ),
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}
```

### 4.2 Créer RegisterScreen.dart (Inscription Client complète)
```dart
// File: lib/screens/register_screen.dart

import 'package:flutter/material.dart';
import '../services/client_api.dart';
import 'login_screen.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nomController = TextEditingController();
  final _prenomController = TextEditingController();
  final _emailController = TextEditingController();
  final _telephoneController = TextEditingController();
  final _adresseController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();

  bool _obscurePassword = true;
  bool _obscureConfirm = true;
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _nomController.dispose();
    _prenomController.dispose();
    _emailController.dispose();
    _telephoneController.dispose();
    _adresseController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  Future<void> _register() async {
    if (!_formKey.currentState!.validate()) return;

    if (_passwordController.text != _confirmPasswordController.text) {
      _showError('Les mots de passe ne correspondent pas');
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      await ClientApi.registerClient(
        nom: _nomController.text,
        prenom: _prenomController.text,
        email: _emailController.text,
        telephone: _telephoneController.text,
        adresse: _adresseController.text,
        password: _passwordController.text,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Inscription réussie! Connectez-vous maintenant.'),
            backgroundColor: Colors.green,
          ),
        );
        Navigator.of(context).pushReplacementNamed('/login');
      }
    } catch (e) {
      _showError('Erreur: ${e.toString()}');
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  void _showError(String message) {
    setState(() => _errorMessage = message);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Créer un compte'),
        centerTitle: true,
        elevation: 0,
        backgroundColor: const Color(0xFF0D8A5F),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Form(
            key: _formKey,
            child: Column(
              children: [
                // Logo
                Image.asset(
                  'assets/images/logo-savely.png',
                  height: 80,
                  width: 80,
                ),
                const SizedBox(height: 24),

                // Nom
                TextFormField(
                  controller: _nomController,
                  decoration: const InputDecoration(
                    labelText: 'Nom',
                    prefixIcon: Icon(Icons.person_outline),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) return 'Nom requis';
                    if (value.length < 2) return 'Nom trop court';
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Prénom
                TextFormField(
                  controller: _prenomController,
                  decoration: const InputDecoration(
                    labelText: 'Prénom',
                    prefixIcon: Icon(Icons.person_outline),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) return 'Prénom requis';
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Email
                TextFormField(
                  controller: _emailController,
                  decoration: const InputDecoration(
                    labelText: 'Email',
                    prefixIcon: Icon(Icons.email_outlined),
                  ),
                  keyboardType: TextInputType.emailAddress,
                  validator: (value) {
                    if (value == null || value.isEmpty) return 'Email requis';
                    if (!value.contains('@')) return 'Email invalide';
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Téléphone
                TextFormField(
                  controller: _telephoneController,
                  decoration: const InputDecoration(
                    labelText: 'Téléphone',
                    prefixIcon: Icon(Icons.phone_outlined),
                  ),
                  keyboardType: TextInputType.phone,
                  validator: (value) {
                    if (value == null || value.isEmpty) return 'Téléphone requis';
                    if (value.length < 8) return 'Numéro invalide';
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Adresse
                TextFormField(
                  controller: _adresseController,
                  decoration: const InputDecoration(
                    labelText: 'Adresse',
                    prefixIcon: Icon(Icons.location_on_outlined),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) return 'Adresse requise';
                    return null;
                  },
                  maxLines: 2,
                ),
                const SizedBox(height: 16),

                // Mot de passe
                TextFormField(
                  controller: _passwordController,
                  obscureText: _obscurePassword,
                  decoration: InputDecoration(
                    labelText: 'Mot de passe',
                    prefixIcon: const Icon(Icons.lock_outlined),
                    suffixIcon: IconButton(
                      icon: Icon(_obscurePassword 
                        ? Icons.visibility_off 
                        : Icons.visibility),
                      onPressed: () => setState(
                        () => _obscurePassword = !_obscurePassword
                      ),
                    ),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) return 'Mot de passe requis';
                    if (value.length < 6) return 'Au moins 6 caractères';
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Confirmation mot de passe
                TextFormField(
                  controller: _confirmPasswordController,
                  obscureText: _obscureConfirm,
                  decoration: InputDecoration(
                    labelText: 'Confirmer mot de passe',
                    prefixIcon: const Icon(Icons.lock_outlined),
                    suffixIcon: IconButton(
                      icon: Icon(_obscureConfirm 
                        ? Icons.visibility_off 
                        : Icons.visibility),
                      onPressed: () => setState(
                        () => _obscureConfirm = !_obscureConfirm
                      ),
                    ),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) 
                      return 'Confirmation requise';
                    return null;
                  },
                ),
                const SizedBox(height: 32),

                // Message erreur
                if (_errorMessage != null)
                  Container(
                    padding: const EdgeInsets.all(12),
                    margin: const EdgeInsets.only(bottom: 16),
                    decoration: BoxDecoration(
                      color: Colors.red[50],
                      border: Border.all(color: Colors.red[300]!),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      _errorMessage!,
                      style: TextStyle(color: Colors.red[700]),
                    ),
                  ),

                // Bouton Inscription
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: ElevatedButton(
                    onPressed: _isLoading ? null : _register,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF0D8A5F),
                    ),
                    child: _isLoading
                      ? const SizedBox(
                          height: 24,
                          width: 24,
                          child: CircularProgressIndicator(
                            valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                          ),
                        )
                      : const Text(
                          'S\'inscrire',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
```

### 4.3 Dashboard Collecteur Complet
```dart
// File: lib/screens/dashboards/collecteur_dashboard.dart
// AMÉLIORATION MAJEURE - Voir section détaillée ci-dessous

import 'package:flutter/material.dart';
import '../../models/user_model.dart';
import '../../models/collecteur_model.dart';
import '../../models/transaction_model.dart';
import '../../services/collecteur_api.dart';

class CollecteurDashboard extends StatefulWidget {
  final UserModel user;

  const CollecteurDashboard({
    required this.user,
    super.key,
  });

  @override
  State<CollecteurDashboard> createState() => _CollecteurDashboardState();
}

class _CollecteurDashboardState extends State<CollecteurDashboard> {
  late CollecteurModel? _profile;
  late List<TransactionModel> _transactions = [];
  late Map<String, dynamic> _stats = {};
  
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // Charger le profil
      _profile = await CollecteurApi.getProfile(widget.user.idEmploye);
      
      // Charger les transactions
      _transactions = await CollecteurApi.getTransactions(widget.user.idEmploye);
      
      // Charger les stats
      _stats = await CollecteurApi.getStats(widget.user.idEmploye);
      
      setState(() => _isLoading = false);
    } catch (e) {
      setState(() {
        _errorMessage = 'Erreur chargement: ${e.toString()}';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Image.asset(
                'assets/images/logo-savely.png',
                height: 60,
                width: 60,
              ),
              const SizedBox(height: 16),
              const CircularProgressIndicator(),
              const SizedBox(height: 16),
              const Text('Chargement...'),
            ],
          ),
        ),
      );
    }

    if (_errorMessage != null) {
      return Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.error_outline, 
                color: Colors.red, 
                size: 64,
              ),
              const SizedBox(height: 16),
              Text(
                _errorMessage!,
                textAlign: TextAlign.center,
                style: const TextStyle(color: Colors.red),
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _loadData,
                child: const Text('Réessayer'),
              ),
            ],
          ),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Tableau de Bord'),
        elevation: 0,
        backgroundColor: const Color(0xFF0D8A5F),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadData,
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // En-tête avec profil
            Container(
              color: const Color(0xFF0D8A5F),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    // Avatar + Info
                    Row(
                      children: [
                        Container(
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: Colors.white.withOpacity(0.2),
                          ),
                          padding: const EdgeInsets.all(12),
                          child: const Icon(
                            Icons.person,
                            color: Colors.white,
                            size: 32,
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                _profile?.fullName ?? 'Collecteur',
                                style: const TextStyle(
                                  color: Colors.white,
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              Text(
                                'Matricule: ${_profile?.displayMatricule ?? "---"}',
                                style: const TextStyle(
                                  color: Colors.white70,
                                  fontSize: 12,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),

            // KPIs
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  // Total collecté
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: Colors.blue[50],
                      border: Border.all(color: Colors.blue[200]!),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              'Total collecté',
                              style: TextStyle(
                                color: Colors.grey,
                                fontSize: 12,
                              ),
                            ),
                            SizedBox(height: 4),
                          ],
                        ),
                        Text(
                          '${(_profile?.totalCollecte ?? 0).toStringAsFixed(0)} FCFA',
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: Colors.blue,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),

                  // Row: Clients + Transactions
                  Row(
                    children: [
                      Expanded(
                        child: Container(
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.green[50],
                            border: Border.all(color: Colors.green[200]!),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text(
                                'Clients',
                                style: TextStyle(fontSize: 12, color: Colors.grey),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                '${_profile?.nbClients ?? 0}',
                                style: const TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.green,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Container(
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.orange[50],
                            border: Border.all(color: Colors.orange[200]!),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text(
                                'Transactions',
                                style: TextStyle(fontSize: 12, color: Colors.grey),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                '${_profile?.nbTransactions ?? 0}',
                                style: const TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.orange,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),

            // Transactions récentes
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Transactions récentes',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 12),
                  if (_transactions.isEmpty)
                    Container(
                      padding: const EdgeInsets.symmetric(vertical: 32),
                      child: Center(
                        child: Column(
                          children: [
                            Icon(Icons.inbox_outlined, 
                              color: Colors.grey[400],
                              size: 48,
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'Aucune transaction',
                              style: TextStyle(color: Colors.grey[500]),
                            ),
                          ],
                        ),
                      ),
                    )
                  else
                    ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: _transactions.length,
                      itemBuilder: (context, index) {
                        final tx = _transactions[index];
                        return Card(
                          margin: const EdgeInsets.only(bottom: 8),
                          child: ListTile(
                            leading: Container(
                              decoration: BoxDecoration(
                                shape: BoxShape.circle,
                                color: _getStatusColor(tx.statusValidation).withOpacity(0.2),
                              ),
                              padding: const EdgeInsets.all(8),
                              child: Icon(
                                _getStatusIcon(tx.statusValidation),
                                color: _getStatusColor(tx.statusValidation),
                              ),
                            ),
                            title: Text(tx.nomClient),
                            subtitle: Text(
                              '${tx.formattedDate} • ${tx.reference}',
                              style: const TextStyle(fontSize: 12),
                            ),
                            trailing: Text(
                              '${tx.formattedAmount} FCFA',
                              style: TextStyle(
                                fontWeight: FontWeight.w600,
                                color: _getStatusColor(tx.statusValidation),
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: const Color(0xFF0D8A5F),
        onPressed: () {
          // Naviguer vers créer transaction
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Feature à implémenter')),
          );
        },
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'VALIDEE':
        return Colors.green;
      case 'EN_ATTENTE':
        return Colors.orange;
      case 'REJETEE':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  IconData _getStatusIcon(String status) {
    switch (status) {
      case 'VALIDEE':
        return Icons.check_circle;
      case 'EN_ATTENTE':
        return Icons.schedule;
      case 'REJETEE':
        return Icons.cancel;
      default:
        return Icons.help;
    }
  }
}
```

### 4.4 Dashboard Client Complet
```dart
// File: lib/screens/dashboards/client_dashboard.dart
// STRUCTURE similaire au collecteur mais adapté pour client
// Afficher: Profil, Comptes, Historique transactions
```

---

## 🖼️ **PHASE 5: CRÉATION DU LOGO**

### Logo Savely PNG (512x512px)

Tu peux créer le logo avec:

**Option 1: Utiliser un outil en ligne (rapidement)**
- Allez sur Canva.com ou Figma
- Créez un design avec:
  - Un cœur stylisé (vert #0D8A5F)
  - Le texte "SAVELY" (gras, noir)
  - Fond blanc
- Export en PNG 512x512

**Option 2: Code pour générer un SVG convertible**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<svg width="512" height="512" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg">
  <!-- Fond blanc -->
  <rect width="512" height="512" fill="white"/>
  
  <!-- Cœur stylisé -->
  <path d="M 256 420 C 140 340 60 260 60 160 C 60 100 100 60 140 60 C 170 60 200 80 256 130 C 312 80 342 60 372 60 C 412 60 452 100 452 160 C 452 260 372 340 256 420 Z" 
    fill="#0D8A5F" stroke="none"/>
  
  <!-- Texte SAVELY -->
  <text x="256" y="480" font-family="Arial, sans-serif" font-size="48" font-weight="bold" 
    text-anchor="middle" fill="#212121">SAVELY</text>
</svg>
```

Ensuite convertis ce SVG en PNG avec ImageMagick ou tout convertisseur en ligne.

---

## 📱 **PHASE 6: MISE À JOUR pubspec.yaml**

```yaml
name: savely
description: "Application Savely pour épargne et collecte."
publish_to: 'none'

version: 1.0.0+1

environment:
  sdk: ^3.10.1

dependencies:
  flutter:
    sdk: flutter

  cupertino_icons: ^1.0.8
  http: ^1.2.1
  image_picker: ^1.0.7
  intl: ^0.19.0

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^6.0.0

flutter:
  uses-material-design: true
  
  assets:
    - assets/images/logo-savely.png
    - assets/images/
    - assets/icons/
  
  # Pour les fonts (optionnel)
  # fonts:
  #   - family: CustomFont
  #     fonts:
  #       - asset: assets/fonts/CustomFont-Regular.ttf
  #       - asset: assets/fonts/CustomFont-Bold.ttf
  #         weight: 700
```

---

## 🔄 **PHASE 7: GESTION D'ERREURS MOBILE**

### 7.1 ErrorHandler.dart (Classe d'aide)
```dart
// File: lib/utils/error_handler.dart

class ErrorHandler {
  static String getDisplayMessage(dynamic error) {
    if (error is Exception) {
      final message = error.toString();
      
      // Erreurs réseau
      if (message.contains('Connection refused')) {
        return 'Impossible de se connecter au serveur. Vérifiez votre connexion.';
      }
      if (message.contains('Timeout')) {
        return 'Le serveur ne répond pas. Réessayez plus tard.';
      }
      if (message.contains('Certificate')) {
        return 'Problème de certificat SSL. Vérifiez votre connexion.';
      }
      
      // Erreurs authentification
      if (message.contains('401')) {
        return 'Identifiants invalides. Vérifiez email et mot de passe.';
      }
      if (message.contains('403')) {
        return 'Vous n\'avez pas l\'accès à cette ressource.';
      }
      
      // Erreurs validation
      if (message.contains('validation')) {
        return 'Erreur de validation des données. Vérifiez vos entrées.';
      }
      
      // Erreur générique
      return message.replaceFirst('Exception: ', '');
    }
    
    return 'Une erreur inconnue s\'est produite. Réessayez.';
  }

  static void showErrorDialog(BuildContext context, dynamic error) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Erreur'),
        content: Text(getDisplayMessage(error)),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }
}
```

### 7.2 Retry avec Backoff
```dart
// File: lib/utils/retry_handler.dart

class RetryHandler {
  static Future<T> retryWithBackoff<T>({
    required Future<T> Function() fn,
    int maxRetries = 3,
    Duration initialDelay = const Duration(milliseconds: 100),
  }) async {
    int retries = 0;
    Duration delay = initialDelay;

    while (true) {
      try {
        return await fn();
      } catch (e) {
        retries++;
        if (retries >= maxRetries) rethrow;
        
        await Future.delayed(delay);
        delay *= 2; // Exponential backoff
      }
    }
  }
}
```

---

## 🚀 **ÉTAPES D'IMPLÉMENTATION MOBILE**

### ✅ Phase 1: Structure (2 heures)
1. [ ] Créer dossiers assets/images, assets/icons
2. [ ] Créer et ajouter logo-savely.png
3. [ ] Mettre à jour pubspec.yaml
4. [ ] Ajouter ErrorHandler et RetryHandler

### ✅ Phase 2: Modèles (1 heure)
1. [ ] Créer ClientModel.dart
2. [ ] Créer CompteModel.dart
3. [ ] Créer CollecteurModel.dart
4. [ ] Créer TransactionModel.dart

### ✅ Phase 3: Services API (2 heures)
1. [ ] Compléter ClientApi.dart
2. [ ] Créer CollecteurApi.dart
3. [ ] Tester endpoints avec ngrok

### ✅ Phase 4: Écrans (4 heures)
1. [ ] Créer LoginScreen.dart (avec logo)
2. [ ] Créer RegisterScreen.dart (9 champs + validation)
3. [ ] Compléter CollecteurDashboard (KPIs + transactions)
4. [ ] Compléter ClientDashboard (comptes + historique)

### ✅ Phase 5: Navigation & Routing (1 heure)
1. [ ] Configurer MaterialApp avec routes nommées
2. [ ] Ajouter logique auth pour redirection

### ✅ Phase 6: Tests (2 heures)
1. [ ] `flutter run` - test build complet
2. [ ] Tester inscription/login
3. [ ] Tester dashboards
4. [ ] Vérifier gestion d'erreurs

---

## 📋 **CHECKLIST MOBILE**

- ✅ Logo Savely sur login, register, dashboards
- ✅ Inscription client complète (9 champs)
- ✅ Matricule collecteur (default "0000", superviseur peut modifier)
- ✅ Dashboard collecteur (KPIs + transactions)
- ✅ Dashboard client (comptes + solde)
- ✅ Gestion erreurs réseau/validation
- ✅ HTTPS ngrok sans SSL errors
- ✅ Offline handling (caching optionnel)
- ✅ Refresh buttons sur tous dashboards

---

## 🎯 **RÉSUMÉ**

**Avant:** App avec structure basique, pas de logo, dashboards incomplets, gestion d'erreurs minimale.

**Après:** App complète avec:
- Logo Savely (marque professionnelle)
- Inscription client robuste (9 champs validés)
- Matricule collecteur (assignation auto)
- Dashboards riches (KPIs, statistiques)
- Gestion d'erreurs avancée (messages user-friendly)
- Connexion HTTPS ngrok stable

