import 'package:flutter/material.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/collecteur_dashboard.dart';
import 'screens/client_dashboard.dart';
import 'screens/account_history.dart';
import 'screens/client_profile.dart';
import 'screens/collecteur_profile.dart';
import 'screens/collecteur_clients.dart';
import 'screens/collecteur_collect.dart';
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
        '/collecteur-dashboard': (context) => const CollecteurDashboard(),
        '/client-dashboard': (context) => const ClientDashboard(),
        '/account-history': (context) => const AccountHistory(),
        '/client-profile': (context) => const ClientProfile(),
        '/collecteur-profile': (context) => const CollecteurProfile(),
        '/collecteur-clients': (context) => const CollecteurClients(),
        '/collecteur-collect': (context) => const CollecteurCollect(),
        '/client-transactions': (context) => const ClientTransactions(),
      },
      initialRoute: '/login',
    );
  }
}
