import 'dart:io';

import 'package:image_picker/image_picker.dart';
import 'package:flutter/material.dart';
import '../services/collecteur_api.dart';
import '../services/utilisateur_api.dart';
import '../services/auth_api.dart';
import '../services/error_handler.dart';

class CollecteurProfile extends StatefulWidget {
  const CollecteurProfile({super.key});

  @override
  State<CollecteurProfile> createState() => _CollecteurProfileState();
}

class _CollecteurProfileState extends State<CollecteurProfile> {
  bool _isLoading = true;
  String? _error;
  Map<String, dynamic>? _profile;
  final TextEditingController _photoCtrl = TextEditingController();
  final TextEditingController _prenomCtrl = TextEditingController();
  final TextEditingController _nomCtrl = TextEditingController();
  final TextEditingController _emailCtrl = TextEditingController();
  final TextEditingController _telephoneCtrl = TextEditingController();
  File? _photoFile;
  final ImagePicker _picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _init());
  }

  @override
  void dispose() {
    _photoCtrl.dispose();
    _prenomCtrl.dispose();
    _nomCtrl.dispose();
    _emailCtrl.dispose();
    _telephoneCtrl.dispose();
    super.dispose();
  }

  Future<void> _init() async {
    setState(() => _isLoading = true);
    try {
      final args =
          ModalRoute.of(context)?.settings.arguments as Map<String, dynamic>?;
      String id = '';
      if (args != null && args['matricule'] != null) {
        id = args['matricule'] as String;
      } else {
        final user = AuthApi.currentUser;
        if (user == null) throw Exception('Utilisateur non connecté');
        id = user.login;
      }

      final dynamic profileModel = await CollecteurApi.getProfile(id);
      Map<String, dynamic>? resolved;
      if (profileModel == null) {
        resolved = null;
      } else if (profileModel is Map<String, dynamic>) {
        resolved = profileModel;
      } else {
        try {
          // Try to call toJson() if available and ensure it's a Map
          final dynamic json = (profileModel as dynamic).toJson();
          if (json is Map<String, dynamic>) {
            resolved = json;
          } else {
            resolved = null;
          }
        } catch (_) {
          resolved = null;
        }
      }

      setState(() {
        _profile = resolved;
        _photoCtrl.text = resolved?['photoPath']?.toString() ?? '';
        _prenomCtrl.text = resolved?['prenom']?.toString() ?? '';
        _nomCtrl.text = resolved?['nom']?.toString() ?? '';
        // Try nested utilisateur
        _emailCtrl.text =
            resolved?['email']?.toString() ??
            (resolved?['utilisateur']?['email']?.toString() ?? '');
        _telephoneCtrl.text = resolved?['telephone']?.toString() ?? '';
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = ErrorHandler.getDisplayMessage(e.toString());
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profil Collecteur'),
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
              child: _profile == null
                  ? const Text('Profil introuvable')
                  : Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Center(
                          child: Column(
                            children: [
                              CircleAvatar(
                                radius: 56,
                                backgroundColor: const Color(0xFFEFF7F1),
                                backgroundImage: _photoFile != null
                                    ? FileImage(_photoFile!) as ImageProvider
                                    : (_photoCtrl.text.isNotEmpty
                                          ? NetworkImage(_photoCtrl.text)
                                          : null),
                                child:
                                    (_photoFile == null &&
                                        _photoCtrl.text.isEmpty)
                                    ? const Icon(
                                        Icons.person,
                                        size: 48,
                                        color: Color(0xFF0D8A5F),
                                      )
                                    : null,
                              ),
                              const SizedBox(height: 8),
                              Text(
                                '${_profile!['prenom'] ?? ''} ${_profile!['nom'] ?? ''}',
                                style: const TextStyle(
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                'Matricule: ${_profile!['matricule'] ?? ''}',
                              ),
                              const SizedBox(height: 12),
                              Text('Email: ${_profile!['email'] ?? ''}'),
                              const SizedBox(height: 4),
                              Text(
                                'Téléphone: ${_profile!['telephone'] ?? ''}',
                              ),
                              const SizedBox(height: 12),
                              ElevatedButton.icon(
                                onPressed: () async {
                                  final picked = await _picker.pickImage(
                                    source: ImageSource.gallery,
                                    imageQuality: 80,
                                  );
                                  if (picked != null) {
                                    setState(() {
                                      _photoFile = File(picked.path);
                                      _photoCtrl.text = _photoFile!.path;
                                    });
                                  }
                                },
                                icon: const Icon(Icons.camera_alt),
                                label: const Text('Modifier la photo'),
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFF0D8A5F),
                                  foregroundColor: Colors.white,
                                ),
                              ),
                              const SizedBox(height: 12),
                              // Editable fields for collecteur
                              Padding(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 8.0,
                                ),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    TextFormField(
                                      controller: _prenomCtrl,
                                      decoration: const InputDecoration(
                                        labelText: 'Prénom',
                                      ),
                                    ),
                                    const SizedBox(height: 8),
                                    TextFormField(
                                      controller: _nomCtrl,
                                      decoration: const InputDecoration(
                                        labelText: 'Nom',
                                      ),
                                    ),
                                    const SizedBox(height: 8),
                                    TextFormField(
                                      controller: _emailCtrl,
                                      decoration: const InputDecoration(
                                        labelText: 'Email',
                                      ),
                                    ),
                                    const SizedBox(height: 8),
                                    TextFormField(
                                      controller: _telephoneCtrl,
                                      decoration: const InputDecoration(
                                        labelText: 'Téléphone',
                                      ),
                                      keyboardType: TextInputType.phone,
                                    ),
                                    const SizedBox(height: 12),
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      children: [
                                        ElevatedButton(
                                          onPressed: () async {
                                            // Try to find login to update
                                            String? login;
                                            if (_profile != null) {
                                              login =
                                                  _profile?['login']
                                                      ?.toString() ??
                                                  _profile?['utilisateur']?['login']
                                                      ?.toString();
                                            }
                                            if (login == null ||
                                                login.isEmpty) {
                                              // Fallback to currently authenticated user's login
                                              final cu = AuthApi.currentUser;
                                              login = cu?.login;
                                            }
                                            if (login == null ||
                                                login.isEmpty) {
                                              ScaffoldMessenger.of(
                                                context,
                                              ).showSnackBar(
                                                const SnackBar(
                                                  content: Text(
                                                    'Impossible de déterminer le login utilisateur',
                                                  ),
                                                ),
                                              );
                                              return;
                                            }
                                            try {
                                              setState(() => _isLoading = true);
                                              await UtilisateurApi.updateUtilisateur(
                                                login: login,
                                                nom: _nomCtrl.text.isEmpty
                                                    ? null
                                                    : _nomCtrl.text,
                                                prenom: _prenomCtrl.text.isEmpty
                                                    ? null
                                                    : _prenomCtrl.text,
                                                email: _emailCtrl.text.isEmpty
                                                    ? null
                                                    : _emailCtrl.text,
                                                telephone:
                                                    _telephoneCtrl.text.isEmpty
                                                    ? null
                                                    : _telephoneCtrl.text,
                                                photoPath:
                                                    _photoCtrl.text.isEmpty
                                                    ? null
                                                    : _photoCtrl.text,
                                              );
                                              ScaffoldMessenger.of(
                                                context,
                                              ).showSnackBar(
                                                const SnackBar(
                                                  content: Text(
                                                    'Profil mis à jour',
                                                  ),
                                                ),
                                              );
                                              // Refresh
                                              await _init();
                                            } catch (e) {
                                              setState(() {
                                                _error = e.toString();
                                                _isLoading = false;
                                              });
                                            }
                                          },
                                          child: const Text('Enregistrer'),
                                          style: ElevatedButton.styleFrom(
                                            backgroundColor: const Color(
                                              0xFF0D8A5F,
                                            ),
                                          ),
                                        ),
                                        const SizedBox(width: 12),
                                        OutlinedButton(
                                          onPressed: () =>
                                              Navigator.pop(context),
                                          child: const Text('Fermer'),
                                        ),
                                      ],
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(height: 20),
                        OutlinedButton(
                          onPressed: () => Navigator.pop(context),
                          child: const Text('Fermer'),
                        ),
                      ],
                    ),
            ),
    );
  }
}
