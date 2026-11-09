import 'package:flutter/material.dart';
import '../../models/user_model.dart';
import '../../models/type_compte_model.dart';
import '../../services/admin_api.dart';
import '../../services/type_compte_api.dart';
import 'institution_details.dart';

class AdminDashboard extends StatefulWidget {
  final UserModel user;

  const AdminDashboard({super.key, required this.user});

  @override
  State<AdminDashboard> createState() => _AdminDashboardState();
}

class _AdminDashboardState extends State<AdminDashboard> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  int _institutionsRefreshKey = 0;
  int _usersRefreshKey = 0;
  int _typeComptesRefreshKey = 0;
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = '';
  String _selectedRoleFilter = 'all';

  // Helper pour sécuriser les accès aux valeurs de Map
  String _safeString(Map<String, dynamic>? map, String key, {String defaultValue = ''}) {
    if (map == null) return defaultValue;
    final value = map[key];
    if (value == null) return defaultValue;
    return value.toString();
  }

  int _safeInt(Map<String, dynamic>? map, String key, {int defaultValue = 0}) {
    if (map == null) return defaultValue;
    final value = map[key];
    if (value == null) return defaultValue;
    if (value is int) return value;
    if (value is String) {
      final parsed = int.tryParse(value);
      return parsed ?? defaultValue;
    }
    return defaultValue;
  }

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _searchController.addListener(() {
      setState(() {
        _searchQuery = _searchController.text.toLowerCase();
      });
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Dashboard Admin'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () => Navigator.pushReplacementNamed(context, '/'),
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabs: const [
            Tab(icon: Icon(Icons.dashboard), text: 'Accueil'),
            Tab(icon: Icon(Icons.business), text: 'Agences'),
            Tab(icon: Icon(Icons.people), text: 'Utilisateurs'),
            Tab(icon: Icon(Icons.account_balance), text: 'Types de Comptes'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildHomeTab(context),
          _buildInstitutionsTab(context),
          _buildUsersTab(context),
          _buildTypeComptesTab(context),
        ],
      ),
    );
  }

  Widget _buildHomeTab(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildWelcomeCard(context),
          const SizedBox(height: 20),
          _buildStatsGrid(context),
          const SizedBox(height: 20),
          _buildQuickActions(context),
        ],
      ),
    );
  }

  Widget _buildWelcomeCard(BuildContext context) {
    // Gérer les valeurs vides pour prenom et nom
    final prenom = widget.user.prenom.isNotEmpty ? widget.user.prenom : '';
    final nom = widget.user.nom.isNotEmpty ? widget.user.nom : '';
    final initiales = (prenom.isNotEmpty ? prenom[0] : '') + 
                      (nom.isNotEmpty ? nom[0] : '');
    final displayName = prenom.isNotEmpty ? prenom : (nom.isNotEmpty ? nom : 'Utilisateur');
    
    return Card(
      color: Theme.of(context).colorScheme.primaryContainer,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            CircleAvatar(
              radius: 28,
              backgroundColor: Theme.of(context).colorScheme.primary,
              child: Text(
                initiales.isNotEmpty ? initiales.toUpperCase() : 'A',
                style: const TextStyle(fontSize: 20, color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    'Bienvenue, $displayName!',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 2),
                  Text(
                    'Administrateur Système',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Colors.grey[600],
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            Icon(
              Icons.admin_panel_settings,
              size: 28,
              color: Theme.of(context).colorScheme.primary,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatsGrid(BuildContext context) {
    return FutureBuilder<Map<String, dynamic>>(
      future: _loadStats(),
      builder: (context, snapshot) {
        final stats = snapshot.data ?? {
          'totalUsers': 0,
          'totalInstitutions': 0,
          'activeCollectors': 0,
          'totalClients': 0,
        };
        
        return GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          childAspectRatio: 1.6,
          children: [
            _buildStatCard(
              context,
              'Utilisateurs',
              '${stats['totalUsers']}',
              Icons.people,
              Colors.blue,
              isLoading: snapshot.connectionState == ConnectionState.waiting,
            ),
            _buildStatCard(
              context,
              'Agences',
              '${stats['totalInstitutions']}',
              Icons.business,
              Colors.green,
              isLoading: snapshot.connectionState == ConnectionState.waiting,
            ),
            _buildStatCard(
              context,
              'Collecteurs',
              '${stats['activeCollectors']}',
              Icons.hiking_rounded,
              Colors.orange,
              isLoading: snapshot.connectionState == ConnectionState.waiting,
            ),
            _buildStatCard(
              context,
              'Clients',
              '${stats['totalClients']}',
              Icons.person_outline,
              Colors.purple,
              isLoading: snapshot.connectionState == ConnectionState.waiting,
            ),
          ],
        );
      },
    );
  }

  Future<Map<String, dynamic>> _loadStats() async {
    try {
      final users = await AdminApi.getUsers();
      final institutions = await AdminApi.getInstitutions();
      
      final collectors = users.where((u) => (u['roleCode']?.toString() ?? '') == 'collector').length;
      final clients = users.where((u) => (u['roleCode']?.toString() ?? '') == 'client').length;
      
      return {
        'totalUsers': users.length,
        'totalInstitutions': institutions.length,
        'activeCollectors': collectors,
        'totalClients': clients,
      };
    } catch (e) {
      return {
        'totalUsers': 0,
        'totalInstitutions': 0,
        'activeCollectors': 0,
        'totalClients': 0,
      };
    }
  }

  Widget _buildStatCard(BuildContext context, String title, String value, IconData icon, Color color, {bool isLoading = false}) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [color.withOpacity(0.1), color.withOpacity(0.05)],
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                padding: const EdgeInsets.all(6),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.2),
                  shape: BoxShape.circle,
                ),
                child: Icon(icon, size: 24, color: color),
              ),
              const SizedBox(height: 8),
              if (isLoading)
                const SizedBox(
                  width: 18,
                  height: 18,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              else
                Flexible(
                  child: Text(
                    value,
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: color,
                      fontSize: 20,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              const SizedBox(height: 2),
              Flexible(
                child: Text(
                  title,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Colors.grey[600],
                    fontSize: 11,
                  ),
                  textAlign: TextAlign.center,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildQuickActions(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Actions Rapides', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 16),
            ListTile(
              leading: const Icon(Icons.business),
              title: const Text('Créer une Agence'),
              trailing: const Icon(Icons.arrow_forward_ios),
              onTap: () {
                _tabController.animateTo(1);
                _showCreateInstitutionDialog(context);
              },
            ),
            const Divider(),
            ListTile(
              leading: const Icon(Icons.person_add),
              title: const Text('Créer un Utilisateur'),
              trailing: const Icon(Icons.arrow_forward_ios),
              onTap: () {
                _tabController.animateTo(2);
                _showCreateUserDialog(context, null);
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInstitutionsTab(BuildContext context) {
    return FutureBuilder<List<Map<String, dynamic>>>(
      key: ValueKey('institutions_$_institutionsRefreshKey'),
      future: AdminApi.getInstitutions(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        if (snapshot.hasError) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.error_outline, size: 64, color: Colors.red[300]),
                const SizedBox(height: 16),
                Text(
                  'Erreur: ${snapshot.error}',
                  style: TextStyle(color: Colors.red[700]),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 16),
                ElevatedButton.icon(
                  onPressed: () {
                    setState(() {
                      _institutionsRefreshKey++;
                    });
                  },
                  icon: const Icon(Icons.refresh),
                  label: const Text('Réessayer'),
                ),
              ],
            ),
          );
        }
        final allInstitutions = snapshot.data ?? [];
        final institutions = allInstitutions.where((inst) {
          if (_searchQuery.isEmpty) return true;
          final name = (inst['name']?.toString() ?? '').toLowerCase();
          final code = (inst['code']?.toString() ?? '').toLowerCase();
          return name.contains(_searchQuery) || code.contains(_searchQuery);
        }).toList();
        
        return Column(
          children: [
            // Barre de recherche et bouton
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  TextField(
                    controller: _searchController,
                    decoration: InputDecoration(
                      hintText: 'Rechercher une agence...',
                      prefixIcon: const Icon(Icons.search),
                      suffixIcon: _searchQuery.isNotEmpty
                          ? IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                _searchController.clear();
                              },
                            )
                          : null,
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      filled: true,
                      fillColor: Colors.grey[100],
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: () => _showCreateInstitutionDialog(context),
                          icon: const Icon(Icons.add),
                          label: const Text('Créer une Agence'),
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 12),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      IconButton(
                        icon: const Icon(Icons.refresh),
                        tooltip: 'Actualiser',
                        onPressed: () {
                          setState(() {
                            _institutionsRefreshKey++;
                          });
                        },
                      ),
                    ],
                  ),
                  if (institutions.length != allInstitutions.length)
                    Padding(
                      padding: const EdgeInsets.only(top: 8),
                      child: Text(
                        '${institutions.length} sur ${allInstitutions.length} agence(s)',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.grey[600],
                        ),
                      ),
                    ),
                ],
              ),
            ),
            Expanded(
              child: institutions.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            _searchQuery.isNotEmpty ? Icons.search_off : Icons.business_center,
                            size: 64,
                            color: Colors.grey[300],
                          ),
                          const SizedBox(height: 16),
                          Text(
                            _searchQuery.isNotEmpty
                                ? 'Aucune agence trouvée pour "$_searchQuery"'
                                : 'Aucune agence',
                            style: TextStyle(color: Colors.grey[600]),
                          ),
                        ],
                      ),
                    )
                  : RefreshIndicator(
                      onRefresh: () async {
                        setState(() {
                          _institutionsRefreshKey++;
                        });
                        await Future.delayed(const Duration(milliseconds: 500));
                      },
                      child: ListView.builder(
                        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        itemCount: institutions.length,
                        itemBuilder: (context, index) {
                          final inst = institutions[index];
                          return Card(
                            margin: const EdgeInsets.only(bottom: 12),
                            elevation: 2,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: ListTile(
                              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                              leading: Container(
                                padding: const EdgeInsets.all(12),
                                decoration: BoxDecoration(
                                  color: Colors.blue.withOpacity(0.1),
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                child: const Icon(Icons.business, color: Colors.blue, size: 24),
                              ),
                              title: Text(
                                inst['name']?.toString() ?? '',
                                style: const TextStyle(fontWeight: FontWeight.bold),
                              ),
                              subtitle: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const SizedBox(height: 4),
                                  Row(
                                    children: [
                                      const Icon(Icons.tag, size: 14, color: Colors.grey),
                                      const SizedBox(width: 4),
                                      Text('Code: ${inst['code']?.toString() ?? ''}'),
                                    ],
                                  ),
                                  if (inst['contactEmail'] != null && inst['contactEmail'].toString().isNotEmpty)
                                    Padding(
                                      padding: const EdgeInsets.only(top: 4),
                                      child: Row(
                                        children: [
                                          const Icon(Icons.email, size: 14, color: Colors.grey),
                                          const SizedBox(width: 4),
                                          Expanded(
                                            child: Text(
                                              inst['contactEmail']?.toString() ?? '',
                                              style: const TextStyle(fontSize: 12),
                                              overflow: TextOverflow.ellipsis,
                                            ),
                                          ),
                                        ],
                                      ),
                                    ),
                                ],
                              ),
                              trailing: PopupMenuButton<String>(
                                icon: const Icon(Icons.more_vert),
                                onSelected: (value) {
                                  if (value == 'edit') {
                                    _showCreateInstitutionDialog(context, institutionDetails: inst);
                                  } else if (value == 'delete') {
                                    _showDeleteInstitutionConfirmation(context, inst);
                                  } else if (value == 'details') {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (context) => InstitutionDetailsPage(
                                          institutionId: (inst['id'] as int?) ?? 0,
                                        ),
                                      ),
                                    ).then((_) {
                                      // Rafraîchir la liste après retour
                                      setState(() {
                                        _institutionsRefreshKey++;
                                      });
                                    });
                                  }
                                },
                                itemBuilder: (context) => [
                                  const PopupMenuItem(
                                    value: 'details',
                                    child: Row(
                                      children: [
                                        Icon(Icons.info_outline, size: 20),
                                        SizedBox(width: 8),
                                        Text('Détails'),
                                      ],
                                    ),
                                  ),
                                  const PopupMenuItem(
                                    value: 'edit',
                                    child: Row(
                                      children: [
                                        Icon(Icons.edit, size: 20),
                                        SizedBox(width: 8),
                                        Text('Modifier'),
                                      ],
                                    ),
                                  ),
                                  const PopupMenuItem(
                                    value: 'delete',
                                    child: Row(
                                      children: [
                                        Icon(Icons.delete, size: 20, color: Colors.red),
                                        SizedBox(width: 8),
                                        Text('Supprimer', style: TextStyle(color: Colors.red)),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                              onTap: () {
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                    builder: (context) => InstitutionDetailsPage(
                                      institutionId: (inst['id'] as int?) ?? 0,
                                    ),
                                  ),
                                ).then((_) {
                                  // Rafraîchir la liste après retour
                                  setState(() {
                                    _institutionsRefreshKey++;
                                  });
                                });
                              },
                            ),
                          );
                        },
                      ),
                    ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildUsersTab(BuildContext context) {
    return FutureBuilder<List<Map<String, dynamic>>>(
      key: ValueKey('users_$_usersRefreshKey'),
      future: AdminApi.getUsers(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        if (snapshot.hasError) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.error_outline, size: 64, color: Colors.red[300]),
                const SizedBox(height: 16),
                Text(
                  'Erreur: ${snapshot.error}',
                  style: TextStyle(color: Colors.red[700]),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 16),
                ElevatedButton.icon(
                  onPressed: () {
                    setState(() {
                      _usersRefreshKey++;
                    });
                  },
                  icon: const Icon(Icons.refresh),
                  label: const Text('Réessayer'),
                ),
              ],
            ),
          );
        }
        final allUsers = snapshot.data ?? [];
        
        // Filtrer par recherche et rôle
        final filteredUsers = allUsers.where((user) {
          // Filtre par recherche
          if (_searchQuery.isNotEmpty) {
            final prenom = user['prenom']?.toString() ?? '';
            final nom = user['nom']?.toString() ?? '';
            final name = '$prenom $nom'.toLowerCase();
            final email = (user['email']?.toString() ?? '').toLowerCase();
            final login = (user['login']?.toString() ?? '').toLowerCase();
            if (!name.contains(_searchQuery) && 
                !email.contains(_searchQuery) && 
                !login.contains(_searchQuery)) {
              return false;
            }
          }
          // Filtre par rôle
          if (_selectedRoleFilter != 'all') {
            final userRoleCode = user['roleCode']?.toString() ?? '';
            if (userRoleCode != _selectedRoleFilter) {
              return false;
            }
          }
          return true;
        }).toList();
        
        // Grouper les utilisateurs par rôle
        final Map<String, List<Map<String, dynamic>>> usersByRole = {};
        for (var user in filteredUsers) {
          final roleCode = user['roleCode']?.toString() ?? 'autre';
          if (!usersByRole.containsKey(roleCode)) {
            usersByRole[roleCode] = [];
          }
          usersByRole[roleCode]!.add(user);
        }
        
        // Ordre d'affichage des rôles
        final roleOrder = ['admin', 'supervisor', 'caissier', 'collector', 'auditor', 'client'];
        final sortedRoles = roleOrder.where((role) => usersByRole.containsKey(role)).toList();
        // Ajouter les autres rôles non listés
        sortedRoles.addAll(usersByRole.keys.where((role) => !roleOrder.contains(role)));
        
        return Column(
          children: [
            // Barre de recherche et filtres
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  TextField(
                    controller: _searchController,
                    decoration: InputDecoration(
                      hintText: 'Rechercher un utilisateur...',
                      prefixIcon: const Icon(Icons.search),
                      suffixIcon: _searchQuery.isNotEmpty
                          ? IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                _searchController.clear();
                              },
                            )
                          : null,
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      filled: true,
                      fillColor: Colors.grey[100],
                    ),
                  ),
                  const SizedBox(height: 12),
                  // Filtre par rôle
                  SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Row(
                      children: [
                        _buildRoleFilterChip('all', 'Tous', Icons.people),
                        const SizedBox(width: 8),
                        _buildRoleFilterChip('admin', 'Admin', Icons.admin_panel_settings),
                        const SizedBox(width: 8),
                        _buildRoleFilterChip('supervisor', 'Superviseur', Icons.supervisor_account),
                        const SizedBox(width: 8),
                        _buildRoleFilterChip('caissier', 'Caissier', Icons.account_balance_wallet),
                        const SizedBox(width: 8),
                        _buildRoleFilterChip('collector', 'Collecteur', Icons.person_search),
                        const SizedBox(width: 8),
                        _buildRoleFilterChip('auditor', 'Auditeur', Icons.verified_user),
                        const SizedBox(width: 8),
                        _buildRoleFilterChip('client', 'Client', Icons.person),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: () => _showCreateUserDialog(context, null),
                          icon: const Icon(Icons.person_add),
                          label: const Text('Créer un Utilisateur'),
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 12),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      IconButton(
                        icon: const Icon(Icons.refresh),
                        tooltip: 'Actualiser',
                        onPressed: () {
                          setState(() {
                            _usersRefreshKey++;
                          });
                        },
                      ),
                    ],
                  ),
                  if (filteredUsers.length != allUsers.length)
                    Padding(
                      padding: const EdgeInsets.only(top: 8),
                      child: Text(
                        '${filteredUsers.length} sur ${allUsers.length} utilisateur(s)',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.grey[600],
                        ),
                      ),
                    ),
                ],
              ),
            ),
            Expanded(
              child: sortedRoles.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            _searchQuery.isNotEmpty || _selectedRoleFilter != 'all'
                                ? Icons.search_off
                                : Icons.people_outline,
                            size: 64,
                            color: Colors.grey[300],
                          ),
                          const SizedBox(height: 16),
                          Text(
                            _searchQuery.isNotEmpty || _selectedRoleFilter != 'all'
                                ? 'Aucun utilisateur trouvé'
                                : 'Aucun utilisateur',
                            style: TextStyle(color: Colors.grey[600]),
                          ),
                        ],
                      ),
                    )
                  : RefreshIndicator(
                      onRefresh: () async {
                        setState(() {
                          _usersRefreshKey++;
                        });
                        await Future.delayed(const Duration(milliseconds: 500));
                      },
                      child: ListView.builder(
                        padding: const EdgeInsets.symmetric(vertical: 8),
                        itemCount: sortedRoles.length,
                        itemBuilder: (context, index) {
                          final roleCode = sortedRoles[index];
                          final roleUsers = usersByRole[roleCode]!;
                          final roleName = roleUsers.isNotEmpty 
                              ? (roleUsers.first['roleName']?.toString() ?? roleCode)
                              : roleCode;
                          
                          return _buildRoleSection(roleName, roleCode, roleUsers);
                        },
                      ),
                    ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildRoleSection(String roleName, String roleCode, List<Map<String, dynamic>> users) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: ExpansionTile(
        leading: _getRoleIcon(roleCode),
        title: Text(
          roleName,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
        ),
        subtitle: Row(
          children: [
            Text('${users.length} utilisateur(s)'),
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: _getRoleColor(roleCode).withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                roleCode.toUpperCase(),
                style: TextStyle(
                  fontSize: 10,
                  color: _getRoleColor(roleCode),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
        children: [
          ...users.map((user) => _buildUserCard(user, roleCode)),
        ],
      ),
    );
  }

  Widget _buildUserCard(Map<String, dynamic> user, String roleCode) {
    final isActive = (user['statut']?.toString() ?? 'ACTIF') == 'ACTIF';
    final prenom = user['prenom']?.toString() ?? '';
    final nom = user['nom']?.toString() ?? '';
    final fullName = '$prenom $nom'.trim();
    final email = user['email']?.toString() ?? '';
    final phone = user['phone']?.toString() ?? '';
    
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(
          color: isActive ? Colors.green.withOpacity(0.3) : (Colors.grey[300] ?? Colors.grey),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        leading: CircleAvatar(
          backgroundColor: _getRoleColor(roleCode).withOpacity(0.2),
          child: Text(
            fullName.isNotEmpty 
                ? fullName[0].toUpperCase()
                : (email.isNotEmpty ? email[0].toUpperCase() : '?'),
            style: TextStyle(
              color: _getRoleColor(roleCode),
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        title: Row(
          children: [
            Expanded(
              child: Text(
                fullName.isNotEmpty ? fullName : email,
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
              decoration: BoxDecoration(
                color: isActive ? Colors.green.withOpacity(0.1) : Colors.grey.withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                isActive ? 'Actif' : 'Inactif',
                style: TextStyle(
                  fontSize: 10,
                  color: isActive ? Colors.green[700] : Colors.grey[700],
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            if (email.isNotEmpty)
              Row(
                children: [
                  const Icon(Icons.email, size: 14, color: Colors.grey),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      email,
                      style: const TextStyle(fontSize: 12),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
            if (phone.isNotEmpty)
              Padding(
                padding: const EdgeInsets.only(top: 4),
                child: Row(
                  children: [
                    const Icon(Icons.phone, size: 14, color: Colors.grey),
                    const SizedBox(width: 4),
                    Text(
                      phone,
                      style: const TextStyle(fontSize: 12),
                    ),
                  ],
                ),
              ),
          ],
        ),
        trailing: PopupMenuButton<String>(
          icon: const Icon(Icons.more_vert),
          onSelected: (value) {
            if (value == 'edit') {
              _showCreateUserDialog(context, roleCode, userDetails: user);
            } else if (value == 'toggle_status') {
              _toggleUserStatus(context, user);
            } else if (value == 'delete') {
              _showDeleteUserConfirmation(context, user);
            }
          },
          itemBuilder: (context) => [
            const PopupMenuItem(
              value: 'edit',
              child: Row(
                children: [
                  Icon(Icons.edit, size: 18),
                  SizedBox(width: 8),
                  Text('Modifier'),
                ],
              ),
            ),
            PopupMenuItem(
              value: 'toggle_status',
              child: Row(
                children: [
                  Icon(
                    isActive ? Icons.block : Icons.check_circle,
                    size: 18,
                    color: isActive ? Colors.orange : Colors.green,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    isActive ? 'Désactiver' : 'Activer',
                    style: TextStyle(color: isActive ? Colors.orange : Colors.green),
                  ),
                ],
              ),
            ),
            const PopupMenuItem(
              value: 'delete',
              child: Row(
                children: [
                  Icon(Icons.delete, size: 18, color: Colors.red),
                  SizedBox(width: 8),
                  Text('Supprimer', style: TextStyle(color: Colors.red)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRoleFilterChip(String roleCode, String label, IconData icon) {
    final isSelected = _selectedRoleFilter == roleCode;
    return FilterChip(
      label: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 16, color: isSelected ? Colors.white : Colors.grey),
          const SizedBox(width: 4),
          Text(label),
        ],
      ),
      selected: isSelected,
      onSelected: (selected) {
        setState(() {
          _selectedRoleFilter = roleCode;
        });
      },
      selectedColor: Theme.of(context).colorScheme.primary,
      labelStyle: TextStyle(
        color: isSelected ? Colors.white : Colors.grey[700],
        fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
      ),
    );
  }

  Icon _getRoleIcon(String roleCode) {
    switch (roleCode) {
      case 'admin':
        return const Icon(Icons.admin_panel_settings, color: Colors.red);
      case 'supervisor':
        return const Icon(Icons.supervisor_account, color: Colors.blue);
      case 'caissier':
        return const Icon(Icons.account_balance_wallet, color: Colors.green);
      case 'collector':
        return const Icon(Icons.person_search, color: Colors.orange);
      case 'auditor':
        return const Icon(Icons.verified_user, color: Colors.purple);
      case 'client':
        return const Icon(Icons.person, color: Colors.teal);
      default:
        return const Icon(Icons.person_outline, color: Colors.grey);
    }
  }

  Color _getRoleColor(String roleCode) {
    switch (roleCode) {
      case 'admin':
        return Colors.red;
      case 'supervisor':
        return Colors.blue;
      case 'caissier':
        return Colors.green;
      case 'collector':
        return Colors.orange;
      case 'auditor':
        return Colors.purple;
      case 'client':
        return Colors.teal;
      default:
        return Colors.grey;
    }
  }

  void _showCreateInstitutionDialog(BuildContext context, {Map<String, dynamic>? institutionDetails}) {
    final isEditMode = institutionDetails != null;
    final nameController = TextEditingController(text: isEditMode ? (institutionDetails['name']?.toString() ?? '') : '');
    final codeController = TextEditingController(text: isEditMode ? (institutionDetails['code']?.toString() ?? '') : '');
    final emailController = TextEditingController(text: isEditMode ? (institutionDetails['contactEmail']?.toString() ?? '') : '');
    final phoneController = TextEditingController(text: isEditMode ? (institutionDetails['contactPhone']?.toString() ?? '') : '');
    bool isLoading = false;

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (context, setState) => AlertDialog(
          title: Text(isEditMode ? 'Modifier l\'Agence' : 'Créer une Agence'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: nameController,
                  decoration: const InputDecoration(labelText: 'Nom de l\'agence *'),
                ),
                TextField(
                  controller: codeController,
                  decoration: const InputDecoration(labelText: 'Code *'),
                ),
                TextField(
                  controller: emailController,
                  decoration: const InputDecoration(labelText: 'Email de contact'),
                  keyboardType: TextInputType.emailAddress,
                ),
                TextField(
                  controller: phoneController,
                  decoration: const InputDecoration(labelText: 'Téléphone de contact'),
                  keyboardType: TextInputType.phone,
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: isLoading ? null : () => Navigator.pop(ctx),
              child: const Text('Annuler'),
            ),
            ElevatedButton(
              onPressed: isLoading ? null : () async {
                if (nameController.text.isEmpty || codeController.text.isEmpty) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Nom et code requis')),
                  );
                  return;
                }
                setState(() => isLoading = true);
                try {
                  if (isEditMode) {
                    final instId = institutionDetails['id'] as int?;
                    if (instId == null) {
                      throw Exception('ID institution introuvable');
                    }
                    await AdminApi.updateInstitution(
                      id: instId,
                      name: nameController.text,
                      code: codeController.text,
                      contactEmail: emailController.text.isEmpty ? null : emailController.text,
                      contactPhone: phoneController.text.isEmpty ? null : phoneController.text,
                    );
                  } else {
                    await AdminApi.createInstitution(
                      name: nameController.text,
                      code: codeController.text,
                      contactEmail: emailController.text.isEmpty ? null : emailController.text,
                      contactPhone: phoneController.text.isEmpty ? null : phoneController.text,
                    );
                  }
                  if (context.mounted) {
                    Navigator.pop(ctx);
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text(isEditMode ? 'Agence modifiée avec succès' : 'Agence créée avec succès'),
                        backgroundColor: Colors.green,
                      ),
                    );
                    // Rafraîchir la liste des institutions
                    Future.delayed(const Duration(milliseconds: 100), () {
                      if (mounted) {
                        setState(() {
                          _institutionsRefreshKey++;
                        });
                      }
                    });
                  }
                } catch (e) {
                  if (context.mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('Erreur: $e'),
                        backgroundColor: Colors.red,
                      ),
                    );
                  }
                } finally {
                  setState(() => isLoading = false);
                }
              },
              child: isLoading
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                    )
                  : Text(isEditMode ? 'Enregistrer' : 'Créer'),
            ),
          ],
        ),
      ),
    );
  }

  void _showDeleteInstitutionConfirmation(BuildContext context, Map<String, dynamic> institution) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Supprimer l\'agence'),
        content: Text('Êtes-vous sûr de vouloir supprimer l\'agence "${institution['name']}" ?\n\nCette action est irréversible.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('Annuler'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(ctx);
              try {
                final instId = institution['id'] as int?;
                if (instId == null) {
                  throw Exception('ID institution introuvable');
                }
                await AdminApi.deleteInstitution(instId);
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Agence supprimée avec succès'),
                      backgroundColor: Colors.green,
                    ),
                  );
                  // Rafraîchir la liste des institutions
                  Future.delayed(const Duration(milliseconds: 100), () {
                    if (mounted) {
                      setState(() {
                        _institutionsRefreshKey++;
                      });
                    }
                  });
                }
              } catch (e) {
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Erreur: $e'),
                      backgroundColor: Colors.red,
                    ),
                  );
                }
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Supprimer', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  void _showCreateUserDialog(BuildContext context, String? roleCode, {Map<String, dynamic>? userDetails}) {
    final isEditMode = userDetails != null;
    
    final fullNameController = TextEditingController(
      text: isEditMode ? '${userDetails['prenom']?.toString() ?? ''} ${userDetails['nom']?.toString() ?? ''}'.trim() : '',
    );
    final emailController = TextEditingController(text: isEditMode ? (userDetails['email']?.toString() ?? '') : '');
    final phoneController = TextEditingController(text: isEditMode ? (userDetails['phone']?.toString() ?? '') : '');
    final passwordController = TextEditingController();
    final badgeCodeController = TextEditingController();
    final zoneController = TextEditingController();
    final matriculeController = TextEditingController();
    
    // Charger les informations employé si en mode édition
    if (isEditMode && userDetails['employe'] != null) {
      final employe = userDetails['employe'] as Map<String, dynamic>?;
      if (employe != null) {
        matriculeController.text = employe['matricule']?.toString() ?? '';
        badgeCodeController.text = employe['badgeCode']?.toString() ?? '';
        zoneController.text = employe['zone']?.toString() ?? '';
      }
    }
    
    // S'assurer que selectedRole correspond à une valeur valide dans le dropdown
    final validRoles = ['caissier', 'collector', 'supervisor', 'auditor'];
    final initialRoleCode = roleCode ?? (isEditMode ? (userDetails['roleCode']?.toString() ?? 'collector') : 'collector');
    // Si le rôle n'est pas dans la liste valide (ex: 'client'), utiliser 'collector' par défaut
    String selectedRole = validRoles.contains(initialRoleCode) ? initialRoleCode : 'collector';
    int selectedInstitutionId = 1;
    List<Map<String, dynamic>> institutions = [];
    bool isLoading = false;
    bool loadingInstitutions = true;
    bool isPasswordVisible = false;
    String? passwordError;

    // Charger les institutions et initialiser selectedInstitutionId si en mode édition
    AdminApi.getInstitutions().then((insts) {
      if (mounted) {
        setState(() {
          institutions = insts;
          if (isEditMode) {
            // Essayer de récupérer l'institution depuis les données utilisateur
            final userInstitutionId = userDetails['institutionId'] as int?;
            if (userInstitutionId != null && userInstitutionId > 0 && 
                insts.any((inst) => (inst['id'] as int?) == userInstitutionId)) {
              selectedInstitutionId = userInstitutionId;
            } else if (insts.isNotEmpty) {
              selectedInstitutionId = (insts[0]['id'] as int?) ?? 0;
            }
          } else if (insts.isNotEmpty) {
            selectedInstitutionId = (insts[0]['id'] as int?) ?? 0;
          }
          loadingInstitutions = false;
        });
      }
    }).catchError((error) {
      if (mounted) {
        setState(() {
          loadingInstitutions = false;
        });
      }
    });

    // Validation du mot de passe
    void validatePassword(String password) {
      if (password.length < 6) {
        passwordError = 'Le mot de passe doit contenir au moins 6 caractères';
      } else {
        passwordError = null;
      }
    }

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => StatefulBuilder(
        builder: (context, setState) => Dialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          child: Container(
            constraints: const BoxConstraints(maxWidth: 500, maxHeight: 700),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // En-tête avec icône
                Container(
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    color: Theme.of(context).primaryColor.withOpacity(0.1),
                    borderRadius: const BorderRadius.only(
                      topLeft: Radius.circular(16),
                      topRight: Radius.circular(16),
                    ),
                  ),
                  child: Row(
                    children: [
                      CircleAvatar(
                        backgroundColor: Theme.of(context).primaryColor,
                        child: Icon(
                          isEditMode ? Icons.edit : Icons.person_add,
                          color: Colors.white,
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Text(
                          isEditMode ? 'Modifier l\'Utilisateur' : 'Créer un Utilisateur',
                          style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.close),
                        onPressed: isLoading ? null : () => Navigator.pop(ctx),
                      ),
                    ],
                  ),
                ),
                // Contenu
                Flexible(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Section: Informations personnelles
                        _buildSectionHeader('Informations personnelles', Icons.person),
                        const SizedBox(height: 12),
                        TextField(
                          controller: fullNameController,
                          decoration: InputDecoration(
                            labelText: 'Nom complet',
                            hintText: 'Prénom Nom',
                            prefixIcon: const Icon(Icons.badge),
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                            filled: true,
                            fillColor: Colors.grey[50],
                          ),
                          textCapitalization: TextCapitalization.words,
                        ),
                        const SizedBox(height: 16),
                        TextField(
                          controller: emailController,
                          decoration: InputDecoration(
                            labelText: 'Email',
                            hintText: 'exemple@email.com',
                            prefixIcon: const Icon(Icons.email),
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                            filled: true,
                            fillColor: Colors.grey[50],
                          ),
                          keyboardType: TextInputType.emailAddress,
                          autocorrect: false,
                        ),
                        const SizedBox(height: 16),
                        TextField(
                          controller: phoneController,
                          decoration: InputDecoration(
                            labelText: 'Téléphone',
                            hintText: '+225 XX XX XX XX XX',
                            prefixIcon: const Icon(Icons.phone),
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                            filled: true,
                            fillColor: Colors.grey[50],
                          ),
                          keyboardType: TextInputType.phone,
                        ),
                        const SizedBox(height: 24),
                        
                        // Section: Compte
                        _buildSectionHeader('Compte', Icons.account_circle),
                        const SizedBox(height: 12),
                        TextField(
                          controller: passwordController,
                          decoration: InputDecoration(
                            labelText: isEditMode ? 'Nouveau mot de passe (laisser vide pour ne pas changer)' : 'Mot de passe',
                            hintText: isEditMode ? 'Laisser vide pour conserver le mot de passe actuel' : 'Minimum 6 caractères',
                            prefixIcon: const Icon(Icons.lock),
                            suffixIcon: IconButton(
                              icon: Icon(
                                isPasswordVisible ? Icons.visibility : Icons.visibility_off,
                              ),
                              onPressed: () => setState(() => isPasswordVisible = !isPasswordVisible),
                            ),
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                            filled: true,
                            fillColor: Colors.grey[50],
                            errorText: passwordError,
                          ),
                          obscureText: !isPasswordVisible,
                          onChanged: isEditMode ? null : validatePassword,
                        ),
                        if (passwordController.text.isNotEmpty && passwordError == null)
                          Padding(
                            padding: const EdgeInsets.only(top: 8),
                            child: Row(
                              children: [
                                Icon(Icons.check_circle, color: Colors.green, size: 16),
                                const SizedBox(width: 8),
                                Text(
                                  'Mot de passe valide',
                                  style: TextStyle(color: Colors.green, fontSize: 12),
                                ),
                              ],
                            ),
                          ),
                        const SizedBox(height: 16),
                        DropdownButtonFormField<String>(
                          initialValue: selectedRole,
                          decoration: InputDecoration(
                            labelText: 'Type de compte',
                            prefixIcon: _getRoleIcon(selectedRole),
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                            filled: true,
                            fillColor: Colors.grey[50],
                          ),
                          items: const [
                            DropdownMenuItem(
                              value: 'caissier',
                              child: Row(
                                children: [
                                  Icon(Icons.account_balance_wallet, color: Colors.green, size: 20),
                                  SizedBox(width: 12),
                                  Text('Caissier'),
                                ],
                              ),
                            ),
                            DropdownMenuItem(
                              value: 'collector',
                              child: Row(
                                children: [
                                  Icon(Icons.person_search, color: Colors.orange, size: 20),
                                  SizedBox(width: 12),
                                  Text('Collecteur'),
                                ],
                              ),
                            ),
                            DropdownMenuItem(
                              value: 'supervisor',
                              child: Row(
                                children: [
                                  Icon(Icons.supervisor_account, color: Colors.blue, size: 20),
                                  SizedBox(width: 12),
                                  Text('Superviseur'),
                                ],
                              ),
                            ),
                            DropdownMenuItem(
                              value: 'auditor',
                              child: Row(
                                children: [
                                  Icon(Icons.verified_user, color: Colors.purple, size: 20),
                                  SizedBox(width: 12),
                                  Text('Auditeur'),
                                ],
                              ),
                            ),
                          ],
                          onChanged: (v) => setState(() => selectedRole = v!),
                        ),
                        const SizedBox(height: 16),
                        if (loadingInstitutions)
                          const Center(child: CircularProgressIndicator())
                        else
                          DropdownButtonFormField<int>(
                            initialValue: selectedInstitutionId,
                            decoration: InputDecoration(
                              labelText: 'Agence',
                              prefixIcon: const Icon(Icons.business),
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(12),
                              ),
                              filled: true,
                              fillColor: Colors.grey[50],
                            ),
                            items: institutions.map((inst) => DropdownMenuItem(
                              value: (inst['id'] as int?) ?? 0,
                              child: Text(inst['name']?.toString() ?? ''),
                            )).toList(),
                            onChanged: (v) => setState(() => selectedInstitutionId = v!),
                          ),
                        const SizedBox(height: 24),
                        
                        // Section: Informations spécifiques (conditionnelle)
                        if (selectedRole == 'collector' || selectedRole == 'caissier') ...[
                          _buildSectionHeader(
                            selectedRole == 'collector' ? 'Informations collecteur' : 'Informations caissier',
                            selectedRole == 'collector' ? Icons.assignment_ind : Icons.credit_card,
                          ),
                          const SizedBox(height: 12),
                          if (selectedRole == 'collector') ...[
                            TextField(
                              controller: badgeCodeController,
                              decoration: InputDecoration(
                                labelText: 'Code badge',
                                hintText: 'Identifiant unique du collecteur',
                                prefixIcon: const Icon(Icons.qr_code),
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                filled: true,
                                fillColor: Colors.grey[50],
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextField(
                              controller: zoneController,
                              decoration: InputDecoration(
                                labelText: 'Zone d\'intervention',
                                hintText: 'Zone géographique',
                                prefixIcon: const Icon(Icons.location_on),
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                filled: true,
                                fillColor: Colors.grey[50],
                              ),
                            ),
                          ],
                          if (selectedRole == 'caissier')
                            TextField(
                              controller: matriculeController,
                              decoration: InputDecoration(
                                labelText: 'Matricule',
                                hintText: 'Numéro de matricule',
                                prefixIcon: const Icon(Icons.badge_outlined),
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                filled: true,
                                fillColor: Colors.grey[50],
                              ),
                            ),
                        ],
                      ],
                    ),
                  ),
                ),
                // Actions
                Container(
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    color: Colors.grey[100],
                    borderRadius: const BorderRadius.only(
                      bottomLeft: Radius.circular(16),
                      bottomRight: Radius.circular(16),
                    ),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      TextButton(
                        onPressed: isLoading ? null : () => Navigator.pop(ctx),
                        child: const Text('Annuler'),
                      ),
                      const SizedBox(width: 12),
                      ElevatedButton.icon(
                        onPressed: isLoading ? null : () async {
                          // Validation
                          if (fullNameController.text.trim().isEmpty) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Le nom complet est requis')),
                            );
                            return;
                          }
                          if (emailController.text.trim().isEmpty || !emailController.text.contains('@')) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Un email valide est requis')),
                            );
                            return;
                          }
                          if (phoneController.text.trim().isEmpty) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Le téléphone est requis')),
                            );
                            return;
                          }
                          if (!isEditMode && (passwordController.text.isEmpty || passwordController.text.length < 6)) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Le mot de passe doit contenir au moins 6 caractères')),
                            );
                            return;
                          }
                          
                          if (isEditMode && passwordController.text.isNotEmpty && passwordController.text.length < 6) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Le nouveau mot de passe doit contenir au moins 6 caractères')),
                            );
                            return;
                          }
                          
                          setState(() => isLoading = true);
                          try {
                            if (isEditMode) {
                              final login = userDetails['login']?.toString();
                              if (login == null || login.isEmpty) {
                                throw Exception('Login utilisateur introuvable');
                              }
                              await AdminApi.updateUser(
                                login: login,
                                fullName: fullNameController.text.trim(),
                                email: emailController.text.trim(),
                                phone: phoneController.text.trim(),
                                password: passwordController.text.isEmpty ? null : passwordController.text,
                                roleCode: selectedRole,
                                institutionId: selectedInstitutionId,
                                badgeCode: badgeCodeController.text.trim().isEmpty ? null : badgeCodeController.text.trim(),
                                zone: zoneController.text.trim().isEmpty ? null : zoneController.text.trim(),
                                matricule: matriculeController.text.trim().isEmpty ? null : matriculeController.text.trim(),
                              );
                            } else {
                              await AdminApi.createUser(
                                fullName: fullNameController.text.trim(),
                                email: emailController.text.trim(),
                                phone: phoneController.text.trim(),
                                password: passwordController.text,
                                roleCode: selectedRole,
                                institutionId: selectedInstitutionId,
                                badgeCode: badgeCodeController.text.trim().isEmpty ? null : badgeCodeController.text.trim(),
                                zone: zoneController.text.trim().isEmpty ? null : zoneController.text.trim(),
                                matricule: matriculeController.text.trim().isEmpty ? null : matriculeController.text.trim(),
                              );
                            }
                            if (context.mounted) {
                              Navigator.pop(ctx);
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text(isEditMode ? 'Utilisateur modifié avec succès' : 'Utilisateur créé avec succès'),
                                  backgroundColor: Colors.green,
                                ),
                              );
                              // Rafraîchir la liste des utilisateurs en utilisant le contexte du widget parent
                              // Attendre un court délai pour s'assurer que le dialog est complètement fermé
                              Future.delayed(const Duration(milliseconds: 100), () {
                                if (mounted) {
                                  setState(() {
                                    _usersRefreshKey++;
                                  });
                                }
                              });
                            }
                          } catch (e) {
                            if (context.mounted) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text('Erreur: $e'),
                                  backgroundColor: Colors.red,
                                ),
                              );
                            }
                          } finally {
                            if (mounted) {
                              setState(() => isLoading = false);
                            }
                          }
                        },
                        icon: isLoading
                            ? const SizedBox(
                                width: 20,
                                height: 20,
                                child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                              )
                            : Icon(isEditMode ? Icons.save : Icons.check),
                        label: Text(isEditMode ? 'Enregistrer' : 'Créer'),
                        style: ElevatedButton.styleFrom(
                          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title, IconData icon) {
    return Row(
      children: [
        Icon(icon, color: Theme.of(context).primaryColor, size: 20),
        const SizedBox(width: 8),
        Text(
          title,
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
            color: Theme.of(context).primaryColor,
          ),
        ),
      ],
    );
  }

  void _showEditUserDialog(BuildContext context, Map<String, dynamic> user) async {
    // Charger les détails complets de l'utilisateur
    try {
      final login = user['login']?.toString();
      if (login == null || login.isEmpty) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Login utilisateur introuvable')),
          );
        }
        return;
      }
      final userDetails = await AdminApi.getUserDetails(login);
      final roleCode = userDetails['roleCode']?.toString() ?? '';
      _showCreateUserDialog(context, roleCode, userDetails: userDetails);
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur lors du chargement: $e')),
        );
      }
    }
  }

  void _toggleUserStatus(BuildContext context, Map<String, dynamic> user) async {
    final login = user['login']?.toString();
    if (login == null || login.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Login utilisateur introuvable')),
      );
      return;
    }

    final currentStatut = user['statut']?.toString() ?? 'ACTIF';
    final isActive = currentStatut == 'ACTIF';
    final newStatut = isActive ? 'INACTIF' : 'ACTIF';
    final action = isActive ? 'désactiver' : 'activer';
    final userName = '${user['prenom']?.toString() ?? ''} ${user['nom']?.toString() ?? ''}'.trim();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Row(
          children: [
            Icon(
              isActive ? Icons.block : Icons.check_circle,
              color: isActive ? Colors.orange : Colors.green,
            ),
            const SizedBox(width: 8),
            Text(isActive ? 'Désactiver l\'utilisateur' : 'Activer l\'utilisateur'),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Êtes-vous sûr de vouloir $action cet utilisateur ?'),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(userName.isNotEmpty ? userName : login,
                      style: const TextStyle(fontWeight: FontWeight.bold)),
                  Text(user['email']?.toString() ?? '', style: const TextStyle(fontSize: 12)),
                  Text(user['roleName']?.toString() ?? '', style: const TextStyle(fontSize: 12)),
                ],
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Annuler'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: isActive ? Colors.orange : Colors.green,
              foregroundColor: Colors.white,
            ),
            child: Text(isActive ? 'Désactiver' : 'Activer'),
          ),
        ],
      ),
    );

    if (confirmed == true && context.mounted) {
      try {
        await AdminApi.updateUserStatus(login: login, statut: newStatut);
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Utilisateur ${isActive ? 'désactivé' : 'activé'} avec succès'),
              backgroundColor: Colors.green,
            ),
          );
          // Rafraîchir la liste des utilisateurs
          setState(() {
            _usersRefreshKey++;
          });
        }
      } catch (e) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Erreur: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  void _showDeleteUserConfirmation(BuildContext context, Map<String, dynamic> user) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.warning, color: Colors.red),
            SizedBox(width: 8),
            Text('Confirmer la suppression'),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Êtes-vous sûr de vouloir supprimer cet utilisateur ?'),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('${user['prenom']?.toString() ?? ''} ${user['nom']?.toString() ?? ''}',
                      style: const TextStyle(fontWeight: FontWeight.bold)),
                  Text(user['email']?.toString() ?? '', style: const TextStyle(fontSize: 12)),
                  Text(user['roleName']?.toString() ?? '', style: const TextStyle(fontSize: 12)),
                ],
              ),
            ),
            const SizedBox(height: 8),
            const Text(
              'Cette action est irréversible.',
              style: TextStyle(color: Colors.red, fontSize: 12),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Annuler'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              try {
                final login = user['login'] as String?;
                if (login == null) {
                  throw Exception('Login utilisateur introuvable');
                }
                await AdminApi.deleteUser(login);
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Utilisateur supprimé avec succès'),
                      backgroundColor: Colors.green,
                    ),
                  );
                  setState(() {
                    _usersRefreshKey++;
                  });
                }
              } catch (e) {
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Erreur: $e'),
                      backgroundColor: Colors.red,
                    ),
                  );
                }
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Supprimer', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  // ========== ONGLET TYPES DE COMPTES ==========
  Widget _buildTypeComptesTab(BuildContext context) {
    return RefreshIndicator(
      onRefresh: () async {
        setState(() {
          _typeComptesRefreshKey++;
        });
      },
      child: FutureBuilder<List<TypeCompteModel>>(
        key: ValueKey('type_comptes_$_typeComptesRefreshKey'),
        future: TypeCompteApi.getAll(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.error_outline, size: 64, color: Colors.red[300]),
                  const SizedBox(height: 16),
                  Text(
                    'Erreur de chargement',
                    style: TextStyle(fontSize: 18, color: Colors.grey[700]),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    snapshot.error.toString(),
                    style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton.icon(
                    onPressed: () {
                      setState(() {
                        _typeComptesRefreshKey++;
                      });
                    },
                    icon: const Icon(Icons.refresh),
                    label: const Text('Réessayer'),
                  ),
                ],
              ),
            );
          }

          final typesComptes = snapshot.data ?? [];

          return Column(
            children: [
              // En-tête avec bouton d'ajout
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.05),
                      blurRadius: 4,
                      offset: const Offset(0, 2),
                    ),
                  ],
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Types de Comptes',
                          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                                fontWeight: FontWeight.bold,
                              ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          '${typesComptes.length} type${typesComptes.length > 1 ? 's' : ''} disponible${typesComptes.length > 1 ? 's' : ''}',
                          style: TextStyle(color: Colors.grey[600], fontSize: 14),
                        ),
                      ],
                    ),
                    ElevatedButton.icon(
                      onPressed: () => _showCreateTypeCompteDialog(context),
                      icon: const Icon(Icons.add),
                      label: const Text('Ajouter'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Theme.of(context).colorScheme.primary,
                        foregroundColor: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),
              // Liste des types de comptes
              Expanded(
                child: typesComptes.isEmpty
                    ? Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(Icons.account_balance_outlined,
                                size: 64, color: Colors.grey[400]),
                            const SizedBox(height: 16),
                            Text(
                              'Aucun type de compte',
                              style: TextStyle(
                                fontSize: 18,
                                color: Colors.grey[600],
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'Cliquez sur "Ajouter" pour créer un type de compte',
                              style: TextStyle(color: Colors.grey[500], fontSize: 14),
                            ),
                          ],
                        ),
                      )
                    : ListView.builder(
                        padding: const EdgeInsets.all(16),
                        itemCount: typesComptes.length,
                        itemBuilder: (context, index) {
                          final typeCompte = typesComptes[index];
                          return Card(
                            margin: const EdgeInsets.only(bottom: 12),
                            elevation: 2,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: ListTile(
                              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                              leading: Container(
                                padding: const EdgeInsets.all(10),
                                decoration: BoxDecoration(
                                  color: Theme.of(context).colorScheme.primary.withOpacity(0.1),
                                  borderRadius: BorderRadius.circular(10),
                                ),
                                child: Icon(
                                  Icons.account_balance,
                                  color: Theme.of(context).colorScheme.primary,
                                  size: 24,
                                ),
                              ),
                              title: Text(
                                typeCompte.nom,
                                style: const TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: 15,
                                ),
                              ),
                              subtitle: Padding(
                                padding: const EdgeInsets.only(top: 4),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    Text(
                                      'Code: ${typeCompte.code}',
                                      style: TextStyle(
                                        color: Colors.grey[600],
                                        fontSize: 12,
                                      ),
                                    ),
                                    if (typeCompte.description != null &&
                                        typeCompte.description!.isNotEmpty) ...[
                                      const SizedBox(height: 3),
                                      Text(
                                        typeCompte.description!,
                                        style: TextStyle(
                                          color: Colors.grey[600],
                                          fontSize: 11,
                                        ),
                                        maxLines: 1,
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                    ],
                                    const SizedBox(height: 6),
                                    Wrap(
                                      spacing: 6,
                                      runSpacing: 4,
                                      children: [
                                        if (typeCompte.tauxInteret != null)
                                          Chip(
                                            label: Text(
                                              'Taux: ${typeCompte.tauxInteret}%',
                                              style: const TextStyle(fontSize: 10),
                                            ),
                                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                            materialTapTargetSize:
                                                MaterialTapTargetSize.shrinkWrap,
                                            visualDensity: VisualDensity.compact,
                                          ),
                                        if (typeCompte.soldeMinimum != null)
                                          Chip(
                                            label: Text(
                                              'Min: ${typeCompte.soldeMinimum}',
                                              style: const TextStyle(fontSize: 10),
                                            ),
                                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                            materialTapTargetSize:
                                                MaterialTapTargetSize.shrinkWrap,
                                            visualDensity: VisualDensity.compact,
                                          ),
                                        if (typeCompte.autoriserRetrait == true)
                                          Chip(
                                            label: const Text(
                                              'Retrait OK',
                                              style: TextStyle(fontSize: 10),
                                            ),
                                            backgroundColor: Colors.green[100],
                                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                            materialTapTargetSize:
                                                MaterialTapTargetSize.shrinkWrap,
                                            visualDensity: VisualDensity.compact,
                                          ),
                                      ],
                                    ),
                                  ],
                                ),
                              ),
                              trailing: PopupMenuButton<String>(
                                icon: const Icon(Icons.more_vert),
                                onSelected: (value) {
                                  if (value == 'edit') {
                                    _showCreateTypeCompteDialog(context, typeCompte: typeCompte);
                                  } else if (value == 'delete') {
                                    _showDeleteTypeCompteConfirmation(context, typeCompte);
                                  }
                                },
                                itemBuilder: (context) => [
                                  const PopupMenuItem(
                                    value: 'edit',
                                    child: Row(
                                      children: [
                                        Icon(Icons.edit, size: 20),
                                        SizedBox(width: 8),
                                        Text('Modifier'),
                                      ],
                                    ),
                                  ),
                                  const PopupMenuItem(
                                    value: 'delete',
                                    child: Row(
                                      children: [
                                        Icon(Icons.delete, size: 20, color: Colors.red),
                                        SizedBox(width: 8),
                                        Text('Supprimer', style: TextStyle(color: Colors.red)),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          );
                        },
                      ),
              ),
            ],
          );
        },
      ),
    );
  }

  void _showCreateTypeCompteDialog(BuildContext context, {TypeCompteModel? typeCompte}) {
    final isEditing = typeCompte != null;
    final formKey = GlobalKey<FormState>();
    final codeController = TextEditingController(text: typeCompte?.code ?? '');
    final nomController = TextEditingController(text: typeCompte?.nom ?? '');
    final descriptionController = TextEditingController(text: typeCompte?.description ?? '');
    final tauxInteretController = TextEditingController(
        text: typeCompte?.tauxInteret != null ? typeCompte!.tauxInteret.toString() : '');
    final soldeMinimumController = TextEditingController(
        text: typeCompte?.soldeMinimum != null ? typeCompte!.soldeMinimum.toString() : '');
    final fraisOuvertureController = TextEditingController(
        text: typeCompte?.fraisOuverture != null ? typeCompte!.fraisOuverture.toString() : '');
    final fraisClotureController = TextEditingController(
        text: typeCompte?.fraisCloture != null ? typeCompte!.fraisCloture.toString() : '');
    final dureeBlocageController = TextEditingController(
        text: typeCompte?.dureeBlocageJours != null
            ? typeCompte!.dureeBlocageJours.toString()
            : '');
    bool autoriserRetrait = typeCompte?.autoriserRetrait ?? true;
    bool isSubmitting = false;
    final controllersDisposed = <bool>[false];

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setDialogState) {
          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
            title: Row(
              children: [
                Icon(
                  isEditing ? Icons.edit : Icons.add_circle_outline,
                  color: Theme.of(context).colorScheme.primary,
                ),
                const SizedBox(width: 12),
                Flexible(
                  child: Text(isEditing ? 'Modifier le type de compte' : 'Nouveau type de compte'),
                ),
              ],
            ),
            content: SingleChildScrollView(
              child: Form(
                key: formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    TextFormField(
                      controller: codeController,
                      decoration: InputDecoration(
                        labelText: 'Code *',
                        hintText: 'EPARGNE',
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                        prefixIcon: const Icon(Icons.code),
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'Le code est requis';
                        }
                        if (value.length > 20) {
                          return 'Le code ne doit pas dépasser 20 caractères';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: nomController,
                      decoration: InputDecoration(
                        labelText: 'Nom *',
                        hintText: 'Compte d\'épargne',
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                        prefixIcon: const Icon(Icons.label),
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'Le nom est requis';
                        }
                        if (value.length > 50) {
                          return 'Le nom ne doit pas dépasser 50 caractères';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: descriptionController,
                      maxLines: 3,
                      decoration: InputDecoration(
                        labelText: 'Description (optionnel)',
                        hintText: 'Description du type de compte...',
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                        prefixIcon: const Icon(Icons.description),
                      ),
                    ),
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        Expanded(
                          child: TextFormField(
                            controller: tauxInteretController,
                            keyboardType: TextInputType.number,
                            decoration: InputDecoration(
                              labelText: 'Taux d\'intérêt (%)',
                              hintText: '0.0',
                              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                              prefixIcon: const Icon(Icons.percent),
                            ),
                            validator: (value) {
                              if (value != null && value.isNotEmpty) {
                                final taux = double.tryParse(value);
                                if (taux == null || taux < 0) {
                                  return 'Taux invalide';
                                }
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: TextFormField(
                            controller: soldeMinimumController,
                            keyboardType: TextInputType.number,
                            decoration: InputDecoration(
                              labelText: 'Solde minimum (FCFA)',
                              hintText: '0',
                              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                              prefixIcon: const Icon(Icons.attach_money),
                            ),
                            validator: (value) {
                              if (value != null && value.isNotEmpty) {
                                final solde = double.tryParse(value);
                                if (solde == null || solde < 0) {
                                  return 'Solde invalide';
                                }
                              }
                              return null;
                            },
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        Expanded(
                          child: TextFormField(
                            controller: fraisOuvertureController,
                            keyboardType: TextInputType.number,
                            decoration: InputDecoration(
                              labelText: 'Frais d\'ouverture (FCFA)',
                              hintText: '0',
                              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                              prefixIcon: const Icon(Icons.payment),
                            ),
                            validator: (value) {
                              if (value != null && value.isNotEmpty) {
                                final frais = double.tryParse(value);
                                if (frais == null || frais < 0) {
                                  return 'Frais invalides';
                                }
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: TextFormField(
                            controller: fraisClotureController,
                            keyboardType: TextInputType.number,
                            decoration: InputDecoration(
                              labelText: 'Frais de clôture (FCFA)',
                              hintText: '0',
                              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                              prefixIcon: const Icon(Icons.close),
                            ),
                            validator: (value) {
                              if (value != null && value.isNotEmpty) {
                                final frais = double.tryParse(value);
                                if (frais == null || frais < 0) {
                                  return 'Frais invalides';
                                }
                              }
                              return null;
                            },
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        Expanded(
                          child: TextFormField(
                            controller: dureeBlocageController,
                            keyboardType: TextInputType.number,
                            decoration: InputDecoration(
                              labelText: 'Durée de blocage (jours)',
                              hintText: '0',
                              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                              prefixIcon: const Icon(Icons.lock_clock),
                            ),
                            validator: (value) {
                              if (value != null && value.isNotEmpty) {
                                final duree = int.tryParse(value);
                                if (duree == null || duree < 0) {
                                  return 'Durée invalide';
                                }
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: CheckboxListTile(
                            title: const Text('Autoriser retrait'),
                            value: autoriserRetrait,
                            onChanged: (value) {
                              setDialogState(() {
                                autoriserRetrait = value ?? true;
                              });
                            },
                            contentPadding: EdgeInsets.zero,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            actions: [
              TextButton(
                onPressed: isSubmitting ? null : () => Navigator.pop(context),
                child: const Text('Annuler'),
              ),
              ElevatedButton(
                onPressed: isSubmitting
                    ? null
                    : () async {
                        if (formKey.currentState?.validate() ?? false) {
                          setDialogState(() {
                            isSubmitting = true;
                          });

                          try {
                            if (isEditing) {
                              await TypeCompteApi.updateTypeCompte(
                                id: typeCompte.id,
                                code: codeController.text.trim(),
                                nom: nomController.text.trim(),
                                description: descriptionController.text.trim().isEmpty
                                    ? null
                                    : descriptionController.text.trim(),
                                tauxInteret: tauxInteretController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(tauxInteretController.text.trim()),
                                soldeMinimum: soldeMinimumController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(soldeMinimumController.text.trim()),
                                fraisOuverture: fraisOuvertureController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(fraisOuvertureController.text.trim()),
                                fraisCloture: fraisClotureController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(fraisClotureController.text.trim()),
                                autoriserRetrait: autoriserRetrait,
                                dureeBlocageJours: dureeBlocageController.text.trim().isEmpty
                                    ? null
                                    : int.tryParse(dureeBlocageController.text.trim()),
                              );
                            } else {
                              await TypeCompteApi.createTypeCompte(
                                code: codeController.text.trim(),
                                nom: nomController.text.trim(),
                                description: descriptionController.text.trim().isEmpty
                                    ? null
                                    : descriptionController.text.trim(),
                                tauxInteret: tauxInteretController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(tauxInteretController.text.trim()),
                                soldeMinimum: soldeMinimumController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(soldeMinimumController.text.trim()),
                                fraisOuverture: fraisOuvertureController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(fraisOuvertureController.text.trim()),
                                fraisCloture: fraisClotureController.text.trim().isEmpty
                                    ? null
                                    : double.tryParse(fraisClotureController.text.trim()),
                                autoriserRetrait: autoriserRetrait,
                                dureeBlocageJours: dureeBlocageController.text.trim().isEmpty
                                    ? null
                                    : int.tryParse(dureeBlocageController.text.trim()),
                              );
                            }

                            if (!context.mounted) return;

                            Navigator.pop(context);

                            // Attendre que le dialog soit complètement fermé avant de mettre à jour l'état
                            WidgetsBinding.instance.addPostFrameCallback((_) {
                              if (mounted) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(
                                    content: Text(
                                      isEditing
                                          ? 'Type de compte modifié avec succès'
                                          : 'Type de compte créé avec succès',
                                    ),
                                    backgroundColor: Colors.green,
                                  ),
                                );

                                setState(() {
                                  _typeComptesRefreshKey++;
                                });
                              }
                            });
                          } catch (e) {
                            setDialogState(() {
                              isSubmitting = false;
                            });

                            if (context.mounted) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text('Erreur: $e'),
                                  backgroundColor: Colors.red,
                                ),
                              );
                            }
                          }
                        }
                      },
                style: ElevatedButton.styleFrom(
                  backgroundColor: Theme.of(context).colorScheme.primary,
                  foregroundColor: Colors.white,
                ),
                child: isSubmitting
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : Text(isEditing ? 'Modifier' : 'Créer'),
              ),
            ],
          );
        },
      ),
    ).then((_) {
      // S'assurer que les contrôleurs sont disposés même si le dialog est fermé autrement
      if (!controllersDisposed[0]) {
        controllersDisposed[0] = true;
        try {
          codeController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
        try {
          nomController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
        try {
          descriptionController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
        try {
          tauxInteretController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
        try {
          soldeMinimumController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
        try {
          fraisOuvertureController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
        try {
          fraisClotureController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
        try {
          dureeBlocageController.dispose();
        } catch (e) {
          // Ignorer si déjà disposé
        }
      }
    });
  }

  void _showDeleteTypeCompteConfirmation(BuildContext context, TypeCompteModel typeCompte) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: const Row(
          children: [
            Icon(Icons.warning, color: Colors.orange),
            SizedBox(width: 12),
            Flexible(child: Text('Confirmer la suppression')),
          ],
        ),
        content: Text(
          'Êtes-vous sûr de vouloir supprimer le type de compte "${typeCompte.nom}" ?\n\nCette action est irréversible.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Annuler'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              try {
                await TypeCompteApi.deleteTypeCompte(typeCompte.id);
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Type de compte supprimé avec succès'),
                      backgroundColor: Colors.green,
                    ),
                  );
                  setState(() {
                    _typeComptesRefreshKey++;
                  });
                }
              } catch (e) {
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Erreur: $e'),
                      backgroundColor: Colors.red,
                    ),
                  );
                }
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Supprimer', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }
}
