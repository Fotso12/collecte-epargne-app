import 'dart:convert';
import 'package:http/http.dart' as http;
import '../services/auth_api.dart';

class TransactionOfflineApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Crée une nouvelle transaction offline (collecte)
  static Future<Map<String, dynamic>> createTransaction({
    required String idEmploye,
    required String codeClient,
    required String idCompte,
    required double montant,
    required String typeTransaction,
    String? description,
    String? signatureClient,
    double? latitude,
    double? longitude,
  }) async {
    try {
      final payload = {
        'idEmploye': idEmploye,
        'codeClient': codeClient,
        'idCompte': idCompte,
        'montant': montant,
        'typeTransaction': typeTransaction,
        'dateTransaction': DateTime.now().toIso8601String(),
        if (description != null) 'description': description,
        if (signatureClient != null) 'signatureClient': signatureClient,
        if (latitude != null) 'latitude': latitude,
        if (longitude != null) 'longitude': longitude,
        'statutSynchro': 'EN_ATTENTE',
      };

      final uri = _uri('/api/transactions-offline');
      final res = await _client
          .post(
            uri,
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode(payload),
          )
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 201 && res.statusCode != 200) {
        final error = jsonDecode(res.body);
        throw Exception(
          error['error'] ??
              'Erreur lors de la création de la transaction (${res.statusCode})',
        );
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la création de la transaction: $e');
    }
  }

  /// Récupère toutes les transactions offline d'un collecteur
  /// Note: Cet endpoint n'existe peut-être pas encore dans le backend
  static Future<List<Map<String, dynamic>>> getTransactionsByCollecteur(
    String idEmploye,
  ) async {
    try {
      final uri = _uri('/api/transactions-offline/collecteur/$idEmploye');
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode == 404 || res.statusCode == 204) {
        // Endpoint n'existe pas encore ou aucune transaction trouvée, retourner une liste vide
        return [];
      }

      if (res.statusCode != 200) {
        // Pour les autres erreurs, retourner une liste vide plutôt que de lancer une exception
        return [];
      }

      final dynamic body = jsonDecode(res.body);
      if (body == null) {
        return [];
      }

      if (body is List) {
        return body.cast<Map<String, dynamic>>();
      }

      return [];
    } catch (e) {
      // Si l'endpoint n'existe pas, retourner une liste vide
      return [];
    }
  }

  /// Récupère les transactions du jour pour un collecteur
  /// Note: Cet endpoint n'existe peut-être pas encore dans le backend
  static Future<List<Map<String, dynamic>>> getTodayTransactions(
    String idEmploye,
  ) async {
    try {
      final uri = _uri('/api/transactions-offline/collecteur/$idEmploye/today');
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode == 404 || res.statusCode == 204) {
        // Endpoint n'existe pas encore ou aucune transaction trouvée, retourner une liste vide
        return [];
      }

      if (res.statusCode != 200) {
        // Pour les autres erreurs, retourner une liste vide plutôt que de lancer une exception
        return [];
      }

      final dynamic body = jsonDecode(res.body);
      if (body == null) {
        return [];
      }

      if (body is List) {
        return body.cast<Map<String, dynamic>>();
      }

      return [];
    } catch (e) {
      // Si l'endpoint n'existe pas, retourner une liste vide
      return [];
    }
  }
}
