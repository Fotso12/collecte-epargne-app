import 'package:flutter/material.dart';
import '../services/employe_api.dart';
import '../services/transaction_offline_api.dart';
import 'package:intl/intl.dart';

class HistoriqueCollectesScreen extends StatefulWidget {
  final String loginCollecteur;
  final String? idEmploye;

  const HistoriqueCollectesScreen({
    super.key,
    required this.loginCollecteur,
    this.idEmploye,
  });

  @override
  State<HistoriqueCollectesScreen> createState() => _HistoriqueCollectesScreenState();
}

class _HistoriqueCollectesScreenState extends State<HistoriqueCollectesScreen> {
  List<Map<String, dynamic>> _transactions = [];
  bool _isLoading = true;
  String? _idEmploye;
  String _filterType = 'Tous'; // Tous, DEPOT, RETRAIT

  final DateFormat _dateFormat = DateFormat('dd/MM/yyyy HH:mm');

  @override
  void initState() {
    super.initState();
    _loadTransactions();
  }

  Future<void> _loadTransactions() async {
    setState(() => _isLoading = true);
    try {
      if (widget.idEmploye == null) {
        _idEmploye = await EmployeApi.getIdEmployeByLogin(widget.loginCollecteur);
      } else {
        _idEmploye = widget.idEmploye;
      }

      if (_idEmploye == null) {
        throw Exception('Impossible de trouver l\'ID du collecteur');
      }

      // Les endpoints de transactions offline n'existent pas encore, gérer l'erreur gracieusement
      try {
        _transactions = await TransactionOfflineApi.getTransactionsByCollecteur(_idEmploye!);
      } catch (e) {
        // Si l'endpoint n'existe pas (404), continuer avec une liste vide
        _transactions = [];
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    } finally {
      setState(() => _isLoading = false);
    }
  }

  List<Map<String, dynamic>> get _filteredTransactions {
    if (_filterType == 'Tous') {
      return _transactions;
    }
    return _transactions
        .where((t) => t['typeTransaction']?.toString() == _filterType)
        .toList();
  }

  double _calculateTotal() {
    return _filteredTransactions.fold<double>(
      0.0,
      (sum, t) => sum + ((t['montant'] as num?)?.toDouble() ?? 0.0),
    );
  }

  Color _getTypeColor(String? type) {
    switch (type) {
      case 'DEPOT':
        return Colors.green;
      case 'RETRAIT':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  IconData _getTypeIcon(String? type) {
    switch (type) {
      case 'DEPOT':
        return Icons.arrow_downward;
      case 'RETRAIT':
        return Icons.arrow_upward;
      default:
        return Icons.swap_horiz;
    }
  }

  String _formatDate(String? dateStr) {
    if (dateStr == null) return 'N/A';
    try {
      final date = DateTime.parse(dateStr);
      return _dateFormat.format(date);
    } catch (e) {
      return dateStr;
    }
  }

  String _formatAmount(dynamic amount) {
    if (amount == null) return '0 FCFA';
    final value = (amount is num) ? amount.toDouble() : double.tryParse(amount.toString()) ?? 0.0;
    return '${value.toStringAsFixed(0).replaceAllMapped(
      RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
      (Match m) => '${m[1]},',
    )} FCFA';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Historique des Collectes'),
        backgroundColor: Theme.of(context).colorScheme.primary,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadTransactions,
            tooltip: 'Actualiser',
          ),
        ],
      ),
      body: Column(
        children: [
          // Filtres
          Container(
            padding: const EdgeInsets.all(16),
            color: Colors.grey[50],
            child: Row(
              children: [
                Expanded(
                  child: SegmentedButton<String>(
                    segments: const [
                      ButtonSegment(value: 'Tous', label: Text('Tous')),
                      ButtonSegment(value: 'DEPOT', label: Text('Dépôts')),
                      ButtonSegment(value: 'RETRAIT', label: Text('Retraits')),
                    ],
                    selected: {_filterType},
                    onSelectionChanged: (Set<String> selected) {
                      setState(() => _filterType = selected.first);
                    },
                  ),
                ),
              ],
            ),
          ),

          // Résumé
          if (!_isLoading && _filteredTransactions.isNotEmpty)
            Container(
              padding: const EdgeInsets.all(16),
              color: Theme.of(context).colorScheme.primaryContainer,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  Column(
                    children: [
                      Text(
                        '${_filteredTransactions.length}',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                      ),
                      Text(
                        'Transactions',
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                  Container(
                    width: 1,
                    height: 40,
                    color: Colors.grey[300],
                  ),
                  Column(
                    children: [
                      Text(
                        _formatAmount(_calculateTotal()),
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                      ),
                      Text(
                        'Total',
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),

          // Liste des transactions
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _filteredTransactions.isEmpty
                    ? Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.history,
                              size: 64,
                              color: Colors.grey[400],
                            ),
                            const SizedBox(height: 16),
                            Text(
                              _filterType == 'Tous'
                                  ? 'Aucune transaction'
                                  : 'Aucune transaction de type $_filterType',
                              style: TextStyle(
                                fontSize: 16,
                                color: Colors.grey[600],
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'Les collectes apparaîtront ici',
                              style: TextStyle(
                                fontSize: 14,
                                color: Colors.grey[500],
                              ),
                            ),
                          ],
                        ),
                      )
                    : RefreshIndicator(
                        onRefresh: _loadTransactions,
                        child: ListView.builder(
                          padding: const EdgeInsets.all(16),
                          itemCount: _filteredTransactions.length,
                          itemBuilder: (context, index) {
                            final transaction = _filteredTransactions[index];
                            final type = transaction['typeTransaction']?.toString();
                            final montant = transaction['montant'];
                            final date = transaction['dateTransaction']?.toString();
                            final description = transaction['description']?.toString();
                            final statut = transaction['statutSynchro']?.toString() ?? 'EN_ATTENTE';

                            return Card(
                              margin: const EdgeInsets.only(bottom: 12),
                              elevation: 2,
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(12),
                                side: BorderSide(
                                  color: _getTypeColor(type).withOpacity(0.3),
                                  width: 1,
                                ),
                              ),
                              child: ListTile(
                                contentPadding: const EdgeInsets.all(16),
                                leading: Container(
                                  padding: const EdgeInsets.all(12),
                                  decoration: BoxDecoration(
                                    color: _getTypeColor(type).withOpacity(0.1),
                                    borderRadius: BorderRadius.circular(10),
                                  ),
                                  child: Icon(
                                    _getTypeIcon(type),
                                    color: _getTypeColor(type),
                                    size: 24,
                                  ),
                                ),
                                title: Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Text(
                                      type ?? 'N/A',
                                      style: TextStyle(
                                        fontWeight: FontWeight.bold,
                                        color: _getTypeColor(type),
                                      ),
                                    ),
                                    Text(
                                      _formatAmount(montant),
                                      style: TextStyle(
                                        fontWeight: FontWeight.bold,
                                        fontSize: 16,
                                        color: _getTypeColor(type),
                                      ),
                                    ),
                                  ],
                                ),
                                subtitle: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const SizedBox(height: 4),
                                    Text(_formatDate(date)),
                                    if (description != null && description.isNotEmpty)
                                      Padding(
                                        padding: const EdgeInsets.only(top: 4),
                                        child: Text(
                                          description,
                                          style: TextStyle(
                                            fontSize: 12,
                                            color: Colors.grey[600],
                                          ),
                                        ),
                                      ),
                                    const SizedBox(height: 4),
                                    Container(
                                      padding: const EdgeInsets.symmetric(
                                        horizontal: 8,
                                        vertical: 4,
                                      ),
                                      decoration: BoxDecoration(
                                        color: statut == 'SYNCHRONISE'
                                            ? Colors.green.withOpacity(0.1)
                                            : Colors.orange.withOpacity(0.1),
                                        borderRadius: BorderRadius.circular(8),
                                      ),
                                      child: Text(
                                        statut,
                                        style: TextStyle(
                                          fontSize: 10,
                                          color: statut == 'SYNCHRONISE'
                                              ? Colors.green[700]
                                              : Colors.orange[700],
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            );
                          },
                        ),
                      ),
          ),
        ],
      ),
    );
  }
}

