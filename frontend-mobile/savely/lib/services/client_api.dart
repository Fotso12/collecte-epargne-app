import 'dart:convert';
import 'package:http/http.dart' as http;
import '../services/auth_api.dart';

class ClientApi {
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère le codeClient à partir du login de l'utilisateur
  static Future<String> getCodeClientByLogin(String login) async {
    try {
      final uri = _uri('/api/clients/login/$login');
      final res = await http.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode == 404) {
        throw Exception('Aucun client trouvé pour le login: $login');
      }

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer le client (${res.statusCode})');
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
      final res = await http.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer le client (${res.statusCode})');
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
      final res = await http.put(
        uri,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la mise à jour du client');
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la mise à jour du client: $e');
    }
  }
}

