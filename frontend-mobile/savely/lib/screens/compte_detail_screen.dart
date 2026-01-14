import 'package:flutter/material.dart';
import '../../models/compte_model.dart';

class CompteDetailScreen extends StatelessWidget {
  final CompteModel compte;

  const CompteDetailScreen({super.key, required this.compte});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Compte ${compte.numCompte}'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeaderCard(context),
            const SizedBox(height: 16),
            _buildInfoCard(context, 'Informations du compte', [
              _buildInfoRow('Numéro de compte', compte.numCompte),
              _buildInfoRow('Type de compte', compte.typeCompteNom ?? 'Non spécifié'),
              _buildInfoRow('Date d\'ouverture', compte.formattedDateOuverture),
              _buildInfoRow('Dernière transaction', compte.formattedDerniereTransaction),
            ]),
            const SizedBox(height: 16),
            _buildInfoCard(context, 'Soldes', [
              _buildInfoRow('Solde total', compte.formattedSolde, isHighlight: true),
              _buildInfoRow('Solde disponible', compte.formattedSoldeDisponible, isHighlight: true),
            ]),
            if (compte.tauxBonus != null || compte.tauxPenalite != null) ...[
              const SizedBox(height: 16),
              _buildInfoCard(context, 'Taux', [
                if (compte.tauxBonus != null)
                  _buildInfoRow('Taux bonus', '${compte.tauxBonus!.toStringAsFixed(2)}%'),
                if (compte.tauxPenalite != null)
                  _buildInfoRow('Taux pénalité', '${compte.tauxPenalite!.toStringAsFixed(2)}%'),
              ]),
            ],
            if (compte.isBloque && compte.motifBlocage != null) ...[
              const SizedBox(height: 16),
              _buildAlertCard(context, 'Compte bloqué', compte.motifBlocage!),
            ],
            if (compte.isCloture) ...[
              const SizedBox(height: 16),
              _buildAlertCard(context, 'Compte clôturé', 
                compte.dateCloture != null 
                  ? 'Clôturé le ${compte.dateCloture!.day}/${compte.dateCloture!.month}/${compte.dateCloture!.year}'
                  : 'Ce compte a été clôturé'),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildHeaderCard(BuildContext context) {
    Color statusColor;
    String statusText;
    IconData statusIcon;

    if (compte.isCloture) {
      statusColor = Colors.grey;
      statusText = 'Clôturé';
      statusIcon = Icons.lock_outline;
    } else if (compte.isBloque) {
      statusColor = Colors.red;
      statusText = 'Bloqué';
      statusIcon = Icons.block;
    } else {
      statusColor = Colors.green;
      statusText = 'Actif';
      statusIcon = Icons.check_circle_outline;
    }

    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Solde total',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: Colors.grey[600],
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        compte.formattedSolde,
                        style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                      ),
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(
                    color: statusColor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(20),
                    border: Border.all(color: statusColor),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(statusIcon, size: 16, color: statusColor),
                      const SizedBox(width: 6),
                      Text(
                        statusText,
                        style: TextStyle(
                          color: statusColor,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Divider(),
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Solde disponible',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.grey[600],
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        compte.formattedSoldeDisponible,
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoCard(BuildContext context, String title, List<Widget> children) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            ...children,
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value, {bool isHighlight = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: TextStyle(
              color: Colors.grey[600],
              fontSize: 14,
            ),
          ),
          Text(
            value,
            style: TextStyle(
              fontWeight: isHighlight ? FontWeight.bold : FontWeight.normal,
              fontSize: isHighlight ? 16 : 14,
              color: isHighlight ? Colors.black87 : Colors.black87,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAlertCard(BuildContext context, String title, String message) {
    return Card(
      color: Colors.orange[50],
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Icon(Icons.warning_amber_rounded, color: Colors.orange[700]),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Colors.orange[900],
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    message,
                    style: TextStyle(
                      color: Colors.orange[800],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

