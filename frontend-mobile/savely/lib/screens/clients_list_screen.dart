import 'package:flutter/material.dart';
import '../services/collecte_api.dart';
import '../services/error_handler.dart';

class ClientsListScreen extends StatefulWidget {
  final String matriculeCollecteur;

  const ClientsListScreen({
    super.key,
    required this.matriculeCollecteur,
  });

  @override
  State<ClientsListScreen> createState() => _ClientsListScreenState();
}

class _ClientsListScreenState extends State<ClientsListScreen> {
  List<dynamic> _clients = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _chargerClients();
  }

  Future<void> _chargerClients() async {
    setState(() => _isLoading = true);
    try {
      final clients = await CollecteApi.getClientsCollecteur(widget.matriculeCollecteur);
      setState(() {
        _clients = clients;
        _isLoading = false;
      });
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ErrorHandler.showErrorSnackBar(context, e);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mes Clients'),
        backgroundColor: const Color(0xFF0D8A5F),
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _chargerClients,
              child: _clients.isEmpty
                  ? ListView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      children: const [
                        SizedBox(height: 100),
                        Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.people_outline, size: 64, color: Colors.grey),
                              SizedBox(height: 16),
                              Text('Aucun client trouvé', style: TextStyle(fontSize: 18, color: Colors.grey)),
                            ],
                          ),
                        ),
                      ],
                    )
                  : ListView.builder(
                      itemCount: _clients.length,
                      padding: const EdgeInsets.all(16),
                      itemBuilder: (context, index) {
                        final client = _clients[index];
                        return Card(
                          margin: const EdgeInsets.only(bottom: 12),
                          elevation: 2,
                          child: ListTile(
                            leading: CircleAvatar(
                              backgroundColor: const Color(0xFF0D8A5F).withOpacity(0.1),
                              child: Text(
                                client['nom']?.substring(0, 1).toUpperCase() ?? '?',
                                style: const TextStyle(color: Color(0xFF0D8A5F), fontWeight: FontWeight.bold),
                              ),
                            ),
                            title: Text(
                              '${client['nom']} ${client['prenom']}',
                              style: const TextStyle(fontWeight: FontWeight.bold),
                            ),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const SizedBox(height: 4),
                                Row(
                                  children: [
                                    const Icon(Icons.phone, size: 14, color: Colors.grey),
                                    const SizedBox(width: 4),
                                    Text(client['telephone'] ?? 'Non spécifié'),
                                  ],
                                ),
                                const SizedBox(height: 2),
                                Row(
                                  children: [
                                    const Icon(Icons.location_on, size: 14, color: Colors.grey),
                                    const SizedBox(width: 4),
                                    Text(client['adresse'] ?? 'Non spécifié'),
                                  ],
                                ),
                              ],
                            ),
                            trailing: const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
                            onTap: () {
                              // Navigation vers détails client éventuelle
                            },
                          ),
                        );
                      },
                    ),
            ),
    );
  }
}
