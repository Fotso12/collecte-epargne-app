import 'dart:convert';
import 'package:http/http.dart' as http;
import '../services/auth_api.dart';

class CollecteApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère la liste des clients d'un collecteur
  static Future<List<dynamic>> getClientsCollecteur(String matricule) async {
    try {
      final uri = _uri('/api/employes/$matricule/clients');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les clients (${res.statusCode})');
      }

      return jsonDecode(res.body) as List<dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la récupération des clients: $e');
    }
  }

  /// Récupère les comptes d'un client
  static Future<List<dynamic>> getComptesClient(String codeClient) async {
    try {
      final uri = _uri('/api/comptes/client/$codeClient');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les comptes (${res.statusCode})');
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

  /// Récupère la liste des caissiers
  static Future<List<dynamic>> getCaissiers() async {
    try {
      final uri = _uri('/api/employes/caissiers');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les caissiers (${res.statusCode})');
      }

      return jsonDecode(res.body) as List<dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la récupération des caissiers: $e');
    }
  }

  /// Crée une nouvelle collecte (transaction)
  static Future<Map<String, dynamic>> creerCollecte({
    required String idCompte,
    required String typeTransaction,
    required double montant,
    required int idCaissier,
    String? description,
  }) async {
    try {
      final uri = _uri('/api/transactions');

      final payload = {
        'idCompte': idCompte,
        'typeTransaction': typeTransaction,
        'montant': montant,
        'idCaissierValidateur': idCaissier,
        if (description != null) 'description': description,
      };

      final res = await _client
          .post(
            uri,
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode(payload),
          )
          .timeout(
            const Duration(seconds: 30),
            onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200 && res.statusCode != 201) {
        final error = jsonDecode(res.body);
        throw Exception(
          error['message'] ?? error['error'] ?? 'Erreur lors de la création de la collecte',
        );
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la création de la collecte: $e');
    }
  }

  /// Récupère l'historique des collectes d'un collecteur
  static Future<List<dynamic>> getHistoriqueCollectes(String matricule) async {
    try {
      final uri = _uri('/api/transactions/collecteur/$matricule');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer l\'historique (${res.statusCode})');
      }

      return jsonDecode(res.body) as List<dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la récupération de l\'historique: $e');
    }
  }
}
