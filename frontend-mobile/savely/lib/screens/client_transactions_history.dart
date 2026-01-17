import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/client_api.dart';
import '../services/error_handler.dart';
import '../widgets/transaction_status_badge.dart';

class ClientTransactionsHistory extends StatefulWidget {
  final String codeClient;
  
  const ClientTransactionsHistory({
    super.key,
    required this.codeClient,
  });

  @override
  State<ClientTransactionsHistory> createState() => _ClientTransactionsHistoryState();
}

class _ClientTransactionsHistoryState extends State<ClientTransactionsHistory> {
  List<dynamic> _transactions = [];
  bool _isLoading = true;
  final DateFormat _dateFormat = DateFormat('dd/MM/yyyy HH:mm');
  final NumberFormat _currencyFormat = NumberFormat.currency(locale: 'fr_FR', symbol: 'FCFA', decimalDigits: 0);

  @override
  void initState() {
    super.initState();
    _chargerTransactions();
  }

  Future<void> _chargerTransactions() async {
    setState(() => _isLoading = true);
    try {
      // Utilisation d'une méthode existante ou nouvelle pour récupérer transactions
      // Pour l'instant on suppose que getTransactions existe dans ClientApi ou on l'ajoute
      final transactions = await ClientApi.getTransactions(widget.codeClient);
      
      setState(() {
        _transactions = transactions;
        _isLoading = false;
      });
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ErrorHandler.showErrorSnackBar(context, e);
      }
    }
  }

  Color _getTypeColor(String type) {
    switch (type.toUpperCase()) {
      case 'DEPOT':
        return Colors.green;
      case 'RETRAIT':
        return Colors.red;
            case 'COTISATION':
        return Colors.blue;
      default:
        return Colors.grey;
    }
  }

  IconData _getTypeIcon(String type) {
    switch (type.toUpperCase()) {
      case 'DEPOT':
        return Icons.arrow_downward;
      case 'RETRAIT':
        return Icons.arrow_upward;
            case 'COTISATION':
        return Icons.savings;
      default:
        return Icons.swap_horiz;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Historique Transactions'),
        backgroundColor: const Color(0xFF0D8A5F),
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _chargerTransactions,
              child: _transactions.isEmpty
                  ? ListView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      children: const [
                         SizedBox(height: 100),
                        Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.history, size: 64, color: Colors.grey),
                              SizedBox(height: 16),
                              Text('Aucune transaction trouvée', style: TextStyle(fontSize: 18, color: Colors.grey)),
                            ],
                          ),
                        ),
                      ],
                    )
                  : ListView.builder(
                      itemCount: _transactions.length,
                      padding: const EdgeInsets.all(16),
                      itemBuilder: (context, index) {
                        final tx = _transactions[index];
                        final type = tx['typeTransaction'] ?? 'AUTRE';
                        final montant = tx['montant'] ?? 0;
                        final dateStr = tx['dateTransaction'];
                        
                        DateTime? date;
                        if (dateStr != null) {
                          try {
                            date = DateTime.parse(dateStr);
                          } catch (_) {}
                        }

                        return Card(
                          margin: const EdgeInsets.only(bottom: 12),
                          elevation: 2,
                          child: Padding(
                            padding: const EdgeInsets.all(12),
                            child: Column(
                              children: [
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Row(
                                      children: [
                                        Container(
                                          padding: const EdgeInsets.all(8),
                                          decoration: BoxDecoration(
                                            color: _getTypeColor(type).withOpacity(0.1),
                                            shape: BoxShape.circle,
                                          ),
                                          child: Icon(_getTypeIcon(type), color: _getTypeColor(type), size: 20),
                                        ),
                                        const SizedBox(width: 12),
                                        Column(
                                          crossAxisAlignment: CrossAxisAlignment.start,
                                          children: [
                                            Text(
                                              type,
                                              style: TextStyle(
                                                fontWeight: FontWeight.bold,
                                                color: _getTypeColor(type),
                                              ),
                                            ),
                                            Text(
                                              date != null ? _dateFormat.format(date) : (dateStr ?? ''),
                                              style: TextStyle(color: Colors.grey[600], fontSize: 12),
                                            ),
                                          ],
                                        ),
                                      ],
                                    ),
                                    Text(
                                      _currencyFormat.format(montant),
                                      style: const TextStyle(
                                        fontWeight: FontWeight.bold,
                                        fontSize: 16,
                                      ),
                                    ),
                                  ],
                                ),
                                const Divider(),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          const Text('Collecteur', style: TextStyle(fontSize: 11, color: Colors.grey)),
                                          Text(
                                            tx['nomInitiateur'] ?? 'N/A',
                                            style: const TextStyle(fontWeight: FontWeight.w500, fontSize: 13),
                                          ),
                                        ],
                                      ),
                                    ),
                                    TransactionStatusBadge(status: tx['statut'] ?? 'EN_ATTENTE'),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
            ),
    );
  }
}
