import 'package:flutter/material.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/dashboards/collecteur_dashboard.dart';
import 'screens/dashboards/client_dashboard.dart';
import 'screens/account_history.dart';
import 'screens/dashboards/client_profile_screen.dart';
import 'screens/dashboards/collecteur_profile_screen.dart';
import 'services/auth_api.dart'; // Pour récupérer l'utilisateur actuel dans les routes si besoin
import 'screens/clients_list_screen.dart';
import 'screens/nouvelle_collecte_screen.dart';
import 'screens/client_transactions.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Savely - Collecte d\'Épargne',
      theme: ThemeData(
        primaryColor: const Color(0xFF0D8A5F),
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF0D8A5F)),
      ),
      home: const LoginScreen(),
      routes: {
        '/login': (context) => const LoginScreen(),
        '/register': (context) => const RegisterScreen(),
        '/collecteur-dashboard': (context) {
          final user = AuthApi.currentUser;
          if (user == null) return const LoginScreen();
          return CollecteurDashboard(user: user);
        },
        '/client-dashboard': (context) {
          final user = AuthApi.currentUser;
          if (user == null) return const LoginScreen();
          return ClientDashboard(user: user);
        },
        '/account-history': (context) => const AccountHistory(),
        '/client-profile': (context) {
          final user = AuthApi.currentUser;
          // Note: ClientProfileScreen might need codeClient from backend
          // We can handle this logic inside the screen or pass it here if known
          return const ClientProfileScreen(codeClient: null); 
        },
        '/collecteur-profile': (context) {
          final user = AuthApi.currentUser;
          if (user == null) return const LoginScreen();
          return CollecteurProfileScreen(user: user);
        },
        '/collecteur-clients': (context) {
          final user = AuthApi.currentUser;
          if (user == null) return const LoginScreen();
          return ClientsListScreen(matriculeCollecteur: user.login);
        },
        '/collecteur-collect': (context) {
          final user = AuthApi.currentUser;
          if (user == null) return const LoginScreen();
          return NouvelleCollecteScreen(matriculeCollecteur: user.login);
        },
        '/client-transactions': (context) => const ClientTransactions(),
      },
      initialRoute: '/login',
    );
  }
}
