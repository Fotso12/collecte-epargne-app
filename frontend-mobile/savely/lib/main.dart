import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/collecteur_dashboard.dart';
import 'screens/client_dashboard.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

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
      },
      initialRoute: '/login',
    );
  }
}
