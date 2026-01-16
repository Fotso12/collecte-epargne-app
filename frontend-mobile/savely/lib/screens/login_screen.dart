import 'package:flutter/material.dart';
import '../services/auth_api.dart';
import '../services/error_handler.dart';
import '../models/user_model.dart';
import 'register_screen.dart';
import 'collecteur_dashboard.dart';
import 'client_dashboard.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({Key? key}) : super(key: key);

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (_emailController.text.isEmpty || _passwordController.text.isEmpty) {
      setState(() => _errorMessage = 'Veuillez remplir tous les champs');
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final result = await AuthApi.login(
        email: _emailController.text,
        password: _passwordController.text,
      );

      if (!mounted) return;

      if (result['success'] as bool) {
        final user = result['user'] as UserModel?;
        if (user != null) {
          // Redirection basée sur le rôle
          if (user.role == 'COLLECTEUR') {
            Navigator.of(context).pushReplacementNamed('/collecteur-dashboard');
          } else if (user.role == 'CLIENT') {
            Navigator.of(context).pushReplacementNamed('/client-dashboard');
          } else if (user.role == 'CAISSIER' || user.role == 'SUPERVISEUR') {
            // Redirection vers web
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text(
                  'Veuillez utiliser l\'interface web pour accéder au dashboard',
                ),
              ),
            );
          }
        }
      } else {
        setState(
          () => _errorMessage = result['message'] ?? 'Erreur de connexion',
        );
      }
    } catch (e) {
      setState(
        () => _errorMessage = ErrorHandler.getDisplayMessage(e.toString()),
      );
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const SizedBox(height: 40),

              // Logo Savely - Coeur vert avec texte
              Container(
                width: 120,
                height: 120,
                decoration: BoxDecoration(
                  color: const Color(0xFF0D8A5F),
                  shape: BoxShape.circle,
                ),
                child: const Center(
                  child: Icon(Icons.favorite, size: 80, color: Colors.white),
                ),
              ),
              const SizedBox(height: 20),

              // Titre
              const Text(
                'SAVELY',
                style: TextStyle(
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF0D8A5F),
                ),
              ),
              const Text(
                'Plateforme de Collecte d\'Épargne',
                style: TextStyle(fontSize: 14, color: Colors.grey),
              ),
              const SizedBox(height: 40),

              // Message d'erreur
              if (_errorMessage != null)
                Container(
                  padding: const EdgeInsets.all(12),
                  margin: const EdgeInsets.only(bottom: 16),
                  decoration: BoxDecoration(
                    color: Colors.red.shade50,
                    border: Border.all(color: Colors.red.shade300),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    _errorMessage!,
                    style: TextStyle(color: Colors.red.shade700, fontSize: 14),
                  ),
                ),

              // Champ email
              TextField(
                controller: _emailController,
                enabled: !_isLoading,
                decoration: InputDecoration(
                  labelText: 'Email',
                  hintText: 'exemple@email.com',
                  prefixIcon: const Icon(Icons.email),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 12,
                  ),
                ),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 16),

              // Champ mot de passe
              TextField(
                controller: _passwordController,
                enabled: !_isLoading,
                obscureText: _obscurePassword,
                decoration: InputDecoration(
                  labelText: 'Mot de passe',
                  hintText: 'Entrez votre mot de passe',
                  prefixIcon: const Icon(Icons.lock),
                  suffixIcon: IconButton(
                    icon: Icon(
                      _obscurePassword
                          ? Icons.visibility_off
                          : Icons.visibility,
                    ),
                    onPressed: () {
                      setState(() => _obscurePassword = !_obscurePassword);
                    },
                  ),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 12,
                  ),
                ),
              ),
              const SizedBox(height: 24),

              // Bouton connexion
              ElevatedButton(
                onPressed: _isLoading ? null : _handleLogin,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF0D8A5F),
                  foregroundColor: Colors.white,
                  minimumSize: const Size(double.infinity, 48),
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
                          valueColor: AlwaysStoppedAnimation<Color>(
                            Colors.white,
                          ),
                        ),
                      )
                    : const Text(
                        'Se connecter',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
              ),
              const SizedBox(height: 16),

              // Lien inscription
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    'Pas encore de compte ? ',
                    style: TextStyle(color: Colors.grey),
                  ),
                  TextButton(
                    onPressed: _isLoading
                        ? null
                        : () {
                            Navigator.of(context).push(
                              MaterialPageRoute(
                                builder: (context) => const RegisterScreen(),
                              ),
                            );
                          },
                    child: const Text(
                      'S\'inscrire',
                      style: TextStyle(
                        color: Color(0xFF0D8A5F),
                        fontWeight: FontWeight.bold,
                      ),
                    ),
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
