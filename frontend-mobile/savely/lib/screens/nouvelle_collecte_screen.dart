import 'package:flutter/material.dart';
import '../services/employe_api.dart';
import '../services/transaction_offline_api.dart';
import '../services/compte_api.dart';
import '../models/compte_model.dart';

class NouvelleCollecteScreen extends StatefulWidget {
  final String loginCollecteur;
  final String? idEmploye;

  const NouvelleCollecteScreen({
    super.key,
    required this.loginCollecteur,
    this.idEmploye,
  });

  @override
  State<NouvelleCollecteScreen> createState() => _NouvelleCollecteScreenState();
}

class _NouvelleCollecteScreenState extends State<NouvelleCollecteScreen> {
  final _formKey = GlobalKey<FormState>();
  final _montantController = TextEditingController();
  final _descriptionController = TextEditingController();
  
  String? _selectedClientCode;
  String? _selectedCompteId;
  String _selectedTypeTransaction = 'DEPOT';
  bool _isLoading = false;
  bool _loadingClients = false;
  bool _loadingComptes = false;
  
  List<Map<String, dynamic>> _clients = [];
  List<CompteModel> _comptes = [];
  String? _idEmploye;

  @override
  void initState() {
    super.initState();
    _initializeData();
  }

  Future<void> _initializeData() async {
    setState(() => _loadingClients = true);
    try {
      // Obtenir l'ID_EMPLOYE si non fourni (le backend attend l'ID, pas le matricule)
      if (widget.idEmploye == null) {
        _idEmploye = await EmployeApi.getIdEmployeByLogin(widget.loginCollecteur);
      } else {
        _idEmploye = widget.idEmploye;
      }

      if (_idEmploye == null) {
        throw Exception('Impossible de trouver l\'ID du collecteur');
      }

      // Charger les clients assignés (utilise l'ID_EMPLOYE)
      _clients = await EmployeApi.getClientsByCollecteur(_idEmploye!);
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    } finally {
      setState(() => _loadingClients = false);
    }
  }

  Future<void> _loadComptes(String codeClient) async {
    setState(() {
      _loadingComptes = true;
      _selectedCompteId = null;
      _comptes = [];
    });

    try {
      final comptesData = await CompteApi.getComptesByClient(codeClient);
      setState(() {
        _comptes = comptesData;
      });
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur lors du chargement des comptes: $e')),
        );
      }
    } finally {
      setState(() => _loadingComptes = false);
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedClientCode == null || _selectedCompteId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Veuillez sélectionner un client et un compte')),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      await TransactionOfflineApi.createTransaction(
        idEmploye: _idEmploye!,
        codeClient: _selectedClientCode!,
        idCompte: _selectedCompteId!,
        montant: double.parse(_montantController.text),
        typeTransaction: _selectedTypeTransaction,
        description: _descriptionController.text.isEmpty ? null : _descriptionController.text,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Collecte créée avec succès!')),
        );
        Navigator.pop(context, true);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  void dispose() {
    _montantController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Nouvelle Collecte'),
        backgroundColor: Theme.of(context).colorScheme.primary,
        foregroundColor: Colors.white,
      ),
      body: _loadingClients
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Card(
                      elevation: 2,
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              'Informations de la collecte',
                              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                                    fontWeight: FontWeight.bold,
                                  ),
                            ),
                            const SizedBox(height: 20),
                            
                            // Sélection du client
                            DropdownButtonFormField<String>(
                              value: _selectedClientCode,
                              decoration: const InputDecoration(
                                labelText: 'Client *',
                                border: OutlineInputBorder(),
                                prefixIcon: Icon(Icons.person),
                              ),
                              items: _clients.map((client) {
                                final nom = '${client['prenom'] ?? ''} ${client['nom'] ?? ''}'.trim();
                                final code = client['codeClient']?.toString() ?? '';
                                return DropdownMenuItem(
                                  value: code,
                                  child: Text(nom.isEmpty ? code : '$nom ($code)'),
                                );
                              }).toList(),
                              onChanged: (value) {
                                setState(() {
                                  _selectedClientCode = value;
                                  _selectedCompteId = null;
                                  _comptes = [];
                                });
                                if (value != null) {
                                  _loadComptes(value);
                                }
                              },
                              validator: (value) =>
                                  value == null ? 'Veuillez sélectionner un client' : null,
                            ),
                            const SizedBox(height: 16),
                            
                            // Sélection du compte
                            DropdownButtonFormField<String>(
                              value: _selectedCompteId,
                              decoration: InputDecoration(
                                labelText: 'Compte *',
                                border: const OutlineInputBorder(),
                                prefixIcon: const Icon(Icons.account_balance),
                                suffixIcon: _loadingComptes
                                    ? const SizedBox(
                                        width: 20,
                                        height: 20,
                                        child: Padding(
                                          padding: EdgeInsets.all(12),
                                          child: CircularProgressIndicator(strokeWidth: 2),
                                        ),
                                      )
                                    : null,
                              ),
                              items: _comptes.map((compte) {
                                return DropdownMenuItem(
                                  value: compte.idCompte.toString(),
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    mainAxisSize: MainAxisSize.min,
                                    children: [
                                      Text('Compte: ${compte.numCompte}'),
                                      Text(
                                        'Solde: ${compte.formattedSolde}',
                                        style: TextStyle(
                                          fontSize: 12,
                                          color: Colors.grey[600],
                                        ),
                                      ),
                                    ],
                                  ),
                                );
                              }).toList(),
                              onChanged: _loadingComptes
                                  ? null
                                  : (value) {
                                      setState(() => _selectedCompteId = value);
                                    },
                              validator: (value) =>
                                  value == null ? 'Veuillez sélectionner un compte' : null,
                            ),
                            const SizedBox(height: 16),
                            
                            // Type de transaction
                            DropdownButtonFormField<String>(
                              value: _selectedTypeTransaction,
                              decoration: const InputDecoration(
                                labelText: 'Type de transaction *',
                                border: OutlineInputBorder(),
                                prefixIcon: Icon(Icons.swap_horiz),
                              ),
                              items: const [
                                DropdownMenuItem(value: 'DEPOT', child: Text('Dépôt')),
                                DropdownMenuItem(value: 'RETRAIT', child: Text('Retrait')),
                              ],
                              onChanged: (value) {
                                setState(() => _selectedTypeTransaction = value!);
                              },
                            ),
                            const SizedBox(height: 16),
                            
                            // Montant
                            TextFormField(
                              controller: _montantController,
                              decoration: const InputDecoration(
                                labelText: 'Montant (FCFA) *',
                                border: OutlineInputBorder(),
                                prefixIcon: Icon(Icons.monetization_on),
                              ),
                              keyboardType: TextInputType.number,
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return 'Veuillez entrer un montant';
                                }
                                final montant = double.tryParse(value);
                                if (montant == null || montant <= 0) {
                                  return 'Montant invalide';
                                }
                                return null;
                              },
                            ),
                            const SizedBox(height: 16),
                            
                            // Description
                            TextFormField(
                              controller: _descriptionController,
                              decoration: const InputDecoration(
                                labelText: 'Description (optionnel)',
                                border: OutlineInputBorder(),
                                prefixIcon: Icon(Icons.description),
                              ),
                              maxLines: 3,
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                    
                    // Bouton de soumission
                    ElevatedButton.icon(
                      onPressed: _isLoading ? null : _submit,
                      icon: _isLoading
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                color: Colors.white,
                              ),
                            )
                          : const Icon(Icons.save),
                      label: Text(_isLoading ? 'Enregistrement...' : 'Enregistrer la collecte'),
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 16),
                        backgroundColor: Theme.of(context).colorScheme.primary,
                        foregroundColor: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}

