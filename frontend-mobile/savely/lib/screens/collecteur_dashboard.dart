import 'package:flutter/material.dart';
import '../models/collecteur_model.dart';
import '../models/transaction_model.dart';
import '../services/collecteur_api.dart';
import '../services/auth_api.dart';
import '../services/error_handler.dart';
import 'login_screen.dart';

class CollecteurDashboard extends StatefulWidget {
  const CollecteurDashboard({Key? key}) : super(key: key);

  @override
  State<CollecteurDashboard> createState() => _CollecteurDashboardState();
}

class _CollecteurDashboardState extends State<CollecteurDashboard> {
  CollecteurModel? _collecteur;
  List<TransactionModel> _transactions = [];
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadDashboard();
  }

  Future<void> _loadDashboard() async {
    try {
      setState(() {
        _isLoading = true;
        _errorMessage = null;
      });

      // Récupérer l'ID du collecteur depuis l'utilisateur connecté
      final user = AuthApi.currentUser;
      if (user == null) {
        throw Exception('Utilisateur non connecté');
      }
      final collecteurId = user.login;

      // Charger le profil
      final collecteur = await CollecteurApi.getProfile(collecteurId);

      // Charger les transactions
      final transactions = await CollecteurApi.getTransactions(collecteurId);

      if (mounted) {
        setState(() {
          _collecteur = collecteur;
          _transactions = transactions;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _errorMessage = ErrorHandler.getDisplayMessage(e.toString());
          _isLoading = false;
        });
      }
    }
  }

  Future<void> _handleLogout() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Se déconnecter'),
        content: const Text('Êtes-vous sûr de vouloir vous déconnecter ?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Annuler'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Déconnecter'),
          ),
        ],
      ),
    );

    if (confirmed ?? false) {
      AuthApi.logout();
      if (mounted) {
        Navigator.of(context).pushReplacementNamed('/login');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mon Tableau de Bord'),
        backgroundColor: const Color(0xFF0D8A5F),
        foregroundColor: Colors.white,
        actions: [
          IconButton(icon: const Icon(Icons.logout), onPressed: _handleLogout),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _errorMessage != null
          ? _buildErrorWidget()
          : _buildDashboardContent(),
    );
  }

  Widget _buildErrorWidget() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 64, color: Colors.red),
          const SizedBox(height: 16),
          Text(
            _errorMessage ?? 'Une erreur est survenue',
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 16),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: _loadDashboard,
            child: const Text('Réessayer'),
          ),
        ],
      ),
    );
  }

  Widget _buildDashboardContent() {
    return RefreshIndicator(
      onRefresh: _loadDashboard,
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // En-tête avec profil
            if (_collecteur != null) _buildProfileHeader(),
            const SizedBox(height: 24),

            // KPIs
            if (_collecteur != null) _buildKPIsSection(),
            const SizedBox(height: 24),

            // Transactions récentes
            _buildTransactionsSection(),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF0D8A5F), Color(0xFF056B4A)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 40,
                backgroundColor: Colors.white.withOpacity(0.2),
                child: const Icon(Icons.person, size: 40, color: Colors.white),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _collecteur!.fullName,
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Matricule: ${_collecteur!.matricule}',
                      style: const TextStyle(
                        fontSize: 12,
                        color: Colors.white70,
                      ),
                    ),
                    if (_collecteur!.email != null)
                      Text(
                        _collecteur!.email!,
                        style: const TextStyle(
                          fontSize: 12,
                          color: Colors.white70,
                        ),
                      ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Text(
              'Performance: ${_collecteur!.performanceLevel}',
              style: const TextStyle(
                fontSize: 12,
                color: Colors.white,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildKPIsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Mes Statistiques',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 12),
        GridView.count(
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          children: [
            _buildKPICard(
              title: 'Montant Collecté',
              value: _collecteur!.formattedMontantCollecte,
              icon: Icons.attach_money,
              color: const Color(0xFF0D8A5F),
            ),
            _buildKPICard(
              title: 'Clients',
              value: '${_collecteur!.nombreClients}',
              icon: Icons.people,
              color: const Color(0xFF0066CC),
            ),
            _buildKPICard(
              title: 'Transactions',
              value: '${_collecteur!.nombreTransactions}',
              icon: Icons.compare_arrows,
              color: const Color(0xFFFF6B6B),
            ),
            _buildKPICard(
              title: 'Gains Moyens',
              value: _collecteur!.formattedGainsMoyens,
              icon: Icons.trending_up,
              color: const Color(0xFFFFA500),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildKPICard({
    required String title,
    required String value,
    required IconData icon,
    required Color color,
  }) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey.shade200),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Icon(icon, color: color, size: 32),
          const SizedBox(height: 8),
          Text(
            title,
            style: const TextStyle(
              fontSize: 12,
              color: Colors.grey,
              fontWeight: FontWeight.w500,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            value,
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }

  Widget _buildTransactionsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Transactions Récentes',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 12),
        if (_transactions.isEmpty)
          Container(
            padding: const EdgeInsets.symmetric(vertical: 32),
            child: const Center(
              child: Text(
                'Aucune transaction',
                style: TextStyle(color: Colors.grey),
              ),
            ),
          )
        else
          ListView.separated(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: _transactions.length,
            separatorBuilder: (_, __) => const SizedBox(height: 8),
            itemBuilder: (context, index) {
              final tx = _transactions[index];
              return _buildTransactionTile(tx);
            },
          ),
      ],
    );
  }

  Widget _buildTransactionTile(TransactionModel transaction) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.grey.shade200),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  transaction.displayType,
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 14,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  'Réf: ${transaction.reference}',
                  style: const TextStyle(fontSize: 12, color: Colors.grey),
                ),
                const SizedBox(height: 2),
                Text(
                  transaction.formattedDate,
                  style: const TextStyle(fontSize: 12, color: Colors.grey),
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                transaction.formattedMontant,
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 14,
                ),
              ),
              const SizedBox(height: 4),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: transaction.getStatusColor().withOpacity(0.1),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  transaction.displayStatus,
                  style: TextStyle(
                    fontSize: 10,
                    color: transaction.getStatusColor(),
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
