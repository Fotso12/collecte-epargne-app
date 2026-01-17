import 'package:flutter/material.dart';
import '../services/compte_api.dart';
import '../services/error_handler.dart';
import '../models/compte_model.dart';

class AccountHistory extends StatefulWidget {
  const AccountHistory({super.key});

  @override
  State<AccountHistory> createState() => _AccountHistoryState();
}

class _AccountHistoryState extends State<AccountHistory> {
  late final String idCompte;
  String? numeroCompte;
  CompteModel? compte;
  List<Map<String, dynamic>> _transactions = [];
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final args =
        ModalRoute.of(context)?.settings.arguments as Map<String, dynamic>?;
    idCompte = (args?['idCompte'] ?? '') as String;
    numeroCompte = args?['numCompte'] as String?;
    _loadHistory();
  }

  Future<void> _loadHistory() async {
    try {
      setState(() {
        _isLoading = true;
        _errorMessage = null;
      });

      if (idCompte.isEmpty) throw Exception('Identifiant de compte manquant');

      // Charger le compte (détails) si possible
      try {
        final data = await CompteApi.getCompteById(idCompte);
        compte = data;
      } catch (_) {
        compte = null;
      }

      // Charger les transactions (endpoint backend attendu)
      final txs = await CompteApi.getTransactionsByCompte(idCompte);

      setState(() {
        _transactions = txs;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = ErrorHandler.getDisplayMessage(e.toString());
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          numeroCompte != null
              ? 'Historique $numeroCompte'
              : 'Historique du compte',
        ),
        backgroundColor: const Color(0xFF0D8A5F),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _errorMessage != null
          ? _buildError()
          : _buildList(),
    );
  }

  Widget _buildError() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, color: Colors.red, size: 64),
          const SizedBox(height: 12),
          Text(_errorMessage ?? 'Erreur'),
          const SizedBox(height: 12),
          ElevatedButton(
            onPressed: _loadHistory,
            child: const Text('Réessayer'),
          ),
        ],
      ),
    );
  }

  Widget _buildList() {
    if (_transactions.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Text('Aucune transaction trouvée pour ce compte.'),
        ),
      );
    }

    return ListView.separated(
      padding: const EdgeInsets.all(12),
      itemCount: _transactions.length,
      separatorBuilder: (_, __) => const Divider(),
      itemBuilder: (context, i) {
        final tx = _transactions[i];
        final montant = tx['montant'] ?? tx['amount'] ?? tx['solde'] ?? '';
        final date =
            tx['dateTransaction'] ?? tx['date'] ?? tx['createdAt'] ?? '';
        final ref = tx['reference'] ?? tx['id'] ?? '';
        final type = tx['typeTransaction'] ?? tx['type'] ?? '';

        return ListTile(
          title: Text('$type — ${montant.toString()}'),
          subtitle: Text('Réf: $ref\n$date'),
          isThreeLine: true,
          trailing: Text(
            montant is num ? montant.toStringAsFixed(0) : montant.toString(),
          ),
        );
      },
    );
  }
}
