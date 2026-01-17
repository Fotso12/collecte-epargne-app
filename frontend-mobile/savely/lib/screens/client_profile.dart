import 'dart:io';

import 'package:image_picker/image_picker.dart';
import 'package:flutter/material.dart';
import '../services/client_api.dart';
import '../services/auth_api.dart';
import '../services/error_handler.dart';

class ClientProfile extends StatefulWidget {
  const ClientProfile({super.key});

  @override
  State<ClientProfile> createState() => _ClientProfileState();
}

class _ClientProfileState extends State<ClientProfile> {
  bool _isLoading = true;
  String? _error;
  late String _codeClient;
  final _formKey = GlobalKey<FormState>();
  Map<String, dynamic>? _profile;

  final TextEditingController _adresseCtrl = TextEditingController();
  final TextEditingController _professionCtrl = TextEditingController();
  final TextEditingController _telephoneCtrl = TextEditingController();
  final TextEditingController _photoUrlCtrl = TextEditingController();
  final TextEditingController _cniRectoCtrl = TextEditingController();
  final TextEditingController _cniVersoCtrl = TextEditingController();
  File? _photoFile;
  File? _cniRectoFile;
  File? _cniVersoFile;
  final ImagePicker _picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _init());
  }

  Future<void> _init() async {
    setState(() => _isLoading = true);
    try {
      final args =
          ModalRoute.of(context)?.settings.arguments as Map<String, dynamic>?;
      if (args != null && args['codeClient'] != null) {
        _codeClient = args['codeClient'] as String;
      } else {
        final user = AuthApi.currentUser;
        if (user == null) throw Exception('Utilisateur non connecté');
        // Try to resolve codeClient from login
        _codeClient = await ClientApi.getCodeClientByLogin(user.login);
      }

      final profile = await ClientApi.getClientByCode(_codeClient);
      _profile = profile;
      _adresseCtrl.text = profile['adresse']?.toString() ?? '';
      _professionCtrl.text = profile['profession']?.toString() ?? '';
      _telephoneCtrl.text = profile['telephone']?.toString() ?? '';
      _photoUrlCtrl.text = profile['photoPath']?.toString() ?? '';
      _cniRectoCtrl.text = profile['cniRectoPath']?.toString() ?? '';
      _cniVersoCtrl.text = profile['cniVersoPath']?.toString() ?? '';

      setState(() {
        _error = null;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = ErrorHandler.getDisplayMessage(e.toString());
        _isLoading = false;
      });
    }
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isLoading = true);
    try {
      // 1. Mise à jour des informations textuelles et photo profil (si gérée via updateClient)
      final updated = await ClientApi.updateClient(
        codeClient: _codeClient,
        adresse: _adresseCtrl.text.isEmpty ? null : _adresseCtrl.text,
        profession: _professionCtrl.text.isEmpty ? null : _professionCtrl.text,
        photoPath: _photoUrlCtrl.text.isEmpty ? null : _photoUrlCtrl.text,
      );

      // 2. Upload CNI si les deux images sont sélectionnées
      if (_cniRectoFile != null && _cniVersoFile != null) {
        await ClientApi.uploadCniImages(
          codeClient: _codeClient,
          rectoPath: _cniRectoFile!.path,
          versoPath: _cniVersoFile!.path,
        );
      } else if (_cniRectoFile != null || _cniVersoFile != null) {
        // Avertissement si une seule image est sélectionnée ?
        // On continue pour l'instant
      }

      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Profil mis à jour avec succès')));

      // Update local profile state
      if (updated.isNotEmpty) {
        _profile = updated;
        _photoUrlCtrl.text = updated['photoPath']?.toString() ?? _photoUrlCtrl.text;
      }
      
      // Recharger pour avoir les nouveaux chemins d'images confirmés par le backend
      await _init();

    } catch (e, st) {
      ErrorHandler.logError(e, st);
      await ErrorHandler.showErrorDialog(
        context,
        e.toString(),
        title: 'Erreur mise à jour',
        onRetry: _init,
      );
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  void dispose() {
    _adresseCtrl.dispose();
    _professionCtrl.dispose();
    _telephoneCtrl.dispose();
    _photoUrlCtrl.dispose();
    _cniRectoCtrl.dispose();
    _cniVersoCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mon Profil'),
        backgroundColor: const Color(0xFF0D8A5F),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, color: Colors.red, size: 64),
                  const SizedBox(height: 12),
                  Text(_error ?? 'Erreur'),
                  const SizedBox(height: 12),
                  ElevatedButton(
                    onPressed: _init,
                    child: const Text('Réessayer'),
                  ),
                ],
              ),
            )
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16.0),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Header with avatar
                    Center(
                      child: Column(
                        children: [
                          CircleAvatar(
                            radius: 52,
                            backgroundColor: const Color(0xFFE6F4ED),
                            backgroundImage: _photoFile != null
                                ? FileImage(_photoFile!) as ImageProvider
                                : (_photoUrlCtrl.text.isNotEmpty
                                      ? NetworkImage(_photoUrlCtrl.text)
                                      : null),
                            child:
                                (_photoFile == null &&
                                    _photoUrlCtrl.text.isEmpty)
                                ? const Icon(
                                    Icons.person,
                                    size: 48,
                                    color: Color(0xFF0D8A5F),
                                  )
                                : null,
                          ),
                          const SizedBox(height: 8),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              ElevatedButton.icon(
                                onPressed: () async {
                                  final picked = await _picker.pickImage(
                                    source: ImageSource.gallery,
                                    imageQuality: 80,
                                  );
                                  if (picked != null) {
                                    setState(() {
                                      _photoFile = File(picked.path);
                                      _photoUrlCtrl.text = _photoFile!.path;
                                    });
                                  }
                                },
                                icon: const Icon(Icons.photo_library),
                                label: const Text('Choisir depuis la galerie'),
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFF0D8A5F),
                                  foregroundColor: Colors.white,
                                ),
                              ),
                              const SizedBox(width: 12),
                              OutlinedButton.icon(
                                onPressed: () {
                                  setState(() {
                                    _photoFile = null;
                                    _photoUrlCtrl.text = '';
                                  });
                                },
                                icon: const Icon(Icons.clear),
                                label: const Text('Supprimer'),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 20),

                    const Text(
                      'Informations',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: _adresseCtrl,
                      decoration: const InputDecoration(labelText: 'Adresse'),
                      maxLines: 2,
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: _professionCtrl,
                      decoration: const InputDecoration(
                        labelText: 'Profession',
                      ),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: _telephoneCtrl,
                      decoration: const InputDecoration(labelText: 'Téléphone'),
                      keyboardType: TextInputType.phone,
                    ),
                    const SizedBox(height: 20),

                    const Text(
                      'Documents (CNI)',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              _cniRectoFile != null 
                                ? Image.file(_cniRectoFile!, height: 100, fit: BoxFit.cover)
                                : (_cniRectoCtrl.text.isNotEmpty 
                                    ? Image.network(
                                        _cniRectoCtrl.text, 
                                        height: 100, 
                                        fit: BoxFit.cover,
                                        errorBuilder: (context, error, stackTrace) => 
                                          Container(height: 100, color: Colors.grey[200], child: const Icon(Icons.broken_image)),
                                      )
                                    : Container(
                                        height: 100,
                                        color: Colors.grey[200],
                                        child: const Icon(Icons.image, size: 40, color: Colors.grey),
                                      )
                                  ),
                              const SizedBox(height: 4),
                              ElevatedButton(
                                onPressed: () async {
                                  final picked = await _picker.pickImage(
                                    source: ImageSource.gallery,
                                    imageQuality: 80,
                                  );
                                  if (picked != null) {
                                    setState(() {
                                      _cniRectoFile = File(picked.path);
                                    });
                                  }
                                },
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFF0D8A5F),
                                  foregroundColor: Colors.white,
                                  padding: const EdgeInsets.symmetric(vertical: 8),
                                ),
                                child: const Text('Recto', style: TextStyle(fontSize: 12)),
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              _cniVersoFile != null 
                                ? Image.file(_cniVersoFile!, height: 100, fit: BoxFit.cover)
                                : (_cniVersoCtrl.text.isNotEmpty 
                                    ? Image.network(
                                        _cniVersoCtrl.text, 
                                        height: 100, 
                                        fit: BoxFit.cover,
                                        errorBuilder: (context, error, stackTrace) => 
                                          Container(height: 100, color: Colors.grey[200], child: const Icon(Icons.broken_image)),
                                      )
                                    : Container(
                                        height: 100,
                                        color: Colors.grey[200],
                                        child: const Icon(Icons.image, size: 40, color: Colors.grey),
                                      )
                                  ),
                              const SizedBox(height: 4),
                              ElevatedButton(
                                onPressed: () async {
                                  final picked = await _picker.pickImage(
                                    source: ImageSource.gallery,
                                    imageQuality: 80,
                                  );
                                  if (picked != null) {
                                    setState(() {
                                      _cniVersoFile = File(picked.path);
                                    });
                                  }
                                },
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFF0D8A5F),
                                  foregroundColor: Colors.white,
                                  padding: const EdgeInsets.symmetric(vertical: 8),
                                ),
                                child: const Text('Verso', style: TextStyle(fontSize: 12)),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    if (_cniRectoFile != null || _cniVersoFile != null)
                      Padding(
                        padding: const EdgeInsets.only(top: 8.0),
                        child: Text(
                          'N\'oubliez pas de cliquer sur "Enregistrer" pour envoyer les images.',
                          style: TextStyle(
                            color: Colors.orange[800],
                            fontSize: 12,
                            fontStyle: FontStyle.italic,
                          ),
                        ),
                      ),
                    const SizedBox(height: 20),

                    Row(
                      children: [
                        ElevatedButton(
                          onPressed: _save,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF0D8A5F),
                            padding: const EdgeInsets.symmetric(
                              horizontal: 20,
                              vertical: 12,
                            ),
                          ),
                          child: const Text('Enregistrer'),
                        ),
                        const SizedBox(width: 12),
                        OutlinedButton(
                          onPressed: () => Navigator.pop(context),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: const Color(0xFF0D8A5F),
                            side: const BorderSide(color: Color(0xFF0D8A5F)),
                          ),
                          child: const Text('Fermer'),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}
