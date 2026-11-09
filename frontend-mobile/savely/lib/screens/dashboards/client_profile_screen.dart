import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:image_picker/image_picker.dart';
import '../../services/client_api.dart';

class ClientProfileScreen extends StatefulWidget {
  final String? codeClient;

  const ClientProfileScreen({super.key, required this.codeClient});

  @override
  State<ClientProfileScreen> createState() => _ClientProfileScreenState();
}

class _ClientProfileScreenState extends State<ClientProfileScreen> {
  final _formKey = GlobalKey<FormState>();
  final _adresseController = TextEditingController();
  final _numCniController = TextEditingController();
  final _lieuNaissanceController = TextEditingController();
  final _dateNaissanceController = TextEditingController();
  final _professionController = TextEditingController();
  
  String? _selectedTypeCni;
  File? _photoFile;
  File? _cniRectoFile;
  File? _cniVersoFile;
  String? _photoPath;
  String? _cniRectoPath;
  String? _cniVersoPath;
  
  bool _isLoading = false;
  bool _isSaving = false;

  final ImagePicker _picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    _loadClientData();
  }

  @override
  void dispose() {
    _adresseController.dispose();
    _numCniController.dispose();
    _lieuNaissanceController.dispose();
    _dateNaissanceController.dispose();
    _professionController.dispose();
    super.dispose();
  }

  Future<void> _loadClientData() async {
    if (widget.codeClient == null) return;

    setState(() {
      _isLoading = true;
    });

    try {
      final data = await ClientApi.getClientByCode(widget.codeClient!);
      final typeCniValue = data['typeCni']?.toString();
      // S'assurer que la valeur correspond à l'une des valeurs valides de l'enum
      final validTypeCniValues = ['CARTE_IDENTITE', 'PASSEPORT', 'PERMIS_CONDUIRE'];
      final selectedTypeCni = validTypeCniValues.contains(typeCniValue) ? typeCniValue : null;
      
      setState(() {
        _adresseController.text = data['adresse']?.toString() ?? '';
        _numCniController.text = data['numCni']?.toString() ?? '';
        _lieuNaissanceController.text = data['lieuNaissance']?.toString() ?? '';
        _dateNaissanceController.text = data['dateNaissance']?.toString() ?? '';
        _professionController.text = data['profession']?.toString() ?? '';
        _selectedTypeCni = selectedTypeCni;
        _photoPath = data['photoPath']?.toString();
        _cniRectoPath = data['cniRectoPath']?.toString();
        _cniVersoPath = data['cniVersoPath']?.toString();
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Erreur: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _pickImage(ImageSource source, String type) async {
    try {
      // Vérifier si on est sur web ou desktop (image_picker a des limitations)
      if (kIsWeb || Platform.isWindows || Platform.isLinux || Platform.isMacOS) {
        // Pour les plateformes web/desktop, image_picker ne fonctionne pas bien
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('La sélection d\'images n\'est disponible que sur Android et iOS.\n'
                  'Veuillez utiliser l\'application mobile pour cette fonctionnalité.'),
              backgroundColor: Colors.orange,
              duration: Duration(seconds: 5),
            ),
          );
        }
        return;
      }

      XFile? image;
      
      // Gérer différemment selon la source
      if (source == ImageSource.camera) {
        image = await _picker.pickImage(
          source: ImageSource.camera,
          imageQuality: 80,
          maxWidth: 1920,
          maxHeight: 1080,
        );
      } else {
        image = await _picker.pickImage(
          source: ImageSource.gallery,
          imageQuality: 80,
          maxWidth: 1920,
          maxHeight: 1080,
        );
      }
      
      if (image != null && image.path.isNotEmpty) {
        final imagePath = image.path;
        setState(() {
          if (type == 'photo') {
            _photoFile = File(imagePath);
            _photoPath = imagePath; // Pour l'instant, on stocke le chemin local
            // TODO: Uploader le fichier vers le serveur et stocker l'URL
          } else if (type == 'cniRecto') {
            _cniRectoFile = File(imagePath);
            _cniRectoPath = imagePath;
          } else if (type == 'cniVerso') {
            _cniVersoFile = File(imagePath);
            _cniVersoPath = imagePath;
          }
        });
        
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Image sélectionnée avec succès'),
              backgroundColor: Colors.green,
              duration: Duration(seconds: 2),
            ),
          );
        }
      }
    } catch (e) {
      if (mounted) {
        String errorMessage = 'Erreur lors de la sélection de l\'image';
        if (e.toString().contains('MissingPluginException')) {
          errorMessage = 'Erreur: Le plugin image_picker n\'est pas correctement configuré.\n\n'
              'Solutions:\n'
              '1. Arrêtez complètement l\'application\n'
              '2. Exécutez: flutter clean\n'
              '3. Exécutez: flutter pub get\n'
              '4. Reconstruisez l\'application: flutter run\n\n'
              'Si le problème persiste, vérifiez que vous utilisez une plateforme mobile (Android/iOS) et non web.';
        } else {
          errorMessage = 'Erreur: $e';
        }
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(errorMessage),
            backgroundColor: Colors.red,
            duration: const Duration(seconds: 8),
          ),
        );
      }
    }
  }

  Future<void> _saveProfile() async {
    if (widget.codeClient == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Erreur: Code client non disponible'),
          backgroundColor: Colors.red,
        ),
      );
      return;
    }

    // Valider le formulaire
    if (!_formKey.currentState!.validate()) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Veuillez corriger les erreurs dans le formulaire'),
          backgroundColor: Colors.orange,
        ),
      );
      return;
    }

    setState(() {
      _isSaving = true;
    });

    try {
      // TODO: Uploader les fichiers vers le serveur si _photoFile, _cniRectoFile, _cniVersoFile ne sont pas null
      // Pour l'instant, on envoie seulement les chemins locaux
      
      await ClientApi.updateClient(
        codeClient: widget.codeClient!,
        adresse: _adresseController.text.trim().isEmpty ? null : _adresseController.text.trim(),
        typeCni: _selectedTypeCni,
        numCni: _numCniController.text.trim().isEmpty ? null : _numCniController.text.trim(),
        dateNaissance: _dateNaissanceController.text.trim().isEmpty ? null : _dateNaissanceController.text.trim(),
        lieuNaissance: _lieuNaissanceController.text.trim().isEmpty ? null : _lieuNaissanceController.text.trim(),
        profession: _professionController.text.trim().isEmpty ? null : _professionController.text.trim(),
        photoPath: _photoPath,
        cniRectoPath: _cniRectoPath,
        cniVersoPath: _cniVersoPath,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Profil mis à jour avec succès'),
            backgroundColor: Colors.green,
            duration: Duration(seconds: 3),
          ),
        );
        
        // Attendre un peu avant de revenir en arrière pour que l'utilisateur voie le message
        await Future.delayed(const Duration(milliseconds: 500));
        
        if (mounted) {
          Navigator.pop(context, true); // Retour avec indication de mise à jour
        }
      }
    } catch (e) {
      setState(() {
        _isSaving = false;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Erreur lors de la sauvegarde: $e'),
            backgroundColor: Colors.red,
            duration: const Duration(seconds: 4),
          ),
        );
      }
    }
  }

  void _removeImage(String type) {
    setState(() {
      if (type == 'photo') {
        _photoFile = null;
        _photoPath = null;
      } else if (type == 'cniRecto') {
        _cniRectoFile = null;
        _cniRectoPath = null;
      } else if (type == 'cniVerso') {
        _cniVersoFile = null;
        _cniVersoPath = null;
      }
    });
  }

  Widget _buildImagePicker(String title, String type, File? file, String? path) {
    final hasImage = file != null || path != null;
    
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  title,
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                ),
                if (hasImage)
                  IconButton(
                    icon: const Icon(Icons.delete, color: Colors.red),
                    onPressed: () => _removeImage(type),
                    tooltip: 'Supprimer l\'image',
                  ),
              ],
            ),
            const SizedBox(height: 12),
            if (hasImage)
              Stack(
                children: [
                  Container(
                    height: 150,
                    width: double.infinity,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: Colors.grey[300]!),
                    ),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: file != null
                          ? Image.file(file, fit: BoxFit.cover)
                          : path != null
                              ? Image.network(
                                  path,
                                  fit: BoxFit.cover,
                                  errorBuilder: (context, error, stackTrace) {
                                    return const Center(
                                      child: Icon(Icons.broken_image, size: 50, color: Colors.grey),
                                    );
                                  },
                                  loadingBuilder: (context, child, loadingProgress) {
                                    if (loadingProgress == null) return child;
                                    return const Center(
                                      child: CircularProgressIndicator(),
                                    );
                                  },
                                )
                              : const SizedBox(),
                    ),
                  ),
                ],
              )
            else
              Container(
                height: 150,
                width: double.infinity,
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.grey[300]!, style: BorderStyle.solid),
                ),
                child: const Center(
                  child: Icon(Icons.add_photo_alternate, size: 50, color: Colors.grey),
                ),
              ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: () => _pickImage(ImageSource.camera, type),
                    icon: const Icon(Icons.camera_alt),
                    label: const Text('Caméra'),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 12),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: () => _pickImage(ImageSource.gallery, type),
                    icon: const Icon(Icons.photo_library),
                    label: const Text('Galerie'),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 12),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mon Profil'),
        actions: [
          if (_isSaving)
            const Padding(
              padding: EdgeInsets.all(16),
              child: SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(strokeWidth: 2),
              ),
            )
          else
            IconButton(
              icon: const Icon(Icons.save),
              onPressed: _saveProfile,
              tooltip: 'Enregistrer',
            ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Photo de profil
                    _buildImagePicker('Photo de profil', 'photo', _photoFile, _photoPath),
                    const SizedBox(height: 24),
                    
                    // Informations personnelles
                    Card(
                      elevation: 2,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              'Informations personnelles',
                              style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _adresseController,
                              decoration: InputDecoration(
                                labelText: 'Adresse',
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                                prefixIcon: const Icon(Icons.location_on),
                              ),
                              maxLines: 2,
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _dateNaissanceController,
                              decoration: InputDecoration(
                                labelText: 'Date de naissance',
                                hintText: 'YYYY-MM-DD',
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                                prefixIcon: const Icon(Icons.calendar_today),
                                suffixIcon: IconButton(
                                  icon: const Icon(Icons.calendar_month),
                                  onPressed: () async {
                                    final DateTime? picked = await showDatePicker(
                                      context: context,
                                      initialDate: _dateNaissanceController.text.isNotEmpty
                                          ? DateTime.tryParse(_dateNaissanceController.text) ?? DateTime.now()
                                          : DateTime.now(),
                                      firstDate: DateTime(1900),
                                      lastDate: DateTime.now(),
                                    );
                                    if (picked != null) {
                                      _dateNaissanceController.text = 
                                          '${picked.year}-${picked.month.toString().padLeft(2, '0')}-${picked.day.toString().padLeft(2, '0')}';
                                    }
                                  },
                                  tooltip: 'Sélectionner une date',
                                ),
                              ),
                              readOnly: true,
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _lieuNaissanceController,
                              decoration: InputDecoration(
                                labelText: 'Lieu de naissance',
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                                prefixIcon: const Icon(Icons.place),
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _professionController,
                              decoration: InputDecoration(
                                labelText: 'Profession',
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                                prefixIcon: const Icon(Icons.work),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                    
                    // Documents d'identité
                    Card(
                      elevation: 2,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              'Documents d\'identité',
                              style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                            ),
                            const SizedBox(height: 16),
                            DropdownButtonFormField<String>(
                              initialValue: _selectedTypeCni,
                              decoration: InputDecoration(
                                labelText: 'Type de CNI',
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                                prefixIcon: const Icon(Icons.badge),
                              ),
                              items: const [
                                DropdownMenuItem(value: 'CARTE_IDENTITE', child: Text('Carte d\'identité')),
                                DropdownMenuItem(value: 'PASSEPORT', child: Text('Passeport')),
                                DropdownMenuItem(value: 'PERMIS_CONDUIRE', child: Text('Permis de conduire')),
                              ],
                              onChanged: (value) {
                                setState(() {
                                  _selectedTypeCni = value;
                                });
                              },
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _numCniController,
                              decoration: InputDecoration(
                                labelText: 'Numéro de CNI',
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                                prefixIcon: const Icon(Icons.credit_card),
                              ),
                            ),
                            const SizedBox(height: 24),
                            _buildImagePicker('CNI Recto', 'cniRecto', _cniRectoFile, _cniRectoPath),
                            const SizedBox(height: 16),
                            _buildImagePicker('CNI Verso', 'cniVerso', _cniVersoFile, _cniVersoPath),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                    
                    // Bouton de sauvegarde
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        onPressed: _isSaving ? null : _saveProfile,
                        icon: const Icon(Icons.save),
                        label: const Text('Enregistrer les modifications'),
                        style: ElevatedButton.styleFrom(
                          padding: const EdgeInsets.symmetric(vertical: 16),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}

