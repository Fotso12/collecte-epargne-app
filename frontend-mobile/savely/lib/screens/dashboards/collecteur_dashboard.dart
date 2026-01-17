import 'package:flutter/material.dart';
import '../../models/user_model.dart';
import '../../services/employe_api.dart';
import '../../services/transaction_offline_api.dart';
import '../nouvelle_collecte_screen.dart';
import '../clients_list_screen.dart';
import '../historique_collectes_screen.dart';
import 'collecteur_profile_screen.dart';
import 'package:intl/intl.dart';

class CollecteurDashboard extends StatefulWidget {
  final UserModel user;

  const CollecteurDashboard({super.key, required this.user});

  @override
  State<CollecteurDashboard> createState() => _CollecteurDashboardState();
}

class _CollecteurDashboardState extends State<CollecteurDashboard> {
  int _clientsAssignes = 0;
  double _collecteAujourdhui = 0;
  int _collectesMois = 0;
  double _commission = 0;
  bool _isLoading = true;
  String? _idEmploye;
  List<Map<String, dynamic>> _todayTransactions = [];
  List<Map<String, dynamic>> _recentClients = [];

  @override
  void initState() {
    super.initState();
    _loadDashboardData();
  }

  Future<void> _loadDashboardData() async {
    setState(() => _isLoading = true);
    try {
      // Réinitialiser les données
      _clientsAssignes = 0;
      _collecteAujourdhui = 0;
      _collectesMois = 0;
      _commission = 0;
      _todayTransactions = [];
      _recentClients = [];

      // Obtenir l'ID_EMPLOYE du collecteur (le backend attend l'ID, pas le matricule)
      try {
        _idEmploye = await EmployeApi.getIdEmployeByLogin(widget.user.login);
      } catch (e) {
        // Si l'erreur survient, on continue avec des données vides
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Impossible de charger les données: $e'),
              backgroundColor: Colors.orange,
            ),
          );
        }
        return;
      }
      
      if (_idEmploye == null || _idEmploye!.isEmpty) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Aucun ID trouvé pour ce collecteur. Vérifiez que vous êtes bien un collecteur.'),
              backgroundColor: Colors.orange,
            ),
          );
        }
        return;
      }

      // Charger les clients assignés
      try {
        final clients = await EmployeApi.getClientsByCollecteur(_idEmploye!);
        _clientsAssignes = clients.length;
        _recentClients = clients.take(3).toList();
      } catch (e) {
        // Continuer même si on ne peut pas charger les clients
        _clientsAssignes = 0;
        _recentClients = [];
      }

      // Charger les transactions du jour
      try {
        _todayTransactions = await TransactionOfflineApi.getTodayTransactions(_idEmploye!);
        _collecteAujourdhui = _todayTransactions.fold<double>(
          0.0,
          (sum, t) => sum + ((t['montant'] as num?)?.toDouble() ?? 0.0),
        );
      } catch (e) {
        // Si l'endpoint n'existe pas encore, utiliser toutes les transactions
        try {
          final allTransactions = await TransactionOfflineApi.getTransactionsByCollecteur(_idEmploye!);
          final now = DateTime.now();
          _todayTransactions = allTransactions.where((t) {
            try {
              final dateStr = t['dateTransaction']?.toString();
              if (dateStr == null || dateStr.isEmpty) return false;
              final date = DateTime.parse(dateStr);
              return date.year == now.year && date.month == now.month && date.day == now.day;
            } catch (e) {
              return false;
            }
          }).toList();
          _collecteAujourdhui = _todayTransactions.fold<double>(
            0.0,
            (sum, t) => sum + ((t['montant'] as num?)?.toDouble() ?? 0.0),
          );
        } catch (e2) {
          // Si même ça échoue, on garde des listes vides
          _todayTransactions = [];
          _collecteAujourdhui = 0;
        }
      }

      // Charger toutes les transactions pour calculer les stats du mois
      try {
        final allTransactions = await TransactionOfflineApi.getTransactionsByCollecteur(_idEmploye!);
        final now = DateTime.now();
        final monthTransactions = allTransactions.where((t) {
          try {
            final dateStr = t['dateTransaction']?.toString();
            if (dateStr == null || dateStr.isEmpty) return false;
            final date = DateTime.parse(dateStr);
            return date.year == now.year && date.month == now.month;
          } catch (e) {
            return false;
          }
        }).toList();
        _collectesMois = monthTransactions.length;
      } catch (e) {
        _collectesMois = 0;
      }

      // Commission (5% par défaut, à adapter selon vos règles)
      _commission = _collecteAujourdhui * 0.05;
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Erreur lors du chargement: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  String _formatCurrency(double amount) {
    return '${amount.toStringAsFixed(0).replaceAllMapped(
      RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
      (Match m) => '${m[1]},',
    )} FCFA';
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    
    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        elevation: 0,
        backgroundColor: colorScheme.primary,
        foregroundColor: Colors.white,
        title: Row(
          children: [
            Icon(Icons.hiking, size: 24),
            const SizedBox(width: 8),
            const Text('Collecteur'),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadDashboardData,
            tooltip: 'Actualiser',
          ),
          PopupMenuButton<String>(
            icon: const Icon(Icons.more_vert),
            onSelected: (value) {
              switch (value) {
                case 'profile':
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => CollecteurProfileScreen(user: widget.user),
                    ),
                  ).then((updated) {
                    if (updated == true) {
                      _loadDashboardData();
                    }
                  });
                  break;
                case 'logout':
                  Navigator.pushReplacementNamed(context, '/');
                  break;
              }
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'profile',
                child: Row(
                  children: [
                    Icon(Icons.person, size: 20),
                    SizedBox(width: 8),
                    Text('Profil'),
                  ],
                ),
              ),
              const PopupMenuDivider(),
              const PopupMenuItem(
                value: 'logout',
                child: Row(
                  children: [
                    Icon(Icons.logout, size: 20),
                    SizedBox(width: 8),
                    Text('Déconnexion'),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadDashboardData,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Carte de bienvenue avec gradient
              _buildWelcomeCard(context, colorScheme),
              const SizedBox(height: 20),
              
              // Grille de statistiques
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: _buildStatsGrid(context),
              ),
              const SizedBox(height: 20),
              
              // Actions rapides
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: _buildQuickActions(context, colorScheme),
              ),
              const SizedBox(height: 20),
              
              // Section collectes du jour
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: _buildTodayCollections(context),
              ),
              const SizedBox(height: 20),
              
              // Section clients assignés
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: _buildAssignedClients(context),
              ),
              const SizedBox(height: 80), // Espace pour le FAB
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(
          builder: (context) => NouvelleCollecteScreen(
            matriculeCollecteur: widget.user.login,
          ),
            ),
          );
          if (result == true) {
            _loadDashboardData();
          }
        },
        icon: const Icon(Icons.add_circle),
        label: const Text('Nouvelle Collecte'),
        backgroundColor: colorScheme.primary,
        foregroundColor: Colors.white,
        elevation: 4,
      ),
    );
  }

  Widget _buildWelcomeCard(BuildContext context, ColorScheme colorScheme) {
    final prenom = widget.user.prenom;
    final nom = widget.user.nom;
    final initiales = '${prenom.isNotEmpty ? prenom[0] : ''}${nom.isNotEmpty ? nom[0] : ''}';
    final displayName = prenom.isNotEmpty ? prenom : (nom.isNotEmpty ? nom : 'Collecteur');
    
    return Container(
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            colorScheme.primary,
            colorScheme.primary.withOpacity(0.8),
            colorScheme.secondary,
          ],
        ),
      ),
      child: SafeArea(
        bottom: false,
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    width: 70,
                    height: 70,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      border: Border.all(color: Colors.white, width: 3),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.2),
                          blurRadius: 8,
                          offset: const Offset(0, 2),
                        ),
                      ],
                    ),
                    child: CircleAvatar(
                      radius: 32,
                      backgroundColor: Colors.white,
                      child: Text(
                        initiales.isNotEmpty ? initiales.toUpperCase() : 'C',
                        style: TextStyle(
                          fontSize: 24,
                          color: colorScheme.primary,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Bonjour,',
                          style: TextStyle(
                            color: Colors.white.withOpacity(0.9),
                            fontSize: 14,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          displayName,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 22,
                            fontWeight: FontWeight.bold,
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ],
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(color: Colors.white.withOpacity(0.3)),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: const [
                        Icon(Icons.verified, color: Colors.white, size: 16),
                        SizedBox(width: 4),
                        Text(
                          'Agent',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 12,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: Colors.white.withOpacity(0.2)),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Total Collecté',
                            style: TextStyle(
                              color: Colors.white.withOpacity(0.9),
                              fontSize: 12,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            _formatCurrency(_collecteAujourdhui),
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ],
                      ),
                    ),
                    Container(
                      width: 1,
                      height: 40,
                      color: Colors.white.withOpacity(0.3),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Clients',
                            style: TextStyle(
                              color: Colors.white.withOpacity(0.9),
                              fontSize: 12,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            '$_clientsAssignes',
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatsGrid(BuildContext context) {
    if (_isLoading) {
      return const SizedBox(
        height: 200,
        child: Center(child: CircularProgressIndicator()),
      );
    }

    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisSpacing: 12,
      mainAxisSpacing: 12,
      childAspectRatio: 1.5,
      children: [
        _buildStatCard(
          context,
          'Clients Assignés',
          '$_clientsAssignes',
          Icons.people_outline,
          Colors.blue,
          subtitle: 'Clients actifs',
        ),
        _buildStatCard(
          context,
          'Collecte Aujourd\'hui',
          _formatCurrency(_collecteAujourdhui),
          Icons.account_balance_wallet,
          Colors.green,
          subtitle: 'Total du jour',
        ),
        _buildStatCard(
          context,
          'Collectes du Mois',
          '$_collectesMois',
          Icons.trending_up,
          Colors.orange,
          subtitle: 'Transactions',
        ),
        _buildStatCard(
          context,
          'Commission',
          _formatCurrency(_commission),
          Icons.monetization_on,
          Colors.purple,
          subtitle: 'Revenus estimés',
        ),
      ],
    );
  }

  Widget _buildStatCard(
    BuildContext context,
    String title,
    String value,
    IconData icon,
    Color color, {
    String? subtitle,
  }) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              color.withOpacity(0.1),
              color.withOpacity(0.05),
            ],
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: color.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Icon(icon, size: 22, color: color),
                  ),
                  if (subtitle != null)
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: color.withOpacity(0.15),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        subtitle,
                        style: TextStyle(
                          color: color,
                          fontSize: 9,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                ],
              ),
              const SizedBox(height: 12),
              Text(
                value,
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: color,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 4),
              Text(
                title,
                style: TextStyle(
                  fontSize: 11,
                  color: Colors.grey[700],
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildQuickActions(BuildContext context, ColorScheme colorScheme) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.flash_on, color: colorScheme.primary, size: 20),
                const SizedBox(width: 8),
                Text(
                  'Actions Rapides',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: Colors.grey[800],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _buildActionButton(
                    context,
                    'Nouvelle\nCollecte',
                    Icons.add_circle_outline,
                    Colors.green,
                    () async {
                      final result = await Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => NouvelleCollecteScreen(
                            matriculeCollecteur: widget.user.login,
                          ),
                        ),
                      );
                      if (result == true) {
                        _loadDashboardData();
                      }
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildActionButton(
                    context,
                    'Mes\nClients',
                    Icons.people_outline,
                    Colors.blue,
                    () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => ClientsListScreen(
                            matriculeCollecteur: widget.user.login,
                          ),
                        ),
                      );
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildActionButton(
                    context,
                    'Historique',
                    Icons.history,
                    Colors.orange,
                    () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => HistoriqueCollectesScreen(
                            loginCollecteur: widget.user.login,
                            idEmploye: _idEmploye,
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActionButton(
    BuildContext context,
    String label,
    IconData icon,
    Color color,
    VoidCallback onTap,
  ) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 8),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: color.withOpacity(0.3)),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, color: color, size: 28),
            const SizedBox(height: 8),
            Text(
              label,
              textAlign: TextAlign.center,
              style: TextStyle(
                color: color,
                fontSize: 12,
                fontWeight: FontWeight.w600,
              ),
              maxLines: 2,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTodayCollections(BuildContext context) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    Icon(Icons.today, color: Theme.of(context).colorScheme.primary, size: 20),
                    const SizedBox(width: 8),
                    Text(
                      'Collectes du Jour',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Colors.grey[800],
                      ),
                    ),
                  ],
                ),
                TextButton.icon(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => HistoriqueCollectesScreen(
                          loginCollecteur: widget.user.login,
                          idEmploye: _idEmploye,
                        ),
                      ),
                    );
                  },
                  icon: const Icon(Icons.arrow_forward, size: 16),
                  label: const Text('Voir tout'),
                  style: TextButton.styleFrom(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            _todayTransactions.isEmpty
                ? Container(
                    padding: const EdgeInsets.all(32),
                    decoration: BoxDecoration(
                      color: Colors.grey[50],
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: Colors.grey[200]!),
                    ),
                    child: Column(
                      children: [
                        Icon(Icons.inbox_outlined, size: 48, color: Colors.grey[400]),
                        const SizedBox(height: 12),
                        Text(
                          'Aucune collecte aujourd\'hui',
                          style: TextStyle(
                            color: Colors.grey[600],
                            fontSize: 14,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          'Appuyez sur "Nouvelle Collecte" pour commencer',
                          style: TextStyle(
                            color: Colors.grey[500],
                            fontSize: 12,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ],
                    ),
                  )
                : Column(
                    mainAxisSize: MainAxisSize.min,
                    children: _todayTransactions.take(3).map((transaction) {
                      final type = transaction['typeTransaction']?.toString() ?? 'DEPOT';
                      final montant = (transaction['montant'] as num?)?.toDouble() ?? 0.0;
                      final dateStr = transaction['dateTransaction']?.toString();
                      
                      String formattedDate = 'N/A';
                      try {
                        if (dateStr != null) {
                          final date = DateTime.parse(dateStr);
                          formattedDate = DateFormat('HH:mm').format(date);
                        }
                      } catch (e) {
                        // Ignore
                      }

                      return ListTile(
                        contentPadding: EdgeInsets.zero,
                        leading: CircleAvatar(
                          backgroundColor: type == 'DEPOT' ? Colors.green[100] : Colors.red[100],
                          child: Icon(
                            type == 'DEPOT' ? Icons.arrow_downward : Icons.arrow_upward,
                            color: type == 'DEPOT' ? Colors.green[700] : Colors.red[700],
                          ),
                        ),
                        title: Text(
                          type,
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                        subtitle: Text(formattedDate),
                        trailing: Text(
                          _formatCurrency(montant),
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: type == 'DEPOT' ? Colors.green[700] : Colors.red[700],
                          ),
                        ),
                      );
                    }).toList(),
                  ),
          ],
        ),
      ),
    );
  }

  Widget _buildAssignedClients(BuildContext context) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    Icon(Icons.people, color: Theme.of(context).colorScheme.primary, size: 20),
                    const SizedBox(width: 8),
                    Text(
                      'Clients Assignés',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Colors.grey[800],
                      ),
                    ),
                  ],
                ),
                TextButton.icon(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                      builder: (context) => ClientsListScreen(
                        matriculeCollecteur: widget.user.login,
                      ),
                      ),
                    );
                  },
                  icon: const Icon(Icons.arrow_forward, size: 16),
                  label: const Text('Voir tout'),
                  style: TextButton.styleFrom(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (_clientsAssignes == 0)
              Container(
                padding: const EdgeInsets.all(32),
                decoration: BoxDecoration(
                  color: Colors.grey[50],
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: Colors.grey[200]!),
                ),
                child: Column(
                  children: [
                    Icon(Icons.people_outline, size: 48, color: Colors.grey[400]),
                    const SizedBox(height: 12),
                    Text(
                      'Aucun client assigné',
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontSize: 14,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Contactez votre superviseur pour obtenir des clients',
                      style: TextStyle(
                        color: Colors.grey[500],
                        fontSize: 12,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              )
            else
              Column(
                mainAxisSize: MainAxisSize.min,
                children: _recentClients.take(3).map((client) {
                  final clientPrenom = client['prenom']?.toString() ?? '';
                  final clientNom = client['nom']?.toString() ?? '';
                  final nom = '$clientPrenom $clientNom'.trim();
                  final code = client['codeClient']?.toString() ?? 'N/A';
                  final initiales = nom.isNotEmpty
                      ? nom[0].toUpperCase()
                      : (code.isNotEmpty && code != 'N/A'
                          ? code[0].toUpperCase()
                          : 'C');

                  return ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: CircleAvatar(
                      backgroundColor: Colors.blue[100],
                      child: Text(
                        initiales,
                        style: TextStyle(
                          color: Colors.blue[700],
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    title: Text(nom.isNotEmpty ? nom : code),
                    subtitle: Text('Code: $code'),
                    trailing: Icon(Icons.chevron_right, color: Colors.grey[400]),
                    onTap: () {
                      // TODO: Naviguer vers les détails du client
                    },
                  );
                }).toList(),
              ),
          ],
        ),
      ),
    );
  }
}
