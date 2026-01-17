import 'package:flutter/material.dart';
import '../services/client_api.dart';
import '../services/compte_api.dart';
import '../services/auth_api.dart';

class ClientTransactions extends StatefulWidget {
  const ClientTransactions({super.key});

  @override
  State<ClientTransactions> createState() => _ClientTransactionsState();
}

class _ClientTransactionsState extends State<ClientTransactions> {
  List<Map<String, dynamic>> _txs = [];
  bool _isLoading = true;
  String? _error;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final args =
        ModalRoute.of(context)?.settings.arguments as Map<String, dynamic>?;
    final codeClient = args?['codeClient'] as String?;
    _loadTransactions(codeClient);
  }

  Future<void> _loadTransactions(String? codeClient) async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      String code = codeClient ?? '';
      if (code.isEmpty) {
        final user = AuthApi.currentUser;
        if (user == null) throw Exception('Utilisateur non connecté');
        code = await ClientApi.getCodeClientByLogin(user.login);
      }

      final comptes = await ClientApi.getClientAccounts(code);
      // If accounts exist, fetch transactions for each and merge
      final List<Map<String, dynamic>> merged = [];
      for (var c in comptes) {
        String id = '';
        if (c is Map<String, dynamic>) {
          id = (c['idCompte'] ?? c['id'] ?? '').toString();
        } else if (c is String)
          id = c;
        if (id.isNotEmpty) {
          final txs = await CompteApi.getTransactionsByCompte(id);
          merged.addAll(txs);
        }
      }

      setState(() {
        _txs = merged;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Historique des transactions'),
        backgroundColor: const Color(0xFF0D8A5F),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
          ? Center(child: Text(_error!))
          : _txs.isEmpty
          ? const Center(child: Text('Aucune transaction trouvée'))
          : ListView.separated(
              padding: const EdgeInsets.all(12),
              itemCount: _txs.length,
              separatorBuilder: (_, __) => const Divider(),
              itemBuilder: (context, i) {
                final tx = _txs[i];
                final montant = tx['montant'] ?? tx['amount'] ?? '';
                final date = tx['dateTransaction'] ?? tx['date'] ?? '';
                final ref = tx['reference'] ?? tx['id'] ?? '';
                final type = tx['typeTransaction'] ?? tx['type'] ?? '';
                return ListTile(
                  title: Text('$type — ${montant.toString()}'),
                  subtitle: Text('Réf: $ref\n$date'),
                );
              },
            ),
    );
  }
}
