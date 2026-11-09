import 'dart:convert';
import 'package:http/http.dart' as http;
import '../services/auth_api.dart';

class ClientApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère le codeClient à partir du login de l'utilisateur
  static Future<String> getCodeClientByLogin(String login) async {
    try {
      final uri = _uri('/api/clients/login/$login');
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        // Log details for debugging
        try {
          print('UPDATE CLIENT -> HTTP ${res.statusCode}: ${res.body}');
        } catch (_) {}
        // Throw an exception that contains the HTTP status and body to help debugging
        throw Exception('UPDATE CLIENT -> HTTP ${res.statusCode}: ${res.body}');
      }
      final Map<String, dynamic> client = jsonDecode(res.body);
      return client['codeClient'] as String;
    } catch (e) {
      throw Exception('Erreur lors de la récupération du code client: $e');
    }
  }

  /// Récupère les informations complètes d'un client par son codeClient
  static Future<Map<String, dynamic>> getClientByCode(String codeClient) async {
    try {
      final uri = _uri('/api/clients/$codeClient');
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer le client (${res.statusCode})',
        );
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la récupération du client: $e');
    }
  }

  /// Met à jour les informations d'un client
  static Future<Map<String, dynamic>> updateClient({
    required String codeClient,
    String? adresse,
    String? typeCni,
    String? numCni,
    String? dateNaissance,
    String? lieuNaissance,
    String? profession,
    String? photoPath,
    String? cniRectoPath,
    String? cniVersoPath,
  }) async {
    try {
      final payload = <String, dynamic>{};
      if (adresse != null) payload['adresse'] = adresse;
      if (typeCni != null) payload['typeCni'] = typeCni;
      if (numCni != null) payload['numCni'] = numCni;
      if (dateNaissance != null) payload['dateNaissance'] = dateNaissance;
      if (lieuNaissance != null) payload['lieuNaissance'] = lieuNaissance;
      if (profession != null) payload['profession'] = profession;
      if (photoPath != null) payload['photoPath'] = photoPath;
      if (cniRectoPath != null) payload['cniRectoPath'] = cniRectoPath;
      if (cniVersoPath != null) payload['cniVersoPath'] = cniVersoPath;

      final uri = _uri('/api/clients/$codeClient');
      final res = await _client
          .put(
            uri,
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode(payload),
          )
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        final error = jsonDecode(res.body);
        throw Exception(
          error['error'] ?? 'Erreur lors de la mise à jour du client',
        );
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la mise à jour du client: $e');
    }
  }

  /// Enregistrer un nouveau client (Registration)
  /// POST /api/clients/register
  static Future<Map<String, dynamic>> registerClient({
    required String nom,
    required String prenom,
    required String email,
    required String telephone,
    required String password,
    required DateTime dateNaissance,
    required String lieuNaissance,
    required String profession,
    String? adresse,
    String? ville,
    String? typeCni,
    String? numCni,
    String collectorMatricule = '0000',
  }) async {
    try {
      final uri = _uri('/api/clients/register');

      final payload = {
        'fullName': '$nom $prenom',
        'phone': telephone,
        'email': email,
        'password': password,
        'dateNaissance': dateNaissance.toIso8601String().split('T').first,
        'lieuNaissance': lieuNaissance,
        'profession': profession,
        if (adresse != null) 'address': adresse,
        if (ville != null) 'ville': ville,
        if (typeCni != null) 'identityType': typeCni,
        if (numCni != null) 'identityNumber': numCni,
        if (collectorMatricule != '0000')
          'collectorMatricule': collectorMatricule,
      };

      final res = await _client
          .post(
            uri,
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode(payload),
          )
          .timeout(
            const Duration(seconds: 30),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      // Retourner un objet standardisé pour faciliter le traitement côté UI
      if (res.statusCode == 200 || res.statusCode == 201) {
        final body = jsonDecode(res.body) as Map<String, dynamic>;
        return {
          'success': true,
          'data': body,
          'message': 'Inscription réussie',
        };
      } else {
        // Essayer d'extraire le message d'erreur renvoyé par le backend
        String msg = 'Erreur lors de l\'enregistrement du client';
        String details = res.body;
        try {
          final error = jsonDecode(res.body);
          msg = error['message'] ?? error['error'] ?? msg;
        } catch (_) {}
        return {
          'success': false,
          'message': msg,
          'status': res.statusCode,
          'details': details,
        };
      }
    } catch (e) {
      throw Exception('Erreur lors de l\'enregistrement du client: $e');
    }
  }

  /// Récupérer le profil complet du client
  /// GET /api/clients/{id}/profile
  static Future<Map<String, dynamic>> getClientProfile(String clientId) async {
    try {
      // Certains déploiements exposent /api/clients/login/{login} et /api/clients/{codeClient}
      // On essaie d'abord la route login, puis on tombe en fallback vers la route par codeClient.
      final uriLogin = _uri('/api/clients/login/$clientId');
      var res = await _client
          .get(uriLogin)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );
      if (res.statusCode == 200) {
        return jsonDecode(res.body) as Map<String, dynamic>;
      }

      // Fallback: essayer par codeClient (ex: /api/clients/{codeClient})
      final uriCode = _uri('/api/clients/$clientId');
      res = await _client
          .get(uriCode)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer le profil (${res.statusCode})',
        );
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la récupération du profil: $e');
    }
  }

  /// Récupérer les comptes du client
  /// GET /api/clients/{id}/accounts
  static Future<List<dynamic>> getClientAccounts(String clientId) async {
    try {
      // Le backend expose l'endpoint /api/comptes/client/{codeClient}
      final uri = _uri('/api/comptes/client/$clientId');

      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );
      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer les comptes (${res.statusCode})',
        );
      }

      final data = jsonDecode(res.body);
      if (data is List) {
        return data;
      } else if (data is Map && data.containsKey('data')) {
        return data['data'] as List? ?? [];
      }
      return [];
    } catch (e) {
      throw Exception('Erreur lors de la récupération des comptes: $e');
    }
  }

  /// Upload des images CNI (recto et verso)
  static Future<Map<String, dynamic>> uploadCniImages({
    required String codeClient,
    required String rectoPath,
    required String versoPath,
  }) async {
    try {
      final uri = _uri('/api/clients/$codeClient/upload-cni');

      // Créer une requête multipart
      var request = http.MultipartRequest('POST', uri);

      // Ajouter les fichiers
      request.files.add(
        await http.MultipartFile.fromPath('cniRecto', rectoPath),
      );
      request.files.add(
        await http.MultipartFile.fromPath('cniVerso', versoPath),
      );

      // Envoyer la requête
      final streamedResponse = await request.send().timeout(
        const Duration(seconds: 30),
        onTimeout: () =>
            throw Exception('Timeout: le serveur ne répond pas'),
      );

      final res = await http.Response.fromStream(streamedResponse);

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible d\'uploader les images CNI (${res.statusCode})',
        );
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de l\'upload des images CNI: $e');
    }
  }
  /// Récupère l'historique des transactions d'un client
  static Future<List<dynamic>> getTransactions(String codeClient) async {
    try {
      // Endpoint supposé: /api/transactions/client/{codeClient}
      final uri = _uri('/api/transactions/client/$codeClient');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer l\'historique (${res.statusCode})');
      }

      final data = jsonDecode(res.body);
      if (data is List) {
        return data;
      } else if (data is Map && data.containsKey('data')) {
        return data['data'] as List? ?? [];
      }
      return [];
    } catch (e) {
      throw Exception('Erreur lors de la récupération de l\'historique: $e');
    }
  }
}
