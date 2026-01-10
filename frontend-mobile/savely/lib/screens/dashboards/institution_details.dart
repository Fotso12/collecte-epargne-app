import 'package:flutter/material.dart';
import '../../services/admin_api.dart';

class InstitutionDetailsPage extends StatefulWidget {
  final int institutionId;

  const InstitutionDetailsPage({super.key, required this.institutionId});

  @override
  State<InstitutionDetailsPage> createState() => _InstitutionDetailsPageState();
}

class _InstitutionDetailsPageState extends State<InstitutionDetailsPage> {
  Map<String, dynamic>? _institutionDetails;
  List<Map<String, dynamic>> _allEmployees = [];
  List<Map<String, dynamic>> _assignedEmployees = [];
  bool _isLoading = true;
  bool _isLoadingEmployees = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final details = await AdminApi.getInstitutionDetails(widget.institutionId);
      final employees = await AdminApi.getInstitutionEmployees(widget.institutionId);

      setState(() {
        _institutionDetails = details;
        _allEmployees = employees;
        _assignedEmployees = employees.where((e) => e['isAssigned'] == true).toList();
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _assignEmployee(int employeeId) async {
    setState(() => _isLoadingEmployees = true);
    try {
      await AdminApi.assignEmployeeToInstitution(
        institutionId: widget.institutionId,
        employeeId: employeeId,
      );
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Employé affecté avec succès')),
      );
      await _loadData();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erreur: $e')),
      );
    } finally {
      setState(() => _isLoadingEmployees = false);
    }
  }

  Future<void> _unassignEmployee(int employeeId) async {
    setState(() => _isLoadingEmployees = true);
    try {
      await AdminApi.unassignEmployeeFromInstitution(
        institutionId: widget.institutionId,
        employeeId: employeeId,
      );
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Employé retiré avec succès')),
      );
      await _loadData();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erreur: $e')),
      );
    } finally {
      setState(() => _isLoadingEmployees = false);
    }
  }

  void _showAssignEmployeeDialog() {
    final unassignedEmployees = _allEmployees.where((e) => e['isAssigned'] == false).toList();
    
    if (unassignedEmployees.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Aucun employé disponible à affecter')),
      );
      return;
    }

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Affecter un employé'),
        content: SizedBox(
          width: double.maxFinite,
          child: ListView.builder(
            shrinkWrap: true,
            itemCount: unassignedEmployees.length,
            itemBuilder: (context, index) {
              final employee = unassignedEmployees[index];
              return ListTile(
                leading: CircleAvatar(
                  child: Text('${employee['prenom']?[0] ?? ''}${employee['nom']?[0] ?? ''}'),
                ),
                title: Text('${employee['prenom'] ?? ''} ${employee['nom'] ?? ''}'),
                subtitle: Text('${employee['typeEmploye'] ?? ''} - ${employee['matricule'] ?? ''}'),
                trailing: IconButton(
                  icon: const Icon(Icons.add),
                  onPressed: () {
                    Navigator.pop(context);
                    _assignEmployee(employee['idEmploye'] as int);
                  },
                ),
              );
            },
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Annuler'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        appBar: AppBar(title: const Text('Détails de l\'agence')),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    if (_error != null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Détails de l\'agence')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Erreur: $_error'),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _loadData,
                child: const Text('Réessayer'),
              ),
            ],
          ),
        ),
      );
    }

    if (_institutionDetails == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Détails de l\'agence')),
        body: const Center(child: Text('Aucune donnée disponible')),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(_institutionDetails!['name'] ?? 'Détails de l\'agence'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadData,
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Informations de l'agence
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Informations de l\'agence',
                        style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 16),
                      _buildInfoRow('Nom', _institutionDetails!['name'] ?? ''),
                      _buildInfoRow('Code', _institutionDetails!['code'] ?? ''),
                      if (_institutionDetails!['contactEmail']?.isNotEmpty == true)
                        _buildInfoRow('Email', _institutionDetails!['contactEmail'] ?? ''),
                      if (_institutionDetails!['contactPhone']?.isNotEmpty == true)
                        _buildInfoRow('Téléphone', _institutionDetails!['contactPhone'] ?? ''),
                      _buildInfoRow('Fuseau horaire', _institutionDetails!['timezone'] ?? ''),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 24),
              
              // Employés affectés
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text(
                    'Employés affectés',
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  ElevatedButton.icon(
                    onPressed: _isLoadingEmployees ? null : _showAssignEmployeeDialog,
                    icon: const Icon(Icons.person_add),
                    label: const Text('Affecter'),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              
              if (_isLoadingEmployees)
                const Center(child: CircularProgressIndicator())
              else if (_assignedEmployees.isEmpty)
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Center(
                      child: Column(
                        children: [
                          Icon(Icons.people_outline, size: 64, color: Colors.grey[400]),
                          const SizedBox(height: 16),
                          Text(
                            'Aucun employé affecté',
                            style: TextStyle(color: Colors.grey[600]),
                          ),
                        ],
                      ),
                    ),
                  ),
                )
              else
                ..._assignedEmployees.map((employee) => Card(
                  margin: const EdgeInsets.only(bottom: 8),
                  child: ListTile(
                    leading: CircleAvatar(
                      child: Text('${employee['prenom']?[0] ?? ''}${employee['nom']?[0] ?? ''}'),
                    ),
                    title: Text('${employee['prenom'] ?? ''} ${employee['nom'] ?? ''}'),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('${employee['typeEmploye'] ?? ''} - ${employee['matricule'] ?? ''}'),
                        if (employee['email'] != null) Text(employee['email'] ?? ''),
                      ],
                    ),
                    trailing: IconButton(
                      icon: const Icon(Icons.remove_circle, color: Colors.red),
                      onPressed: () => _unassignEmployee(employee['idEmploye'] as int),
                    ),
                  ),
                )),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120,
            child: Text(
              '$label:',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }
}



