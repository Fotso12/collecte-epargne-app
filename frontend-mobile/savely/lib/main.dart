import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'services/auth_api.dart';
import 'models/user_model.dart';
import 'screens/dashboards/admin_dashboard.dart';
import 'screens/dashboards/client_dashboard.dart';
import 'screens/dashboards/collecteur_dashboard.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final seed = const Color(0xFF0D8A5F);
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Collecte Épargne',
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: seed),
        scaffoldBackgroundColor: const Color(0xFFF6F8FB),
        inputDecorationTheme: InputDecorationTheme(
          filled: true,
          fillColor: Colors.white,
          contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(14),
            borderSide: const BorderSide(color: Color(0xFFE3E6EC)),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(14),
            borderSide: const BorderSide(color: Color(0xFFE3E6EC)),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(14),
            borderSide: BorderSide(color: seed, width: 1.4),
          ),
        ),
        textTheme: const TextTheme(
          headlineMedium: TextStyle(fontWeight: FontWeight.w700, fontSize: 24),
          bodyLarge: TextStyle(height: 1.4),
          bodyMedium: TextStyle(height: 1.45),
        ),
      ),
      home: const AuthScreen(),
    );
  }
}

class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key});

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  final _formKey = GlobalKey<FormState>();
  final _lastName = TextEditingController();
  final _firstName = TextEditingController();
  final _email = TextEditingController();
  final _phone = TextEditingController();
  final _password = TextEditingController();
  final _confirmPassword = TextEditingController();
  final _collectorMatricule = TextEditingController(text: '0000'); // Par défaut "0000"
  bool _isLogin = true;
  bool _obscurePassword = true;
  bool _isSubmitting = false;

  @override
  void dispose() {
    _lastName.dispose();
    _firstName.dispose();
    _email.dispose();
    _phone.dispose();
    _password.dispose();
    _confirmPassword.dispose();
    _collectorMatricule.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    // Plus besoin de charger les rôles car l'inscription est uniquement pour les clients
}

  void _toggleMode(bool isLogin) {
    setState(() {
      _isLogin = isLogin;
      // Réinitialiser les contrôleurs manuellement avant de réinitialiser le formulaire
      _lastName.clear();
      _firstName.clear();
      _email.clear();
      _phone.clear();
      _password.clear();
      _confirmPassword.clear();
      if (!isLogin) {
        // Mode inscription : réinitialiser le matricule à "0000"
        _collectorMatricule.text = '0000';
      } else {
        _collectorMatricule.clear();
      }
      // Réinitialiser le formulaire
      _formKey.currentState?.reset();
    });
  }

  Future<void> _submit() async {
    if (!(_formKey.currentState?.validate() ?? false)) return;

    setState(() => _isSubmitting = true);

    try {
      if (_isLogin) {
        // Connexion
        final user = await AuthApi.login(
          email: _email.text.trim(),
          password: _password.text.trim(),
        );
        
        if (!mounted) return;
        
        // Navigation vers le dashboard approprié
        _navigateToDashboard(user);
      } else {
        // Inscription CLIENT uniquement
        final fullName = '${_firstName.text.trim()} ${_lastName.text.trim()}';
        final matricule = _collectorMatricule.text.trim().isEmpty 
            ? '0000' 
            : _collectorMatricule.text.trim();
        
        // Inscription CLIENT avec email et mot de passe
        await AuthApi.registerClient(
          fullName: fullName,
          phone: _phone.text.trim(),
          email: _email.text.trim(),
          password: _password.text.trim(),
          collectorMatricule: matricule,
        );
        
        if (!mounted) return;
        
        // Connexion automatique après inscription
        final user = await AuthApi.login(
          email: _email.text.trim(),
          password: _password.text.trim(),
        );
        
        if (!mounted) return;
        
        // Navigation vers le dashboard client
        _navigateToDashboard(user);
      }
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erreur: $e')),
      );
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  void _navigateToDashboard(UserModel user) {
    Widget dashboard;
    
    switch (user.codeRole.toLowerCase()) {
      case 'admin':
        dashboard = AdminDashboard(user: user);
        break;
      case 'client':
        dashboard = ClientDashboard(user: user);
        break;
      case 'collector':
        dashboard = CollecteurDashboard(user: user);
        break;
      case 'supervisor':
      case 'auditor':
        // Pour l'instant, rediriger vers admin dashboard
        dashboard = AdminDashboard(user: user);
        break;
      default:
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Rôle non reconnu: ${user.codeRole}')),
        );
        return;
    }
    
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (context) => dashboard),
    );
  }

  @override
  Widget build(BuildContext context) {
    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: SystemUiOverlayStyle.dark.copyWith(statusBarColor: Colors.transparent),
      child: Scaffold(
        body: LayoutBuilder(
          builder: (context, constraints) {
            final isWide = constraints.maxWidth >= 900;
            final isTablet = constraints.maxWidth >= 600 && constraints.maxWidth < 900;
            final isPhone = constraints.maxWidth < 520;
            final cardWidth = isWide
                ? 420.0
                : isPhone
                    ? constraints.maxWidth * 0.98
                    : constraints.maxWidth * 0.9;

            final form = Center(
              child: ConstrainedBox(
                constraints: BoxConstraints(maxWidth: cardWidth),
                child: Card(
                  elevation: 12,
                  shadowColor: Colors.black12,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(22)),
                  child: Padding(
                    padding: EdgeInsets.symmetric(
                      horizontal: isPhone ? 18 : 26,
                      vertical: isPhone ? 22 : 30,
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    _isLogin ? 'Heureux de vous revoir' : 'Créer un compte',
                                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                                          fontSize: isPhone ? 22 : 24,
                                        ),
                                  ),
                                  const SizedBox(height: 6),
                                  Text(
                                    _isLogin
                                        ? 'Reprenez vos objectifs d’épargne en un clic.'
                                        : 'Accédez à la collecte sécurisée et automatisée.',
                                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                          color: Colors.grey[700],
                                          fontSize: isPhone ? 13 : 14,
                                        ),
                                  ),
                                ],
                              ),
                            ),
                            const SizedBox(width: 12),
                            Container(
                              padding: EdgeInsets.all(isPhone ? 8 : 10),
                              decoration: BoxDecoration(
                                color: Theme.of(context).colorScheme.primary.withOpacity(0.08),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: Icon(
                                _isLogin ? Icons.login_rounded : Icons.person_add_rounded,
                                color: Theme.of(context).colorScheme.primary,
                                size: isPhone ? 20 : 22,
                              ),
                            ),
                          ],
                        ),
                        SizedBox(height: isPhone ? 14 : 20),
                        SegmentedButton<bool>(
                          segments: const [
                            ButtonSegment<bool>(value: true, label: Text('Connexion')),
                            ButtonSegment<bool>(value: false, label: Text('Inscription')),
                          ],
                          selected: {_isLogin},
                          onSelectionChanged: (s) => _toggleMode(s.first),
                          style: ButtonStyle(
                            visualDensity: isPhone ? VisualDensity.standard : VisualDensity.compact,
                            shape: WidgetStateProperty.all(
                              RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                            ),
                          ),
                          showSelectedIcon: false,
                        ),
                        SizedBox(height: isPhone ? 16 : 22),
                        Form(
                          key: _formKey,
                          child: Column(
                            children: [
                              if (!_isLogin) ...[
                                Row(
                                  children: [
                                    Expanded(
                                      child: TextFormField(
                                        controller: _lastName,
                                        textCapitalization: TextCapitalization.words,
                                        decoration: const InputDecoration(
                                          labelText: 'Nom',
                                          prefixIcon: Icon(Icons.person_outline),
                                        ),
                                        validator: (v) => (v == null || v.trim().isEmpty) ? 'Nom requis' : null,
                                      ),
                                    ),
                                    const SizedBox(width: 12),
                                    Expanded(
                                      child: TextFormField(
                                        controller: _firstName,
                                        textCapitalization: TextCapitalization.words,
                                        decoration: const InputDecoration(
                                          labelText: 'Prénom',
                                          prefixIcon: Icon(Icons.person_outline),
                                        ),
                                        validator: (v) => (v == null || v.trim().isEmpty) ? 'Prénom requis' : null,
                                      ),
                                    ),
                                  ],
                                ),
                                SizedBox(height: isPhone ? 12 : 14),
                              ],
                              if (_isLogin) ...[
                                TextFormField(
                                  controller: _email,
                                  keyboardType: TextInputType.emailAddress,
                                  decoration: const InputDecoration(
                                    labelText: 'Email professionnel',
                                    prefixIcon: Icon(Icons.mail_outlined),
                                  ),
                                  validator: (v) {
                                    if (v == null || v.isEmpty) return 'Email requis';
                                    final valid = RegExp(r'^[^@]+@[^@]+\.[^@]+').hasMatch(v);
                                    return valid ? null : 'Email invalide';
                                  },
                                ),
                                const SizedBox(height: 14),
                              ] else ...[
                                TextFormField(
                                  controller: _email,
                                  keyboardType: TextInputType.emailAddress,
                                  decoration: const InputDecoration(
                                    labelText: 'Email',
                                    prefixIcon: Icon(Icons.mail_outlined),
                                  ),
                                  validator: (v) {
                                    if (v == null || v.isEmpty) return 'Email requis';
                                    final valid = RegExp(r'^[^@]+@[^@]+\.[^@]+').hasMatch(v);
                                    return valid ? null : 'Email invalide';
                                  },
                                ),
                                const SizedBox(height: 14),
                                TextFormField(
                                  controller: _phone,
                                  keyboardType: TextInputType.phone,
                                  decoration: const InputDecoration(
                                    labelText: 'Téléphone',
                                    prefixIcon: Icon(Icons.phone_outlined),
                                  ),
                                  validator: (v) {
                                    if (v == null || v.isEmpty) return 'Téléphone requis';
                                    if (v.length < 8) return 'Numéro trop court';
                                    return null;
                                  },
                                ),
                                SizedBox(height: isPhone ? 12 : 14),
                                TextFormField(
                                  controller: _password,
                                  obscureText: _obscurePassword,
                                  decoration: InputDecoration(
                                    labelText: 'Mot de passe',
                                    prefixIcon: const Icon(Icons.lock_outline),
                                    suffixIcon: IconButton(
                                      onPressed: () => setState(() => _obscurePassword = !_obscurePassword),
                                      icon: Icon(_obscurePassword ? Icons.visibility_off : Icons.visibility),
                                    ),
                                  ),
                                  validator: (v) {
                                    if (v == null || v.isEmpty) return 'Mot de passe requis';
                                    if (v.length < 6) return 'Minimum 6 caractères';
                                    return null;
                                  },
                                ),
                                SizedBox(height: isPhone ? 12 : 14),
                                TextFormField(
                                  controller: _confirmPassword,
                                  obscureText: _obscurePassword,
                                  decoration: const InputDecoration(
                                    labelText: 'Confirmer le mot de passe',
                                    prefixIcon: Icon(Icons.verified_user_outlined),
                                  ),
                                  validator: (v) {
                                    if (v == null || v.isEmpty) return 'Confirmation requise';
                                    if (v != _password.text) return 'Les mots de passe diffèrent';
                                    return null;
                                  },
                                ),
                                SizedBox(height: isPhone ? 12 : 14),
                                TextFormField(
                                  controller: _collectorMatricule,
                                  keyboardType: TextInputType.text,
                                  decoration: const InputDecoration(
                                    labelText: 'Matricule du collecteur parrain',
                                    hintText: '0000 si pas de parrain',
                                    prefixIcon: Icon(Icons.badge_outlined),
                                    helperText: 'Entrez "0000" si vous n\'êtes pas parrainé par un collecteur',
                                  ),
                                  validator: (v) {
                                    if (v == null || v.trim().isEmpty) {
                                      return 'Veuillez entrer un matricule ou "0000"';
                                    }
                                    return null;
                                  },
                                ),
                                SizedBox(height: isPhone ? 12 : 14),
                              ],
                              if (_isLogin) ...[
                                TextFormField(
                                  controller: _password,
                                  obscureText: _obscurePassword,
                                  decoration: InputDecoration(
                                    labelText: 'Mot de passe',
                                    prefixIcon: const Icon(Icons.lock_outline),
                                    suffixIcon: IconButton(
                                      onPressed: () => setState(() => _obscurePassword = !_obscurePassword),
                                      icon: Icon(_obscurePassword ? Icons.visibility_off : Icons.visibility),
                                    ),
                                  ),
                                  validator: (v) {
                                    if (v == null || v.isEmpty) return 'Mot de passe requis';
                                    if (v.length < 8) return 'Minimum 8 caractères';
                                    return null;
                                  },
                                ),
                              ],
                              SizedBox(height: isPhone ? 14 : 18),
                              Row(
                                children: [
                                  Checkbox(
                                    value: true,
                                    onChanged: (_) {},
                                    activeColor: Theme.of(context).colorScheme.primary,
                                  ),
                                  Flexible(
                                    child: Text(
                                      'Sécurisation des fonds par double contrôle et notifications temps réel.',
                                      style: Theme.of(context).textTheme.bodySmall,
                                    ),
                                  ),
                                ],
                              ),
                              SizedBox(height: isPhone ? 14 : 18),
                              SizedBox(
                                width: double.infinity,
                                child: FilledButton(
                                  onPressed: _isSubmitting ? null : _submit,
                                  style: FilledButton.styleFrom(
                                    padding: const EdgeInsets.symmetric(vertical: 14),
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(14),
                                    ),
                                  ),
                                  child: _isSubmitting
                                      ? const SizedBox(
                                          height: 18,
                                          width: 18,
                                          child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                                        )
                                      : Text(_isLogin ? 'Se connecter' : 'Créer mon compte'),
                                ),
                              ),
                              SizedBox(height: isPhone ? 10 : 14),
                              TextButton(
                                onPressed: _isSubmitting ? null : () => _toggleMode(true),
                                child: Text(_isLogin ? 'Mot de passe oublie ?' : 'J\'ai deja un compte'),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            );

            final heroContent = Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(isPhone ? 20 : 26),
                gradient: const LinearGradient(
                  colors: [Color(0xFF0D8A5F), Color(0xFF0B5F5C)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                boxShadow: const [
                  BoxShadow(
                    blurRadius: 28,
                    offset: Offset(0, 16),
                    color: Color(0x330A4737),
                  ),
                ],
              ),
              child: Stack(
                children: [
                  Positioned(
                    top: 22,
                    right: 22,
                    child: Icon(Icons.savings_rounded, color: Colors.white.withOpacity(0.14), size: isPhone ? 56 : 64),
                  ),
                  Padding(
                    padding: EdgeInsets.all(isPhone ? 18 : 24),
        child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Container(
                          padding: EdgeInsets.symmetric(horizontal: isPhone ? 10 : 12, vertical: 8),
                          decoration: BoxDecoration(
                            color: Colors.white.withOpacity(0.16),
                            borderRadius: BorderRadius.circular(14),
                            border: Border.all(color: Colors.white24),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
          children: [
                              Icon(Icons.shield_moon_outlined, color: Colors.white, size: isPhone ? 16 : 18),
                              const SizedBox(width: 8),
                              Text(
                                'Sécurité bancaire AES-256',
                                style: TextStyle(color: Colors.white, fontSize: isPhone ? 12 : 14),
                              ),
                            ],
                          ),
                        ),
                        SizedBox(height: isPhone ? 18 : 26),
                        Text(
                          'Collecte d’épargne\nsécurisée & intelligente',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: isPhone ? 24 : 30,
                            fontWeight: FontWeight.w800,
                            height: 1.1,
                          ),
                        ),
                        SizedBox(height: isPhone ? 10 : 12),
            Text(
                          'Visualisez vos objectifs, automatisez vos dépôts et suivez vos progrès en temps réel.',
                          style: TextStyle(
                            color: Colors.white70,
                            fontSize: isPhone ? 13 : 15,
                            height: 1.4,
                          ),
                        ),
                       
                        const Spacer(),
                        if (!isWide)
                          Align(
                            alignment: Alignment.bottomRight,
                            child: IconButton(
                              onPressed: () => _toggleMode(!_isLogin),
                              icon: const Icon(Icons.swap_horiz, color: Colors.white),
                              tooltip: 'Basculer Connexion / Inscription',
                            ),
            ),
          ],
        ),
      ),
                ],
              ),
            );

            final hero = isWide
                ? Expanded(
                    child: Padding(
                      padding: EdgeInsets.symmetric(
                        horizontal: isWide ? 32 : 16,
                        vertical: isWide ? 36 : 10,
                      ),
                      child: heroContent,
                    ),
                  )
                : Padding(
                    padding: EdgeInsets.symmetric(horizontal: isPhone ? 8 : 12, vertical: isPhone ? 8 : 12),
                    child: SizedBox(
                      height: isPhone
                          ? 230
                          : isTablet
                              ? 280
                              : 300,
                      child: heroContent,
                    ),
                  );

            return SafeArea(
              child: Center(
                child: ConstrainedBox(
                    constraints: const BoxConstraints(maxWidth: 1200),
                  child: Padding(
                    padding: EdgeInsets.symmetric(
                      horizontal: isWide
                          ? 32
                          : isTablet
                              ? 22
                              : 14,
                      vertical: isWide ? 18 : 10,
                    ),
                    child: isWide
                        ? Row(
                            children: [
                              hero,
                              const SizedBox(width: 24),
                              form,
                            ],
                          )
                        : SingleChildScrollView(
                            child: Column(
                              children: [
                                hero,
                                const SizedBox(height: 16),
                                form,
                              ],
                            ),
                          ),
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
