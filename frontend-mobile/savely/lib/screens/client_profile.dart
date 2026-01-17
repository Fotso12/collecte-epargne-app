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
      final updated = await ClientApi.updateClient(
        codeClient: _codeClient,
        adresse: _adresseCtrl.text.isEmpty ? null : _adresseCtrl.text,
        profession: _professionCtrl.text.isEmpty ? null : _professionCtrl.text,
        photoPath: _photoUrlCtrl.text.isEmpty ? null : _photoUrlCtrl.text,
        cniRectoPath: _cniRectoCtrl.text.isEmpty ? null : _cniRectoCtrl.text,
        cniVersoPath: _cniVersoCtrl.text.isEmpty ? null : _cniVersoCtrl.text,
      );

      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Profil mis à jour')));

      // Update local profile state with returned data when available
      if (updated.isNotEmpty) {
        _profile = updated;
        _photoUrlCtrl.text =
            updated['photoPath']?.toString() ?? _photoUrlCtrl.text;
      }

      setState(() => _isLoading = false);
    } catch (e, st) {
      // Show raw server message in a dialog to help debugging (will also be logged)
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
                      'Documents',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        Expanded(
                          child: TextFormField(
                            controller: _cniRectoCtrl,
                            decoration: const InputDecoration(
                              labelText: 'CNI Recto / Photo ID (URL)',
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        ElevatedButton(
                          onPressed: () async {
                            final picked = await _picker.pickImage(
                              source: ImageSource.gallery,
                              imageQuality: 80,
                            );
                            if (picked != null) {
                              setState(() {
                                _cniRectoFile = File(picked.path);
                                _cniRectoCtrl.text = _cniRectoFile!.path;
                              });
                            }
                          },
                          child: const Text('Choisir'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF0D8A5F),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        Expanded(
                          child: TextFormField(
                            controller: _cniVersoCtrl,
                            decoration: const InputDecoration(
                              labelText: 'CNI Verso (URL)',
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        ElevatedButton(
                          onPressed: () async {
                            final picked = await _picker.pickImage(
                              source: ImageSource.gallery,
                              imageQuality: 80,
                            );
                            if (picked != null) {
                              setState(() {
                                _cniVersoFile = File(picked.path);
                                _cniVersoCtrl.text = _cniVersoFile!.path;
                              });
                            }
                          },
                          child: const Text('Choisir'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF0D8A5F),
                          ),
                        ),
                      ],
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
