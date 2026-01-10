import 'dart:convert';
import 'package:http/http.dart' as http;
import 'auth_api.dart';

class AdminApi {
  static String _baseUrl() => AuthApi.getBaseUrl();

  static Uri _uri(String path) => Uri.parse('${_baseUrl()}$path');

  /// Créer une institution/agence
  static Future<void> createInstitution({
    required String name,
    required String code,
    String? contactEmail,
    String? contactPhone,
    String? timezone,
  }) async {
    final payload = {
      'name': name,
      'code': code,
      if (contactEmail != null && contactEmail.isNotEmpty) 'contactEmail': contactEmail,
      if (contactPhone != null && contactPhone.isNotEmpty) 'contactPhone': contactPhone,
      'timezone': timezone ?? 'Africa/Abidjan',
    };

    print('📤 Création agence: $payload');
    final uri = _uri('/api/admin/institutions');
    print('🌐 URL: $uri');

    final res = await http.post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    print('📥 Réponse: ${res.statusCode} - ${res.body}');

    if (res.statusCode != 201) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la création de l\'agence');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }

  /// Lister toutes les institutions
  static Future<List<Map<String, dynamic>>> getInstitutions() async {
    final uri = _uri('/api/admin/institutions');
    print('📤 Récupération agences: $uri');
    
    final res = await http.get(uri);

    print('📥 Réponse: ${res.statusCode} - ${res.body}');

    if (res.statusCode == 200) {
      final List<dynamic> data = jsonDecode(res.body);
      final institutions = data.map((e) => e as Map<String, dynamic>).toList();
      print('✅ ${institutions.length} agence(s) récupérée(s)');
      return institutions;
    } else {
      throw Exception('Erreur lors de la récupération des agences: ${res.statusCode} - ${res.body}');
    }
  }

  /// Modifier une institution/agence
  static Future<void> updateInstitution({
    required int id,
    required String name,
    required String code,
    String? contactEmail,
    String? contactPhone,
    String? timezone,
  }) async {
    final payload = {
      'name': name,
      'code': code,
      if (contactEmail != null && contactEmail.isNotEmpty) 'contactEmail': contactEmail,
      if (contactPhone != null && contactPhone.isNotEmpty) 'contactPhone': contactPhone,
      'timezone': timezone ?? 'Africa/Abidjan',
    };

    print('📤 Modification agence ID $id: $payload');
    final uri = _uri('/api/admin/institutions/$id');
    print('🌐 URL: $uri');

    final res = await http.put(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    print('📥 Réponse: ${res.statusCode} - ${res.body}');

    if (res.statusCode != 200) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la modification de l\'agence');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }

  /// Supprimer une institution/agence
  static Future<void> deleteInstitution(int id) async {
    final uri = _uri('/api/admin/institutions/$id');
    print('📤 Suppression agence ID $id: $uri');

    final res = await http.delete(uri);

    print('📥 Réponse: ${res.statusCode} - ${res.body}');

    if (res.statusCode != 200) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la suppression de l\'agence');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }

  /// Créer un utilisateur (caissier, collecteur, superviseur, auditeur)
  static Future<void> createUser({
    required String fullName,
    required String email,
    required String phone,
    required String password,
    required String roleCode,
    required int institutionId,
    String? badgeCode,
    String? zone,
    String? matricule,
  }) async {
    final payload = {
      'fullName': fullName,
      'email': email,
      'phone': phone,
      'password': password,
      'roleCode': roleCode,
      'institutionId': institutionId,
      if (badgeCode != null && badgeCode.isNotEmpty) 'badgeCode': badgeCode,
      if (zone != null && zone.isNotEmpty) 'zone': zone,
      if (matricule != null && matricule.isNotEmpty) 'matricule': matricule,
    };

    final res = await http.post(
      _uri('/api/admin/users'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    if (res.statusCode != 201) {
      final error = jsonDecode(res.body);
      throw Exception(error['error'] ?? 'Erreur lors de la création de l\'utilisateur');
    }
  }

  /// Lister tous les utilisateurs
  static Future<List<Map<String, dynamic>>> getUsers() async {
    final res = await http.get(_uri('/api/admin/users'));

    if (res.statusCode == 200) {
      final List<dynamic> data = jsonDecode(res.body);
      return data.map((e) => e as Map<String, dynamic>).toList();
    } else {
      throw Exception('Erreur lors de la récupération des utilisateurs');
    }
  }

  /// Récupérer les détails d'une institution
  static Future<Map<String, dynamic>> getInstitutionDetails(int institutionId) async {
    final uri = _uri('/api/admin/institutions/$institutionId');
    print('📤 Récupération détails institution: $uri');
    
    final res = await http.get(uri);

    if (res.statusCode == 200) {
      return jsonDecode(res.body) as Map<String, dynamic>;
    } else {
      throw Exception('Erreur lors de la récupération des détails: ${res.statusCode} - ${res.body}');
    }
  }

  /// Récupérer les employés d'une institution
  static Future<List<Map<String, dynamic>>> getInstitutionEmployees(int institutionId) async {
    final uri = _uri('/api/admin/institutions/$institutionId/employees');
    print('📤 Récupération employés institution: $uri');
    
    final res = await http.get(uri);

    if (res.statusCode == 200) {
      final List<dynamic> data = jsonDecode(res.body);
      return data.map((e) => e as Map<String, dynamic>).toList();
    } else {
      throw Exception('Erreur lors de la récupération des employés: ${res.statusCode} - ${res.body}');
    }
  }

  /// Affecter un employé à une institution
  static Future<void> assignEmployeeToInstitution({
    required int institutionId,
    required int employeeId,
  }) async {
    final uri = _uri('/api/admin/institutions/$institutionId/assign-employee');
    final payload = {'employeeId': employeeId};

    print('📤 Affectation employé: $payload');

    final res = await http.post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    if (res.statusCode != 200) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de l\'affectation');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }

  /// Retirer un employé d'une institution
  static Future<void> unassignEmployeeFromInstitution({
    required int institutionId,
    required int employeeId,
  }) async {
    final uri = _uri('/api/admin/institutions/$institutionId/unassign-employee');
    final payload = {'employeeId': employeeId};

    print('📤 Retrait employé: $payload');

    final res = await http.post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    if (res.statusCode != 200) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors du retrait');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }

  /// Récupérer les détails d'un utilisateur
  static Future<Map<String, dynamic>> getUserDetails(String login) async {
    final uri = _uri('/api/admin/users/$login');
    print('📤 Récupération détails utilisateur: $uri');
    
    final res = await http.get(uri);

    if (res.statusCode == 200) {
      return jsonDecode(res.body) as Map<String, dynamic>;
    } else {
      throw Exception('Erreur lors de la récupération des détails: ${res.statusCode} - ${res.body}');
    }
  }

  /// Changer le statut d'un utilisateur (ACTIF/INACTIF)
  static Future<void> updateUserStatus({
    required String login,
    required String statut, // 'ACTIF' ou 'INACTIF'
  }) async {
    final uri = _uri('/api/admin/users/$login/status');
    final payload = {'statut': statut};

    print('📤 Changement statut utilisateur $login: $statut');
    print('🌐 URL: $uri');

    final res = await http.patch(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    print('📥 Réponse: ${res.statusCode} - ${res.body}');

    if (res.statusCode != 200) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors du changement de statut');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }

  /// Modifier un utilisateur
  static Future<void> updateUser({
    required String login,
    required String fullName,
    required String email,
    required String phone,
    String? password,
    required String roleCode,
    required int institutionId,
    String? badgeCode,
    String? zone,
    String? matricule,
  }) async {
    final uri = _uri('/api/admin/users/$login');
    final payload = {
      'fullName': fullName,
      'email': email,
      'phone': phone,
      'roleCode': roleCode,
      'institutionId': institutionId,
      if (password != null && password.isNotEmpty) 'password': password,
      if (badgeCode != null && badgeCode.isNotEmpty) 'badgeCode': badgeCode,
      if (zone != null && zone.isNotEmpty) 'zone': zone,
      if (matricule != null && matricule.isNotEmpty) 'matricule': matricule,
    };

    print('📤 Modification utilisateur: $payload');

    final res = await http.put(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(payload),
    );

    if (res.statusCode != 200) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la modification');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }

  /// Supprimer un utilisateur
  static Future<void> deleteUser(String login) async {
    final uri = _uri('/api/admin/users/$login');
    print('📤 Suppression utilisateur: $uri');

    final res = await http.delete(uri);

    if (res.statusCode != 200) {
      try {
        final error = jsonDecode(res.body);
        throw Exception(error['error'] ?? 'Erreur lors de la suppression');
      } catch (e) {
        throw Exception('Erreur HTTP ${res.statusCode}: ${res.body}');
      }
    }
  }
}
