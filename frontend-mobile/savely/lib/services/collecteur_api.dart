import 'dart:convert';

import '../models/collecteur_model.dart';
import '../models/transaction_model.dart';
import 'auth_api.dart';
import 'employe_api.dart';
import 'transaction_offline_api.dart';

class CollecteurApi {
  /// Récupérer le profil du collecteur (accepte login ou matricule)
  /// Délègue à `EmployeApi.getByMatricule` après résolution éventuelle du login
  static Future<CollecteurModel?> getProfile(String loginOrMatricule) async {
    try {
      String matricule = loginOrMatricule;
      try {
        final resolved = await EmployeApi.getMatriculeByLogin(loginOrMatricule);
        if (resolved != null && resolved.isNotEmpty) matricule = resolved;
      } catch (_) {}

      final emp = await EmployeApi.getByMatricule(matricule);
      if (emp == null) return null;

      return CollecteurModel.fromJson(emp);
    } catch (e) {
      print('[CollecteurApi] Error getting profile: $e');
      return null;
    }
  }

  /// KPIs placeholder (backend may expose later)
  static Future<Map<String, dynamic>?> getStats(String loginOrMatricule) async {
    return null;
  }

  /// Récupérer les transactions du collecteur via TransactionOfflineApi
  static Future<List<TransactionModel>> getTransactions(
    String loginOrId, {
    int limit = 20,
    int offset = 0,
    String? status,
    String? type,
  }) async {
    try {
      String idEmp = loginOrId;
      try {
        final resolved = await EmployeApi.getIdEmployeByLogin(loginOrId);
        if (resolved != null && resolved.isNotEmpty) idEmp = resolved;
      } catch (_) {}

      final txs = await TransactionOfflineApi.getTransactionsByCollecteur(
        idEmp,
      );
      if (txs == null) return [];

      return (txs as List)
          .map((t) => TransactionModel.fromJson(t as Map<String, dynamic>))
          .toList();
    } catch (e) {
      print('[CollecteurApi] Error getting transactions: $e');
      return [];
    }
  }

  /// Créer une transaction offline (délégué à TransactionOfflineApi)
  static Future<Map<String, dynamic>?> createTransactionOffline({
    required String loginOrId,
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
      String idEmp = loginOrId;
      try {
        final resolved = await EmployeApi.getIdEmployeByLogin(loginOrId);
        if (resolved != null && resolved.isNotEmpty) idEmp = resolved;
      } catch (_) {}

      final created = await TransactionOfflineApi.createTransaction(
        idEmploye: idEmp,
        codeClient: codeClient,
        idCompte: idCompte,
        montant: montant,
        typeTransaction: typeTransaction,
        description: description,
        signatureClient: signatureClient,
        latitude: latitude,
        longitude: longitude,
      );

      return created;
    } catch (e) {
      print('[CollecteurApi] Error creating offline transaction: $e');
      return null;
    }
  }

  /// Récupérer le détail d'une transaction (cherche dans les transactions offline)
  static Future<TransactionModel?> getTransaction(
    String loginOrId,
    String idTransaction,
  ) async {
    try {
      String idEmp = loginOrId;
      try {
        final resolved = await EmployeApi.getIdEmployeByLogin(loginOrId);
        if (resolved != null && resolved.isNotEmpty) idEmp = resolved;
      } catch (_) {}

      final txs = await TransactionOfflineApi.getTransactionsByCollecteur(
        idEmp,
      );
      if (txs == null) return null;

      final found = (txs as List).cast<Map<String, dynamic>>().firstWhere((m) {
        final idFields = [
          m['id'],
          m['idTransaction'],
          m['id_transaction'],
          m['uuid'],
        ];
        return idFields.any((f) => f != null && f.toString() == idTransaction);
      }, orElse: () => <String, dynamic>{});

      if (found.isEmpty) return null;
      return TransactionModel.fromJson(found);
    } catch (e) {
      print('[CollecteurApi] Error getting transaction: $e');
      return null;
    }
  }

  /// Récupérer les clients assignés au collecteur (via matricule)
  static Future<List<dynamic>> getClients(String loginOrMatricule) async {
    try {
      String matricule = loginOrMatricule;
      try {
        final resolved = await EmployeApi.getMatriculeByLogin(loginOrMatricule);
        if (resolved != null && resolved.isNotEmpty) matricule = resolved;
      } catch (_) {}

      final clients = await EmployeApi.getClientsByCollecteur(matricule);
      return clients ?? [];
    } catch (e) {
      print('[CollecteurApi] Error getting clients: $e');
      return [];
    }
  }
}
