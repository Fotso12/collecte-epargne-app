import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/user_model.dart';

/// Client HTTP personnalisé pour accepter les certificats auto-signés (ngrok)
class _HttpClientWithSelfSignedCert extends http.BaseClient {
  final HttpClient _httpClient = HttpClient();

  _HttpClientWithSelfSignedCert() {
    // Accepte les certificats auto-signés (nécessaire pour ngrok en HTTPS)
    _httpClient.badCertificateCallback = (cert, host, port) => true;
  }

  @override
  Future<http.StreamedResponse> send(http.BaseRequest request) async {
    try {
      final clientRequest = await _httpClient.openUrl(
        request.method,
        request.url,
      );

      // Copier tous les headers
      request.headers.forEach((header, value) {
        clientRequest.headers.set(header, value);
      });

      // Envoyer le body et fermer la requête
      await clientRequest.addStream(request.finalize());
      final clientResponse = await clientRequest.close();

      // Lire la réponse complète
      final body = await clientResponse.cast<List<int>>().toList();
      final bodyBytes = body.isEmpty
          ? <int>[]
          : body.reduce((a, b) => [...a, ...b]);

      // Convertir les headers
      final headersMap = <String, String>{};
      clientResponse.headers.forEach((name, values) {
        headersMap[name] = values.join(',');
      });

      return http.StreamedResponse(
        Stream.value(bodyBytes),
        clientResponse.statusCode,
        request: request,
        headers: headersMap,
        isRedirect: clientResponse.isRedirect,
      );
    } catch (e) {
      throw Exception('Erreur HTTP: $e');
    }
  }
}

class RoleOption {
  final int id;
  final String code;
  final String nom;
  final String? description;

  RoleOption({
    required this.id,
    required this.code,
    required this.nom,
    this.description,
  });

  factory RoleOption.fromJson(Map<String, dynamic> json) => RoleOption(
    id: json['id'] as int,
    code: (json['code'] ?? '').toString(),
    nom: (json['nom'] ?? '').toString(),
    description: json['description']?.toString(),
  );
}

class AuthApi {
  // Client HTTP personnalisé pour accepter certificats auto-signés (ngrok)
  static final http.Client _httpClient = _HttpClientWithSelfSignedCert();

  // Stockage du token JWT et de l'ID utilisateur
  static String? token;
  static String? userId;
  static UserModel? _currentUser;

  // ⚠️ IMPORTANT: Utilise ngrok pour accéder au backend
  // URL ngrok: https://xochitl-subplexal-generally.ngrok-free.dev
  static String _webBase() {
    // Utilise ngrok pour accéder au backend depuis mobile
    return 'https://xochitl-subplexal-generally.ngrok-free.dev';
  }

  static const _androidBase =
      'https://xochitl-subplexal-generally.ngrok-free.dev'; // ngrok tunnel
  static const _defaultBase =
      'https://xochitl-subplexal-generally.ngrok-free.dev';

  static String _baseUrl() {
    if (kIsWeb) return _webBase();
    try {
      if (Platform.isAndroid) return _androidBase;
    } catch (_) {
      // Platform not supported (e.g. web) -> ignore
    }
    return _defaultBase;
  }

  static String getBaseUrl() => _baseUrl();

  /// Retourne le client HTTP personnalisé qui accepte les certificats auto-signés
  static http.Client getHttpClient() => _httpClient;

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère la liste des rôles disponibles.
  static Future<List<RoleOption>> fetchRoles() async {
    final uri = _uri('/api/roles');
    try {
      final res = await _httpClient
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas sur $uri'),
          );
      if (res.statusCode != 200) {
        final preview = res.body.length > 120
            ? '${res.body.substring(0, 120)}…'
            : res.body;
        throw Exception(
          'Impossible de charger les rôles (${res.statusCode}) : $preview',
        );
      }

      // Sécurise le parsing JSON pour éviter l'erreur "Unexpected token '<'" (réponse HTML).
      final body = res.body.trim();

      // Détecte si on reçoit du HTML au lieu de JSON (signe que l'URL pointe vers le frontend, pas le backend)
      if (body.startsWith('<!DOCTYPE html>') || body.startsWith('<html>')) {
        throw Exception(
          'Erreur: Le backend Spring n\'est pas accessible sur ${uri.toString()}. '
          'Vous recevez du HTML (page Flutter) au lieu de JSON.\n'
          'Vérifiez que:\n'
          '1. Le backend Spring est lancé sur le port 8082 (ou le port configuré)\n'
          '2. L\'endpoint http://localhost:8082/api/roles est accessible dans votre navigateur\n'
          '3. Le port dans _webBase() correspond au port du backend, pas du frontend Flutter',
        );
      }

      try {
        final decoded = jsonDecode(body);
        if (decoded is! List) {
          throw Exception(
            'Réponse inattendue /api/roles (attendu tableau JSON)',
          );
        }
        return decoded
            .map((e) => RoleOption.fromJson(e as Map<String, dynamic>))
            .toList();
      } catch (e) {
        final preview = body.length > 120 ? '${body.substring(0, 120)}…' : body;
        throw Exception("Parsing JSON impossible sur /api/roles : $preview");
      }
    } on http.ClientException catch (e) {
      throw Exception(
        'Erreur réseau: ${e.message}. Vérifiez que le backend est lancé sur ${_baseUrl()}',
      );
    } catch (e) {
      throw Exception('Erreur lors du chargement des rôles: $e');
    }
  }

  /// Authentifie un utilisateur et retourne ses informations + token
  static Future<Map<String, dynamic>> login({
    required String email,
    required String password,
  }) async {
    final payload = {'email': email, 'password': password};

    try {
      final uri = _uri('/api/auth/login');
      print('🔐 Tentative de connexion pour: $email');
      print('🌐 URL: $uri');

      final res = await _httpClient
          .post(
            uri,
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode(payload),
          )
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () {
              throw Exception(
                'Timeout: Le backend ne répond pas sur ${getBaseUrl()}. Vérifiez qu\'il est démarré.',
              );
            },
          );

      // Afficher détails dans la console
      print('📡 Statut HTTP: ${res.statusCode}');
      print('📄 Réponse serveur: ${res.body}');

      if (res.statusCode == 200) {
        final data = jsonDecode(res.body) as Map<String, dynamic>;

        // Créer l'objet utilisateur
        final user = UserModel.fromJson(data);

        // Extraire le token depuis la réponse (le backend peut le mettre en Authorization header ou dans le JSON)
        String? receivedToken = data['token'];
        if (receivedToken == null && res.headers.containsKey('authorization')) {
          receivedToken = res.headers['authorization']?.replaceFirst(
            'Bearer ',
            '',
          );
        }

        // Stocker le token et l'ID utilisateur
        token = receivedToken;
        userId = user.login; // Utiliser login comme ID unique
        _currentUser = user;

        print('✅ Connexion réussie: ${user.fullName} (${user.nomRole})');
        print('🔑 Token stocké: ${token?.substring(0, 20)}...');

        // Retourner le format attendu par LoginScreen
        return {
          'success': true,
          'user': user,
          'token': token,
          'message': 'Connexion réussie',
        };
      } else {
        final error = jsonDecode(res.body);
        final errorMsg =
            error['error'] ?? error['message'] ?? 'Erreur de connexion';
        print('❌ Erreur de connexion: $errorMsg');
        return {
          'success': false,
          'user': null,
          'token': null,
          'message': errorMsg,
        };
      }
    } on http.ClientException catch (e) {
      final baseUrl = getBaseUrl();
      final errorMsg =
          'Le backend n\'est pas accessible sur $baseUrl.\n'
          'Vérifiez que:\n'
          '1. Le backend Spring Boot est démarré\n'
          '2. Il écoute sur le port 8082\n'
          '3. MySQL est démarré et la base savings_collector existe\n'
          'Erreur technique: ${e.message}';
      print('🔴 Erreur réseau: $errorMsg');
      return {
        'success': false,
        'user': null,
        'token': null,
        'message': errorMsg,
      };
    } catch (e) {
      print('🔴 Exception lors de la connexion: $e');
      return {
        'success': false,
        'user': null,
        'token': null,
        'message': 'Erreur: $e',
      };
    }
  }

  /// Déconnecte l'utilisateur et efface le token
  static void logout() {
    print('🚪 Déconnexion de l\'utilisateur...');
    token = null;
    userId = null;
    _currentUser = null;
    print('✅ Déconnexion réussie');
  }

  /// Retourne l'utilisateur actuellement connecté
  static UserModel? get currentUser => _currentUser;

  /// Inscription d'un CLIENT avec email et mot de passe
  static Future<void> registerClient({
    required String fullName,
    required String phone,
    required String email,
    required String password,
    String? identityType,
    String? identityNumber,
    String? address,
    String?
    collectorMatricule, // Matricule du collecteur parrain (optionnel, "0000" si pas de parrain)
  }) async {
    final payload = {
      'fullName': fullName,
      'phone': phone,
      'email': email,
      'password': password,
      if (identityType != null) 'identityType': identityType,
      if (identityNumber != null) 'identityNumber': identityNumber,
      if (address != null) 'address': address,
      'collectorMatricule':
          collectorMatricule ?? '0000', // Par défaut "0000" si non fourni
      'institutionId': 1, // Institution par défaut
    };

    final res = await _httpClient.post(
      _uri('/api/registration/client'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    if (res.statusCode != 201) {
      final error = jsonDecode(res.body);
      throw Exception(error['error'] ?? 'Erreur lors de l\'inscription');
    }
  }

  /// Inscription d'un COLLECTEUR (user + employe)
  static Future<void> registerCollector({
    required String fullName,
    required String email,
    required String phone,
    required String password,
    String? badgeCode,
    String? zone,
  }) async {
    final payload = {
      'fullName': fullName,
      'email': email,
      'phone': phone,
      'password': password,
      if (badgeCode != null) 'badgeCode': badgeCode,
      if (zone != null) 'zone': zone,
      'institutionId': 1,
    };

    final res = await _httpClient.post(
      _uri('/api/registration/collector'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    if (res.statusCode != 201) {
      final error = jsonDecode(res.body);
      throw Exception(error['error'] ?? 'Erreur lors de l\'inscription');
    }
  }

  /// ANCIEN : Crée un utilisateur (inscription) selon UtilisateurCreationRequestDto.
  /// À SUPPRIMER une fois la migration terminée
  static Future<void> register({
    required String login,
    required int idRole,
    required String nom,
    required String prenom,
    required String telephone,
    required String email,
    required String password,
  }) async {
    final payload = {
      'login': login,
      'idRole': idRole,
      'nom': nom,
      'prenom': prenom,
      'telephone': telephone,
      'email': email,
      'password': password,
      'statut': 'ACTIF', // valeurs possibles: ACTIF, INACTIF, SUSPENDU
    };

    final res = await _httpClient.post(
      _uri('/api/utilisateurs'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    if (res.statusCode != 201) {
      throw Exception('Création impossible (${res.statusCode}): ${res.body}');
    }
  }
}
