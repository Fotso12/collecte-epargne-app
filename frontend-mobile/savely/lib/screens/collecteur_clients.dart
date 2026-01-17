import 'package:flutter/material.dart';
import '../services/collecteur_api.dart';
import '../services/auth_api.dart';

class CollecteurClients extends StatefulWidget {
  const CollecteurClients({super.key});

  @override
  State<CollecteurClients> createState() => _CollecteurClientsState();
}

class _CollecteurClientsState extends State<CollecteurClients> {
  List<dynamic> _clients = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadClients();
  }

  Future<void> _loadClients() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final user = AuthApi.currentUser;
      if (user == null) throw Exception('Utilisateur non connecté');
      final list = await CollecteurApi.getClients(user.login);
      setState(() {
        _clients = list;
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
        title: const Text('Mes Clients'),
        backgroundColor: const Color(0xFF0D8A5F),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
          ? Center(child: Text(_error!))
          : ListView.separated(
              padding: const EdgeInsets.all(12),
              itemCount: _clients.length,
              separatorBuilder: (_, __) => const Divider(),
              itemBuilder: (context, i) {
                final c = _clients[i] as Map<String, dynamic>;
                final name =
                    c['fullName'] ?? '${c['prenom'] ?? ''} ${c['nom'] ?? ''}';
                return ListTile(
                  title: Text(name.toString()),
                  subtitle: Text(
                    c['codeClient']?.toString() ??
                        c['numeroClient']?.toString() ??
                        '',
                  ),
                  trailing: PopupMenuButton<String>(
                    onSelected: (v) {
                      if (v == 'profile') {
                        Navigator.pushNamed(
                          context,
                          '/client-profile',
                          arguments: {
                            'codeClient': c['codeClient']?.toString() ?? '',
                          },
                        );
                      } else if (v == 'transactions') {
                        Navigator.pushNamed(
                          context,
                          '/client-transactions',
                          arguments: {
                            'codeClient': c['codeClient']?.toString() ?? '',
                          },
                        );
                      }
                    },
                    itemBuilder: (_) => [
                      const PopupMenuItem(
                        value: 'profile',
                        child: Text('Profil'),
                      ),
                      const PopupMenuItem(
                        value: 'transactions',
                        child: Text('Transactions'),
                      ),
                    ],
                  ),
                );
              },
            ),
    );
  }
}
