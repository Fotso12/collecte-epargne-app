import 'package:flutter/material.dart';

class TransactionStatusBadge extends StatelessWidget {
  final String status;

  const TransactionStatusBadge({
    super.key,
    required this.status,
  });

  Color _getColor() {
    switch (status.toUpperCase()) {
      case 'VALIDEE':
      case 'TERMINEE':
      case 'VALIDEE_CAISSE':
      case 'VALIDEE_SUPERVISEUR':
        return const Color(0xFF28a745);
      case 'EN_ATTENTE':
        return const Color(0xFFffc107);
      case 'REJETEE':
        return const Color(0xFFdc3545);
      case 'ANNULEE':
        return const Color(0xFF6c757d);
      default:
        return Colors.grey;
    }
  }

  String _getLabel() {
    switch (status.toUpperCase()) {
      case 'VALIDEE':
      case 'VALIDEE_CAISSE':
      case 'VALIDEE_SUPERVISEUR':
        return 'Validée';
      case 'EN_ATTENTE':
        return 'En attente';
      case 'REJETEE':
        return 'Rejetée';
      case 'ANNULEE':
        return 'Annulée';
      case 'TERMINEE':
        return 'Terminée';
      default:
        return status;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: _getColor().withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: _getColor(), width: 1),
      ),
      child: Text(
        _getLabel(),
        style: TextStyle(
          color: _getColor(),
          fontWeight: FontWeight.bold,
          fontSize: 12,
        ),
      ),
    );
  }
}
