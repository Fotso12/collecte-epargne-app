import 'dart:convert';
import 'package:http/http.dart' as http;
import '../services/auth_api.dart';

class EmployeApi {
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère un employé par son matricule
  static Future<Map<String, dynamic>> getByMatricule(String matricule) async {
    try {
      final uri = _uri('/api/employes/$matricule');
      final res = await http.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer l\'employé (${res.statusCode})');
      }

      return jsonDecode(res.body) as Map<String, dynamic>;
    } catch (e) {
      throw Exception('Erreur lors de la récupération de l\'employé: $e');
    }
  }

  /// Récupère un employé par le login de l'utilisateur
  /// Retourne les informations complètes de l'employé (matricule, idEmploye, etc.)
  static Future<Map<String, dynamic>?> getEmployeByLogin(String login) async {
    try {
      // Chercher dans la liste des collecteurs
      final uri = _uri('/api/employes/collecteurs');
      final res = await http.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les collecteurs (${res.statusCode})');
      }

      final List<dynamic> collecteurs = jsonDecode(res.body);
      for (var collecteur in collecteurs) {
        final loginUtilisateur = collecteur['loginUtilisateur']?.toString();
        if (loginUtilisateur != null && loginUtilisateur == login) {
          return collecteur as Map<String, dynamic>;
        }
      }

      return null;
    } catch (e) {
      throw Exception('Erreur lors de la récupération de l\'employé: $e');
    }
  }

  /// Récupère le matricule d'un employé par son login (méthode de compatibilité)
  static Future<String?> getMatriculeByLogin(String login) async {
    final employe = await getEmployeByLogin(login);
    return employe?['matricule']?.toString();
  }

  /// Récupère l'ID_EMPLOYE d'un employé par son login
  static Future<String?> getIdEmployeByLogin(String login) async {
    final employe = await getEmployeByLogin(login);
    final idEmploye = employe?['idEmploye'];
    if (idEmploye != null) {
      return idEmploye.toString();
    }
    return null;
  }

  /// Récupère les clients assignés à un collecteur
  /// idCollecteur doit être l'ID_EMPLOYE (entier) et non le matricule
  static Future<List<Map<String, dynamic>>> getClientsByCollecteur(String idCollecteur) async {
    try {
      final uri = _uri('/api/employes/collecteurs/$idCollecteur/clients');
      final res = await http.get(uri).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('Timeout: le serveur ne répond pas'),
      );

      if (res.statusCode == 404 || res.statusCode == 204) {
        // Aucun client trouvé, retourner une liste vide
        return [];
      }

      if (res.statusCode == 400) {
        // Bad Request - probablement que l'ID n'est pas valide
        // Retourner une liste vide plutôt que de lancer une exception
        return [];
      }

      if (res.statusCode != 200) {
        throw Exception('Impossible de récupérer les clients (${res.statusCode})');
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
      // En cas d'erreur, retourner une liste vide plutôt que de propager l'erreur
      return [];
    }
  }
}

