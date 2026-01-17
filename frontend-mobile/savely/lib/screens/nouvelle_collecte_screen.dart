import 'package:flutter/material.dart';
import '../services/collecte_api.dart';
import '../services/error_handler.dart';

class NouvelleCollecteScreen extends StatefulWidget {
  final String matriculeCollecteur;
  
  const NouvelleCollecteScreen({
    super.key,
    required this.matriculeCollecteur,
  });

  @override
  State<NouvelleCollecteScreen> createState() => _NouvelleCollecteScreenState();
}

class _NouvelleCollecteScreenState extends State<NouvelleCollecteScreen> {
  // Controllers
  final _montantController = TextEditingController();
  final _descriptionController = TextEditingController();

  // State variables
  List<dynamic> _clients = [];
  List<dynamic> _comptes = [];
  List<dynamic> _caissiers = [];
  
  String? _selectedClientCode;
  String? _selectedCompteId;
  String? _selectedTypeTransaction;
  int? _selectedCaissierId;
  
  bool _isLoading = false;
  bool _isLoadingComptes = false;

  @override
  void initState() {
    super.initState();
    _chargerDonnees();
  }

  Future<void> _chargerDonnees() async {
    setState(() => _isLoading = true);
    try {
      final clients = await CollecteApi.getClientsCollecteur(widget.matriculeCollecteur);
      final caissiers = await CollecteApi.getCaissiers();
      
      setState(() {
        _clients = clients;
        _caissiers = caissiers;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ErrorHandler.showErrorSnackBar(context, e);
      }
    }
  }

  Future<void> _chargerComptesClient(String codeClient) async {
    setState(() => _isLoadingComptes = true);
    try {
      final comptes = await CollecteApi.getComptesClient(codeClient);
      setState(() {
        _comptes = comptes;
        _selectedCompteId = null;
        _isLoadingComptes = false;
      });
    } catch (e) {
      setState(() => _isLoadingComptes = false);
      if (mounted) {
        ErrorHandler.showErrorSnackBar(context, e);
      }
    }
  }

  Future<void> _soumettre() async {
    // Validation
    if (_selectedClientCode == null ||
        _selectedCompteId == null ||
        _selectedTypeTransaction == null ||
        _selectedCaissierId == null ||
        _montantController.text.isEmpty) {
      ErrorHandler.showErrorSnackBar(
        context,
        'Veuillez remplir tous les champs obligatoires',
      );
      return;
    }

    final montant = double.tryParse(_montantController.text);
    if (montant == null || montant <= 0) {
      ErrorHandler.showErrorSnackBar(
        context,
        'Montant invalide',
      );
      return;
    }

    setState(() => _isLoading = true);
    try {
      await CollecteApi.creerCollecte(
        idCompte: _selectedCompteId!,
        typeTransaction: _selectedTypeTransaction!,
        montant: montant,
        idCaissier: _selectedCaissierId!,
        description: _descriptionController.text,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Collecte créée avec succès !'),
            backgroundColor: Colors.green,
          ),
        );
        Navigator.pop(context, true);
      }
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ErrorHandler.showErrorDialog(context, e);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Nouvelle Collecte'),
        backgroundColor: const Color(0xFF0D8A5F),
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // En-tête
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: const Color(0xFF0D8A5F).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: const Row(
                      children: [
                        Icon(Icons.info_outline, color: Color(0xFF0D8A5F)),
                        SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            'Remplissez tous les champs pour créer une nouvelle collecte',
                            style: TextStyle(fontSize: 14),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 24),

                  // Dropdown Client
                  DropdownButtonFormField<String>(
                    decoration: const InputDecoration(
                      labelText: 'Client *',
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.person),
                    ),
                    value: _selectedClientCode,
                    items: _clients.map((client) {
                      return DropdownMenuItem<String>(
                        value: client['codeClient'],
                        child: Text('${client['nom']} ${client['prenom']}'),
                      );
                    }).toList(),
                    onChanged: (value) {
                      setState(() => _selectedClientCode = value);
                      if (value != null) {
                        _chargerComptesClient(value);
                      }
                    },
                  ),
                  const SizedBox(height: 16),

                  // Dropdown Compte
                  _isLoadingComptes
                      ? const Center(child: CircularProgressIndicator())
                      : DropdownButtonFormField<String>(
                          decoration: const InputDecoration(
                            labelText: 'Compte *',
                            border: OutlineInputBorder(),
                            prefixIcon: Icon(Icons.account_balance),
                          ),
                          value: _selectedCompteId,
                          items: _comptes.map((compte) {
                            return DropdownMenuItem<String>(
                              value: compte['idCompte'],
                              child: Text('${compte['numeroCompte']} - ${compte['typeCompte']}'),
                            );
                          }).toList(),
                          onChanged: (value) {
                            setState(() => _selectedCompteId = value);
                          },
                        ),
                  const SizedBox(height: 16),

                  // Dropdown Type Transaction
                  DropdownButtonFormField<String>(
                    decoration: const InputDecoration(
                      labelText: 'Type de Transaction *',
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.swap_horiz),
                    ),
                    value: _selectedTypeTransaction,
                    items: const [
                      DropdownMenuItem(
                        value: 'DEPOT',
                        child: Row(
                          children: [
                            Icon(Icons.arrow_downward, color: Colors.green, size: 20),
                            SizedBox(width: 8),
                            Text('Dépôt'),
                          ],
                        ),
                      ),
                      DropdownMenuItem(
                        value: 'RETRAIT',
                        child: Row(
                          children: [
                            Icon(Icons.arrow_upward, color: Colors.red, size: 20),
                            SizedBox(width: 8),
                            Text('Retrait'),
                          ],
                        ),
                      ),
                      DropdownMenuItem(
                        value: 'COTISATION',
                        child: Row(
                          children: [
                            Icon(Icons.savings, color: Colors.blue, size: 20),
                            SizedBox(width: 8),
                            Text('Cotisation'),
                          ],
                        ),
                      ),
                    ],
                    onChanged: (value) {
                      setState(() => _selectedTypeTransaction = value);
                    },
                  ),
                  const SizedBox(height: 16),

                  // Champ Montant
                  TextField(
                    controller: _montantController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Montant (FCFA) *',
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.attach_money),
                      hintText: 'Ex: 5000',
                    ),
                  ),
                  const SizedBox(height: 16),

                  // Dropdown Caissier
                  DropdownButtonFormField<int>(
                    decoration: const InputDecoration(
                      labelText: 'Caissier Validateur *',
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.person_outline),
                    ),
                    value: _selectedCaissierId,
                    items: _caissiers.map((caissier) {
                      return DropdownMenuItem<int>(
                        value: caissier['idEmploye'],
                        child: Text('${caissier['nom']} ${caissier['prenom']}'),
                      );
                    }).toList(),
                    onChanged: (value) {
                      setState(() => _selectedCaissierId = value);
                    },
                  ),
                  const SizedBox(height: 16),

                  // Champ Description
                  TextField(
                    controller: _descriptionController,
                    maxLines: 3,
                    decoration: const InputDecoration(
                      labelText: 'Description (optionnel)',
                      border: OutlineInputBorder(),
                      hintText: 'Ajoutez une note...',
                    ),
                  ),
                  const SizedBox(height: 24),

                  // Bouton Soumettre
                  ElevatedButton(
                    onPressed: _isLoading ? null : _soumettre,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF0D8A5F),
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                    ),
                    child: _isLoading
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                            ),
                          )
                        : const Text(
                            'Créer la Collecte',
                            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                          ),
                  ),
                ],
              ),
            ),
    );
  }

  @override
  void dispose() {
    _montantController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }
}
