import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/type_compte_model.dart';
import '../services/auth_api.dart';

class TypeCompteApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupérer tous les types de comptes disponibles
  static Future<List<TypeCompteModel>> getAll() async {
    try {
      final uri = _uri('/api/type-comptes');
      final res = await _client.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les types de comptes (${res.statusCode})');
      }

      final List<dynamic> data = jsonDecode(res.body);
      return data.map((json) => TypeCompteModel.fromJson(json as Map<String, dynamic>)).toList();
    } catch (e) {
      throw Exception('Erreur lors de la récupération des types de comptes: $e');
    }
  }

  /// Créer un nouveau type de compte
  static Future<TypeCompteModel> createTypeCompte({
    required String code,
    required String nom,
    String? description,
    double? tauxInteret,
    double? soldeMinimum,
    double? fraisOuverture,
    double? fraisCloture,
    bool? autoriserRetrait,
    int? dureeBlocageJours,
  }) async {
    try {
      final payload = {
        'code': code,
        'nom': nom,
        if (description != null && description.isNotEmpty) 'description': description,
        if (tauxInteret != null) 'tauxInteret': tauxInteret,
        if (soldeMinimum != null) 'soldeMinimum': soldeMinimum,
        if (fraisOuverture != null) 'fraisOuverture': fraisOuverture,
        if (fraisCloture != null) 'fraisCloture': fraisCloture,
        if (autoriserRetrait != null) 'autoriserRetrait': autoriserRetrait,
        if (dureeBlocageJours != null) 'dureeBlocageJours': dureeBlocageJours,
      };

      final res = await _client.post(
        _uri('/api/type-comptes'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 201) {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la création du type de compte');
      }

      return TypeCompteModel.fromJson(jsonDecode(res.body) as Map<String, dynamic>);
    } catch (e) {
      throw Exception('Erreur lors de la création du type de compte: $e');
    }
  }

  /// Mettre à jour un type de compte
  static Future<TypeCompteModel> updateTypeCompte({
    required int id,
    required String code,
    required String nom,
    String? description,
    double? tauxInteret,
    double? soldeMinimum,
    double? fraisOuverture,
    double? fraisCloture,
    bool? autoriserRetrait,
    int? dureeBlocageJours,
  }) async {
    try {
      final payload = {
        'code': code,
        'nom': nom,
        if (description != null && description.isNotEmpty) 'description': description,
        if (tauxInteret != null) 'tauxInteret': tauxInteret,
        if (soldeMinimum != null) 'soldeMinimum': soldeMinimum,
        if (fraisOuverture != null) 'fraisOuverture': fraisOuverture,
        if (fraisCloture != null) 'fraisCloture': fraisCloture,
        if (autoriserRetrait != null) 'autoriserRetrait': autoriserRetrait,
        if (dureeBlocageJours != null) 'dureeBlocageJours': dureeBlocageJours,
      };

      final res = await _client.put(
        _uri('/api/type-comptes/$id'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la mise à jour du type de compte');
      }

      return TypeCompteModel.fromJson(jsonDecode(res.body) as Map<String, dynamic>);
    } catch (e) {
      throw Exception('Erreur lors de la mise à jour du type de compte: $e');
    }
  }

  /// Supprimer un type de compte
  static Future<void> deleteTypeCompte(int id) async {
    try {
      final res = await _client.delete(
        _uri('/api/type-comptes/$id'),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 204) {
        throw Exception('Erreur lors de la suppression du type de compte');
      }
    } catch (e) {
      throw Exception('Erreur lors de la suppression du type de compte: $e');
    }
  }
}
