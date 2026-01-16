import 'dart:convert';
import 'package:http/http.dart' as http;
import '../services/auth_api.dart';

class EmployeApi {
  static final http.Client _client = AuthApi.getHttpClient();
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Récupère un employé par son matricule
  static Future<Map<String, dynamic>> getByMatricule(String matricule) async {
    try {
      final uri = _uri('/api/employes/$matricule');
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer l\'employé (${res.statusCode})',
        );
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
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer les collecteurs (${res.statusCode})',
        );
      }

      final List<dynamic> collecteurs = jsonDecode(res.body);
      for (var collecteur in collecteurs) {
        // Plusieurs variantes possibles selon le backend: 'loginUtilisateur', 'login', ou utilisateur nested
        final c = collecteur as Map<String, dynamic>;
        final candidates = <String?>[
          c['login']?.toString(),
          c['loginUtilisateur']?.toString(),
          c['matricule']?.toString(),
        ];

        // Check nested structures
        if (c.containsKey('utilisateur') && c['utilisateur'] is Map) {
          final u = c['utilisateur'] as Map<String, dynamic>;
          candidates.add(u['login']?.toString());
          candidates.add(u['email']?.toString());
        }
        if (c.containsKey('user') && c['user'] is Map) {
          final u = c['user'] as Map<String, dynamic>;
          candidates.add(u['login']?.toString());
          candidates.add(u['email']?.toString());
        }

        for (var candidate in candidates) {
          if (candidate != null && candidate == login) {
            return c;
          }
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
  static Future<List<Map<String, dynamic>>> getClientsByCollecteur(
    String idCollecteur,
  ) async {
    try {
      // Le backend expose /api/employes/collecteurs/{matricule}/clients
      // Acceptons plusieurs formes d'identifiant: login, matricule ou idString

      String matricule = idCollecteur;

      // Si un login est fourni, tenter de résoudre le matricule
      try {
        final resolved = await getMatriculeByLogin(idCollecteur);
        if (resolved != null && resolved.isNotEmpty) {
          matricule = resolved;
        }
      } catch (_) {
        // Ignorer si la résolution échoue et utiliser la valeur fournie
      }

      final uri = _uri('/api/employes/collecteurs/$matricule/clients');
      final res = await _client
          .get(uri)
          .timeout(
            const Duration(seconds: 10),
            onTimeout: () =>
                throw Exception('Timeout: le serveur ne répond pas'),
          );

      if (res.statusCode == 404 || res.statusCode == 204) {
        return [];
      }

      if (res.statusCode == 400) {
        return [];
      }

      if (res.statusCode != 200) {
        throw Exception(
          'Impossible de récupérer les clients (${res.statusCode})',
        );
      }

      final dynamic body = jsonDecode(res.body);
      if (body == null) return [];
      if (body is List) return body.cast<Map<String, dynamic>>();
      return [];
    } catch (e) {
      // En cas d'erreur, retourner une liste vide plutôt que de propager l'erreur
      return [];
    }
  }
}
