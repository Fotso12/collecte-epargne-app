import 'package:flutter/material.dart';
import '../services/employe_api.dart';

class ClientsListScreen extends StatefulWidget {
  final String loginCollecteur;
  final String? idEmploye;

  const ClientsListScreen({
    super.key,
    required this.loginCollecteur,
    this.idEmploye,
  });

  @override
  State<ClientsListScreen> createState() => _ClientsListScreenState();
}

class _ClientsListScreenState extends State<ClientsListScreen> {
  List<Map<String, dynamic>> _clients = [];
  bool _isLoading = true;
  String? _idEmploye;
  String? _searchQuery;

  @override
  void initState() {
    super.initState();
    _loadClients();
  }

  Future<void> _loadClients() async {
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

      _clients = await EmployeApi.getClientsByCollecteur(_idEmploye!);
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

  List<Map<String, dynamic>> get _filteredClients {
    if (_searchQuery == null || _searchQuery!.isEmpty) {
      return _clients;
    }
    final query = _searchQuery!.toLowerCase();
    return _clients.where((client) {
      final nom = '${client['prenom'] ?? ''} ${client['nom'] ?? ''}'.toLowerCase();
      final code = (client['codeClient']?.toString() ?? '').toLowerCase();
      final numero = (client['numeroClient']?.toString() ?? '').toLowerCase();
      return nom.contains(query) || code.contains(query) || numero.contains(query);
    }).toList();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mes Clients'),
        backgroundColor: Theme.of(context).colorScheme.primary,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadClients,
            tooltip: 'Actualiser',
          ),
        ],
      ),
      body: Column(
        children: [
          // Barre de recherche
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Rechercher un client...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: _searchQuery != null && _searchQuery!.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          setState(() => _searchQuery = null);
                        },
                      )
                    : null,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              onChanged: (value) {
                setState(() => _searchQuery = value);
              },
            ),
          ),

          // Liste des clients
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _filteredClients.isEmpty
                    ? Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.people_outline,
                              size: 64,
                              color: Colors.grey[400],
                            ),
                            const SizedBox(height: 16),
                            Text(
                              _searchQuery != null && _searchQuery!.isNotEmpty
                                  ? 'Aucun client trouvé'
                                  : 'Aucun client assigné',
                              style: TextStyle(
                                fontSize: 16,
                                color: Colors.grey[600],
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              _searchQuery != null && _searchQuery!.isNotEmpty
                                  ? 'Essayez une autre recherche'
                                  : 'Contactez votre superviseur pour obtenir des clients',
                              style: TextStyle(
                                fontSize: 14,
                                color: Colors.grey[500],
                              ),
                              textAlign: TextAlign.center,
                            ),
                          ],
                        ),
                      )
                    : RefreshIndicator(
                        onRefresh: _loadClients,
                        child: ListView.builder(
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          itemCount: _filteredClients.length,
                          itemBuilder: (context, index) {
                            final client = _filteredClients[index];
                            final nom = '${client['prenom'] ?? ''} ${client['nom'] ?? ''}'.trim();
                            final code = client['codeClient']?.toString() ?? 'N/A';
                            final numero = client['numeroClient']?.toString() ?? 'N/A';
                            final telephone = client['telephone']?.toString() ?? 'N/A';
                            final email = client['email']?.toString() ?? '';

                            return Card(
                              margin: const EdgeInsets.only(bottom: 12),
                              elevation: 2,
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: ListTile(
                                contentPadding: const EdgeInsets.symmetric(
                                  horizontal: 16,
                                  vertical: 12,
                                ),
                                leading: CircleAvatar(
                                  backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                                  child: Text(
                                    nom.isNotEmpty
                                        ? nom[0].toUpperCase()
                                        : code.isNotEmpty
                                            ? code[0].toUpperCase()
                                            : 'C',
                                    style: TextStyle(
                                      color: Theme.of(context).colorScheme.primary,
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                                title: Text(
                                  nom.isNotEmpty ? nom : code,
                                  style: const TextStyle(
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                subtitle: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const SizedBox(height: 4),
                                    Text('Code: $code'),
                                    if (numero != 'N/A') Text('N°: $numero'),
                                    Text('Tél: $telephone'),
                                    if (email.isNotEmpty) Text('Email: $email'),
                                  ],
                                ),
                                trailing: Icon(
                                  Icons.chevron_right,
                                  color: Colors.grey[400],
                                ),
                                onTap: () {
                                  // TODO: Naviguer vers les détails du client
                                },
                              ),
                            );
                          },
                        ),
                      ),
          ),

          // Résumé
          if (!_isLoading && _clients.isNotEmpty)
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.primaryContainer,
                borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(16),
                  topRight: Radius.circular(16),
                ),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  Column(
                    children: [
                      Text(
                        '${_clients.length}',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                      ),
                      Text(
                        'Total clients',
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
                        '${_filteredClients.length}',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                      ),
                      Text(
                        'Résultats',
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
        ],
      ),
    );
  }
}

