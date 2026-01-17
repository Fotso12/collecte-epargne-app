import 'package:flutter/material.dart';
import '../services/collecteur_api.dart';
import '../services/client_api.dart';
import '../services/admin_api.dart';
import '../services/auth_api.dart';
import '../services/error_handler.dart';

class CollecteurCollect extends StatefulWidget {
  const CollecteurCollect({super.key});

  @override
  State<CollecteurCollect> createState() => _CollecteurCollectState();
}

class _CollecteurCollectState extends State<CollecteurCollect> {
  final _formKey = GlobalKey<FormState>();
  final _codeClientCtrl = TextEditingController();
  final _idCompteCtrl = TextEditingController();
  final _montantCtrl = TextEditingController();
  String _type = 'DEPOT';
  bool _isLoading = false;
  String? _error;
  List<dynamic> _clients = [];
  String? _selectedClientCode;
  List<dynamic> _accounts = [];
  String? _selectedAccountId;
  List<Map<String, dynamic>> _caissiers = [];
  String? _selectedCaissierId;

  @override
  void dispose() {
    _codeClientCtrl.dispose();
    _idCompteCtrl.dispose();
    _montantCtrl.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final user = AuthApi.currentUser;
      if (user == null) throw Exception('Utilisateur non connecté');

      final montant =
          double.tryParse(_montantCtrl.text.replaceAll(',', '')) ?? 0.0;
      final res = await CollecteurApi.createTransactionOffline(
        loginOrId: user.login,
        codeClient: _codeClientCtrl.text,
        idCompte: _idCompteCtrl.text,
        montant: montant,
        typeTransaction: _type,
      );

      if (res == null) throw Exception('Erreur création transaction');

      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Transaction enregistrée')));
      Navigator.pop(context);
    } catch (e) {
      setState(() {
        _error = ErrorHandler.getDisplayMessage(e.toString());
        _isLoading = false;
      });
    }
  }

  Future<void> _loadClientsAndCaissiers() async {
    try {
      final user = AuthApi.currentUser;
      if (user == null) throw Exception('Utilisateur non connecté');
      final clients = await CollecteurApi.getClients(user.login);
      final users = await AdminApi.getUsers();
      final caissiers = users
          .where((u) {
            final role = (u['role'] ?? u['roleCode'] ?? '')
                .toString()
                .toLowerCase();
            return role.contains('caissier') || role.contains('cashier');
          })
          .map((u) => u as Map<String, dynamic>)
          .toList();

      setState(() {
        _clients = clients;
        _caissiers = caissiers;
      });
    } catch (e) {
      // silent fallback
    }
  }

  Future<void> _onClientSelected(String? code) async {
    if (code == null) return;
    setState(() {
      _selectedClientCode = code;
      _accounts = [];
      _selectedAccountId = null;
      _codeClientCtrl.text = code;
    });
    try {
      final accs = await ClientApi.getClientAccounts(code);
      setState(() {
        _accounts = accs;
      });
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Nouvelle collecte'),
        backgroundColor: const Color(0xFF0D8A5F),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              // Client selector
              DropdownButtonFormField<String>(
                value:
                    _selectedClientCode ??
                    (_codeClientCtrl.text.isNotEmpty
                        ? _codeClientCtrl.text
                        : null),
                items: _clients.map((c) {
                  final m = c as Map<String, dynamic>;
                  final code =
                      m['codeClient']?.toString() ??
                      m['numeroClient']?.toString() ??
                      '';
                  final name =
                      m['fullName'] ?? '${m['prenom'] ?? ''} ${m['nom'] ?? ''}';
                  return DropdownMenuItem(
                    value: code,
                    child: Text('$name — $code'),
                  );
                }).toList(),
                onChanged: (v) => _onClientSelected(v),
                decoration: const InputDecoration(labelText: 'Client'),
                validator: (v) => (v == null || v.isEmpty) ? 'Requis' : null,
              ),
              const SizedBox(height: 12),
              // Accounts selector
              DropdownButtonFormField<String>(
                value:
                    _selectedAccountId ??
                    (_idCompteCtrl.text.isNotEmpty ? _idCompteCtrl.text : null),
                items: _accounts.map((a) {
                  final m = a as Map<String, dynamic>;
                  final id =
                      m['idCompte']?.toString() ?? m['id']?.toString() ?? '';
                  final num =
                      m['numeroCompte']?.toString() ??
                      m['numero']?.toString() ??
                      id;
                  return DropdownMenuItem(value: id, child: Text('$num'));
                }).toList(),
                onChanged: (v) {
                  setState(() {
                    _selectedAccountId = v;
                    _idCompteCtrl.text = v ?? '';
                  });
                },
                decoration: const InputDecoration(labelText: 'Compte'),
                validator: (v) => (v == null || v.isEmpty) ? 'Requis' : null,
              ),
              const SizedBox(height: 12),
              const SizedBox(height: 12),
              TextFormField(
                controller: _montantCtrl,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: 'Montant'),
                validator: (v) => (v == null || v.isEmpty) ? 'Requis' : null,
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                value: _type,
                items: const [
                  DropdownMenuItem(value: 'DEPOT', child: Text('Dépôt')),
                  DropdownMenuItem(value: 'RETRAIT', child: Text('Retrait')),
                ],
                onChanged: (v) => setState(() => _type = v ?? 'DEPOT'),
                decoration: const InputDecoration(labelText: 'Type'),
              ),
              const SizedBox(height: 12),
              // Caissier selector
              DropdownButtonFormField<String>(
                value: _selectedCaissierId,
                items: _caissiers.map((c) {
                  final id =
                      c['id']?.toString() ?? c['login']?.toString() ?? '';
                  final label = c['fullName'] ?? c['email'] ?? id;
                  return DropdownMenuItem(
                    value: id,
                    child: Text(label.toString()),
                  );
                }).toList(),
                onChanged: (v) => setState(() => _selectedCaissierId = v),
                decoration: const InputDecoration(
                  labelText: 'Envoyer au caissier (optionnel)',
                ),
              ),
              const SizedBox(height: 20),
              if (_error != null)
                Text(_error!, style: const TextStyle(color: Colors.red)),
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: _isLoading ? null : _submit,
                child: const Text('Enregistrer'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  @override
  void initState() {
    super.initState();
    _loadClientsAndCaissiers();
  }
}
