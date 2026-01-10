import 'dart:convert';
import 'package:http/http.dart' as http;
import '../services/auth_api.dart';

class UtilisateurApi {
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère un utilisateur par son login
  static Future<Map<String, dynamic>> getByLogin(String login) async {
    try {
      final uri = _uri('/api/utilisateurs/$login');
      final res = await http.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer l\'utilisateur (${res.statusCode})');
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la récupération de l\'utilisateur: $e');
    }
  }

  /// Met à jour les informations d'un utilisateur
  static Future<Map<String, dynamic>> updateUtilisateur({
    required String login,
    String? nom,
    String? prenom,
    String? email,
    String? telephone,
    String? photoPath,
  }) async {
    try {
      final payload = <String, dynamic>{};
      if (nom != null) payload['nom'] = nom;
      if (prenom != null) payload['prenom'] = prenom;
      if (email != null) payload['email'] = email;
      if (telephone != null) payload['telephone'] = telephone;
      if (photoPath != null) payload['photoPath'] = photoPath;

      final uri = _uri('/api/utilisateurs/$login');
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
        throw Exception(error['error'] ?? 'Erreur lors de la mise à jour de l\'utilisateur');
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la mise à jour de l\'utilisateur: $e');
    }
  }

  /// Met à jour le mot de passe d'un utilisateur
  static Future<void> updatePassword({
    required String login,
    required String newPassword,
  }) async {
    try {
      final payload = {'newPassword': newPassword};

      final uri = _uri('/api/utilisateurs/$login/password');
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
        throw Exception(error['error'] ?? 'Erreur lors de la mise à jour du mot de passe');
      }
    } catch (e) {
      throw Exception('Erreur lors de la mise à jour du mot de passe: $e');
    }
  }
}

