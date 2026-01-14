import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/demande_ouverture_compte_model.dart';
import '../services/auth_api.dart';

class DemandeApi {
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Créer une demande d'ouverture de compte
  static Future<DemandeOuvertureCompteModel> createDemande({
    required String codeClient,
    required int idTypeCompte,
    double? montantInitial,
    String? motif,
  }) async {
    try {
      final payload = {
        'codeClient': codeClient,
        'idTypeCompte': idTypeCompte,
        if (montantInitial != null) 'montantInitial': montantInitial,
        if (motif != null && motif.isNotEmpty) 'motif': motif,
        'statut': 'EN_ATTENTE',
      };

      final res = await http.post(
        _uri('/api/demandes-ouverture'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 201) {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la création de la demande');
      }

      return DemandeOuvertureCompteModel.fromJson(jsonDecode(res.body) as Map<String, dynamic>);
    } catch (e) {
      throw Exception('Erreur lors de la création de la demande: $e');
    }
  }

  /// Récupérer toutes les demandes d'un client
  static Future<List<DemandeOuvertureCompteModel>> getDemandesByClient(String codeClient) async {
    try {
      final uri = _uri('/api/demandes-ouverture/client/$codeClient');
      final res = await http.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode == 404) {
        return [];
      }

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les demandes (${res.statusCode})');
      }

      final List<dynamic> data = jsonDecode(res.body);
      return data.map((json) => DemandeOuvertureCompteModel.fromJson(json as Map<String, dynamic>)).toList();
    } catch (e) {
      throw Exception('Erreur lors de la récupération des demandes: $e');
    }
  }
}

