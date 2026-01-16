import 'package:flutter/material.dart';

class AppException implements Exception {
  final String message;
  final String? code;
  final dynamic originalError;
  final StackTrace? stackTrace;

  AppException({
    required this.message,
    this.code,
    this.originalError,
    this.stackTrace,
  });

  @override
  String toString() => message;
}

class NetworkException extends AppException {
  NetworkException({String message = 'Erreur réseau', dynamic originalError})
    : super(
        message: message,
        code: 'NETWORK_ERROR',
        originalError: originalError,
      );
}

class AuthException extends AppException {
  AuthException({
    String message = 'Authentification échouée',
    dynamic originalError,
  }) : super(
         message: message,
         code: 'AUTH_ERROR',
         originalError: originalError,
       );
}

class ValidationException extends AppException {
  ValidationException({
    String message = 'Données invalides',
    dynamic originalError,
  }) : super(
         message: message,
         code: 'VALIDATION_ERROR',
         originalError: originalError,
       );
}

class ServerException extends AppException {
  ServerException({String message = 'Erreur serveur', dynamic originalError})
    : super(
        message: message,
        code: 'SERVER_ERROR',
        originalError: originalError,
      );
}

class TimeoutException extends AppException {
  TimeoutException({
    String message = 'La demande a expiré',
    dynamic originalError,
  }) : super(
         message: message,
         code: 'TIMEOUT_ERROR',
         originalError: originalError,
       );
}

/// Error Handler pour la gestion des erreurs côté application
class ErrorHandler {
  /// Convertir une exception en message utilisateur friendly
  static String getDisplayMessage(dynamic error) {
    if (error is AppException) {
      return error.message;
    }

    if (error is TimeoutException || error.toString().contains('timeout')) {
      return 'La connexion au serveur a expiré. Veuillez réessayer.';
    }

    if (error is NetworkException || error.toString().contains('Network')) {
      return 'Erreur de connexion. Veuillez vérifier votre connexion internet.';
    }

    if (error is AuthException ||
        error.toString().contains('401') ||
        error.toString().contains('Unauthorized')) {
      return 'Identifiants invalides. Veuillez réessayer.';
    }

    if (error.toString().contains('403') ||
        error.toString().contains('Forbidden')) {
      return 'Vous n\'avez pas les permissions nécessaires.';
    }

    if (error.toString().contains('404') ||
        error.toString().contains('not found')) {
      return 'Ressource non trouvée.';
    }

    if (error.toString().contains('500') ||
        error.toString().contains('Internal Server Error')) {
      return 'Erreur du serveur. Veuillez réessayer plus tard.';
    }

    if (error is ValidationException ||
        error.toString().contains('validation')) {
      return 'Les données fournies sont invalides. Veuillez vérifier vos saisies.';
    }

    return error.toString().length > 200
        ? 'Une erreur est survenue. Veuillez réessayer.'
        : error.toString();
  }

  /// Afficher un dialogue d'erreur
  static Future<void> showErrorDialog(
    BuildContext context,
    dynamic error, {
    String? title,
    VoidCallback? onRetry,
  }) {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title ?? 'Erreur'),
        content: Text(getDisplayMessage(error)),
        actions: [
          if (onRetry != null)
            TextButton(
              onPressed: () {
                Navigator.pop(context);
                onRetry();
              },
              child: const Text('Réessayer'),
            ),
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Fermer'),
          ),
        ],
      ),
    );
  }

  /// Afficher un SnackBar d'erreur
  static void showErrorSnackBar(
    BuildContext context,
    dynamic error, {
    Duration duration = const Duration(seconds: 4),
  }) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(getDisplayMessage(error)),
        backgroundColor: Colors.red.shade700,
        duration: duration,
      ),
    );
  }

  /// Log error pour debug
  static void logError(dynamic error, StackTrace? stackTrace) {
    print('╔════════════════════════════════════════════════════════════');
    print('║ ERROR LOG');
    print('║ Time: ${DateTime.now().toIso8601String()}');
    print('╠════════════════════════════════════════════════════════════');
    print('║ Message: $error');
    if (stackTrace != null) {
      print('║\n$stackTrace');
    }
    print('╚════════════════════════════════════════════════════════════\n');
  }
}

/// Retry Handler pour les retries avec backoff exponentiel
class RetryHandler {
  static Future<T> retryWithBackoff<T>({
    required Future<T> Function() action,
    int maxAttempts = 3,
    Duration initialDelay = const Duration(seconds: 1),
    double backoffMultiplier = 2.0,
  }) async {
    var attempt = 0;
    var delay = initialDelay;

    while (attempt < maxAttempts) {
      try {
        attempt++;
        print('[RetryHandler] Attempt $attempt/$maxAttempts');
        return await action();
      } catch (e) {
        if (attempt >= maxAttempts) {
          print('[RetryHandler] All attempts failed');
          rethrow;
        }

        print(
          '[RetryHandler] Failed (attempt $attempt/$maxAttempts). Retrying in ${delay.inSeconds}s...',
        );
        await Future.delayed(delay);
        delay = Duration(
          milliseconds: (delay.inMilliseconds * backoffMultiplier).toInt(),
        );
      }
    }

    throw Exception('Retry handler failed');
  }

  /// Retry avec condition personnalisée
  static Future<T> retryIf<T>({
    required Future<T> Function() action,
    required bool Function(dynamic error) shouldRetry,
    int maxAttempts = 3,
    Duration delay = const Duration(seconds: 1),
  }) async {
    var attempt = 0;

    while (attempt < maxAttempts) {
      try {
        attempt++;
        return await action();
      } catch (e) {
        if (!shouldRetry(e) || attempt >= maxAttempts) {
          rethrow;
        }
        await Future.delayed(delay);
      }
    }

    throw Exception('Retry handler failed');
  }
}
