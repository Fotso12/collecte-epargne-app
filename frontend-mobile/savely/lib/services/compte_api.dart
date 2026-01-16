import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/compte_model.dart';
import '../services/auth_api.dart';

class CompteApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère tous les comptes d'un client par son codeClient
  static Future<List<CompteModel>> getComptesByClient(String codeClient) async {
    try {
      final uri = _uri('/api/comptes/client/$codeClient');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode == 404) {
        // Aucun compte trouvé, retourner une liste vide
        return [];
      }

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les comptes (${res.statusCode})');
      }

      final List<dynamic> data = jsonDecode(res.body);
      return data.map((json) => CompteModel.fromJson(json as Map<String, dynamic>)).toList();
    } catch (e) {
      throw Exception('Erreur lors de la récupération des comptes: $e');
    }
  }

  /// Récupère un compte par son ID
  static Future<CompteModel> getCompteById(String idCompte) async {
    try {
      final uri = _uri('/api/comptes/$idCompte');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer le compte (${res.statusCode})');
      }

      return CompteModel.fromJson(jsonDecode(res.body) as Map<String, dynamic>);
    } catch (e) {
      throw Exception('Erreur lors de la récupération du compte: $e');
    }
  }
}

