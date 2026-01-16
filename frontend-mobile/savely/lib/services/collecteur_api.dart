import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/collecteur_model.dart';
import '../models/transaction_model.dart';
import 'auth_api.dart';

class CollecteurApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupérer le profil du collecteur avec KPIs
  /// GET /api/collecteur/{idEmploye}/profile
  static Future<CollecteurModel?> getProfile(String idEmploye) async {
    try {
      final uri = _uri('/api/collecteur/$idEmploye/profile');
      final res = await _client
          .get(uri)
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

      final data = jsonDecode(res.body) as Map<String, dynamic>;
      return CollecteurModel.fromJson(data);
    } catch (e) {
      print('[CollecteurApi] Error getting profile: $e');
      return null;
    }
  }

  /// Récupérer les KPIs du collecteur
  /// GET /api/collecteur/{idEmploye}/stats
  static Future<Map<String, dynamic>?> getStats(String idEmploye) async {
    try {
      final uri = _uri('/api/collecteur/$idEmploye/stats');
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer les stats (${res.statusCode})',
        );
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      print('[CollecteurApi] Error getting stats: $e');
      return null;
    }
  }

  /// Récupérer les transactions du collecteur
  /// GET /api/collecteur/{idEmploye}/transactions
  /// Query params: limit, offset, status, type
  static Future<List<TransactionModel>> getTransactions(
    String idEmploye, {
    int limit = 20,
    int offset = 0,
    String? status,
    String? type,
  }) async {
    try {
      final uri = _uri('/api/collecteur/$idEmploye/transactions').replace(
        queryParameters: {
          'limit': limit.toString(),
          'offset': offset.toString(),
          if (status != null) 'status': status,
          if (type != null) 'type': type,
        },
      );

      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer les transactions (${res.statusCode})',
        );
      }

      final data = jsonDecode(res.body);
      List<dynamic> list;

      if (data is List) {
        list = data;
      } else if (data is Map && data.containsKey('data')) {
        list = data['data'] as List? ?? [];
      } else {
        return [];
      }

      return list
          .map(
            (item) => TransactionModel.fromJson(item as Map<String, dynamic>),
          )
          .toList();
    } catch (e) {
      print('[CollecteurApi] Error getting transactions: $e');
      return [];
    }
  }

  /// Créer une nouvelle transaction
  /// POST /api/collecteur/{idEmploye}/transactions
  static Future<Map<String, dynamic>?> createTransaction({
    required String idEmploye,
    required String idCompte,
    required double montant,
    required String typeTransaction, // DEPOT, RETRAIT, COTISATION, etc.
    required String modeTransaction, // LIQUIDE, CHEQUE, VIREMENT, etc.
    String? description,
    String? signatureClient,
  }) async {
    try {
      final uri = _uri('/api/collecteur/$idEmploye/transactions');

      final payload = {
        'idCompte': idCompte,
        'montant': montant,
        'typeTransaction': typeTransaction,
        'modeTransaction': modeTransaction,
        if (description != null) 'description': description,
        if (signatureClient != null) 'signatureClient': signatureClient,
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

      if (res.statusCode == 200 || res.statusCode == 201) {
        return jsonDecode(res.body) as Map<String, dynamic>;
      } else {
        final error = jsonDecode(res.body);
        throw Exception(
          error['message'] ??
              'Erreur lors de la création de la transaction (${res.statusCode})',
        );
      }
    } catch (e) {
      print('[CollecteurApi] Error creating transaction: $e');
      return null;
    }
  }

  /// Récupérer les détails d'une transaction
  /// GET /api/collecteur/{idEmploye}/transactions/{idTransaction}
  static Future<TransactionModel?> getTransaction(
    String idEmploye,
    String idTransaction,
  ) async {
    try {
      final uri = _uri(
        '/api/collecteur/$idEmploye/transactions/$idTransaction',
      );

      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer la transaction (${res.statusCode})',
        );
      }

      final data = jsonDecode(res.body) as Map<String, dynamic>;
      return TransactionModel.fromJson(data);
    } catch (e) {
      print('[CollecteurApi] Error getting transaction: $e');
      return null;
    }
  }

  /// Récupérer les clients assignés au collecteur
  /// GET /api/collecteur/{idEmploye}/clients
  static Future<List<dynamic>> getClients(String idEmploye) async {
    try {
      final uri = _uri('/api/collecteur/$idEmploye/clients');

      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer les clients (${res.statusCode})',
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
      print('[CollecteurApi] Error getting clients: $e');
      return [];
    }
  }
}
