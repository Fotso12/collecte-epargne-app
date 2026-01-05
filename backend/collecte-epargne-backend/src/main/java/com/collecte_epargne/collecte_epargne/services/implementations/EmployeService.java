package com.collecte_epargne.collecte_epargne.services.implementations;


import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.EmployeMapper;
import com.collecte_epargne.collecte_epargne.mappers.ClientMapper;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.EmployeInterface;
import com.collecte_epargne.collecte_epargne.utils.CodeGenerator;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmployeService implements EmployeInterface {

    private final EmployeRepository employeRepository;
    private final EmployeMapper employeMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final AgenceZoneRepository agenceZoneRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final CodeGenerator codeGenerator;

    public EmployeService(EmployeRepository employeRepository, EmployeMapper employeMapper, UtilisateurRepository utilisateurRepository, AgenceZoneRepository agenceZoneRepository, ClientRepository clientRepository, ClientMapper clientMapper, CodeGenerator codeGenerator) {
        this.employeRepository = employeRepository;
        this.employeMapper = employeMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.agenceZoneRepository = agenceZoneRepository;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.codeGenerator = codeGenerator;
    }


    // --- CRUD Générique ---

    private void assignerRelations(Employe employe, EmployeDto dto) {
        // Utilisateur (LOGIN)
        if (dto.getLoginUtilisateur() != null) {
            String login = Objects.requireNonNull(dto.getLoginUtilisateur());
            Utilisateur utilisateur = utilisateurRepository.findById(login)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le login : " + dto.getLoginUtilisateur()));
            employe.setUtilisateur(utilisateur);
        }

        // AgenceZone (ID)
        if (dto.getIdAgence() != null) {
            Integer idAgence = Objects.requireNonNull(dto.getIdAgence());
            AgenceZone agenceZone = agenceZoneRepository.findById(idAgence)
                    .orElseThrow(() -> new RuntimeException("AgenceZone non trouvée"));
            employe.setAgenceZone(agenceZone);
        } else {
            employe.setAgenceZone(null);
        }

        // Superviseur (ID_EMPLOYE)
        if (dto.getIdSuperviseur() != null && !dto.getIdSuperviseur().isEmpty()) {
            // Correction : Convertir le String en Integer
            Integer idSuperviseur = Integer.parseInt(dto.getIdSuperviseur());
            Employe superviseur = employeRepository.findById(idSuperviseur)
                    .orElseThrow(() -> new RuntimeException("Superviseur non trouvé : " + dto.getIdSuperviseur()));

            // Logique métier: s'assurer que l'employé est bien un superviseur
            if (superviseur.getTypeEmploye() != TypeEmploye.SUPERVISEUR) {
                throw new IllegalArgumentException("L'employé assigné comme superviseur doit être de type SUPERVISEUR.");
            }
            employe.setSuperviseur(superviseur);
        } else {
            employe.setSuperviseur(null);
        }
    }

    @Override
    @SuppressWarnings("null")
    public EmployeDto save(EmployeDto employeDto) {
        Objects.requireNonNull(employeDto, "employeDto ne doit pas être null");
        // Auto-générer le matricule si non fourni
        if (employeDto.getMatricule() == null || employeDto.getMatricule().isEmpty() || (employeDto.getMatricule() != null && !employeDto.getMatricule().isEmpty())) {
            employeDto.setMatricule(codeGenerator.generateMatricule(employeDto.getTypeEmploye()));
        }

        // Vérifier l'unicité du matricule
        if (employeRepository.findByMatricule(employeDto.getMatricule()).isPresent()) {
            throw new RuntimeException("Un employé avec ce matricule existe déjà.");
        }

        Employe employeToSave = employeMapper.toEntity(employeDto);
        assignerRelations(employeToSave, employeDto);

        Employe savedEmploye = employeRepository.save(employeToSave);
        return employeMapper.toDto(savedEmploye);
    }

    @Override
@jakarta.transaction.Transactional // Assure que si l'un échoue, rien n'est modifié
public EmployeDto update(String matricule, EmployeDto employeDto) {
    Objects.requireNonNull(matricule, "matricule ne doit pas être null");
    Objects.requireNonNull(employeDto, "employeDto ne doit pas être null");
    
    // 1. Trouver l'employé existant
    Employe existingEmploye = employeRepository.findByMatricule(matricule)
            .orElseThrow(() -> new RuntimeException("Employé non trouvé : " + matricule));

    // 2. Mettre à jour l'entité Utilisateur liée (Champs partagés)
    if (existingEmploye.getUtilisateur() != null) {
        Utilisateur user = existingEmploye.getUtilisateur();
        // On met à jour les infos de base qui viennent du DTO employé
        user.setNom(employeDto.getNom());
        user.setPrenom(employeDto.getPrenom());
        user.setEmail(employeDto.getEmail());
        user.setTelephone(employeDto.getTelephone());

        utilisateurRepository.save(user);
    }

    // 3. Mettre à jour les champs spécifiques à l'Employé
    existingEmploye.setTypeEmploye(employeDto.getTypeEmploye());
    existingEmploye.setCommissionTaux(employeDto.getCommissionTaux());
    existingEmploye.setDateEmbauche(employeDto.getDateEmbauche());

    // 4. Gérer les relations (Changement de superviseur ou d'agence)
    assignerRelations(existingEmploye, employeDto);

    // 5. Sauvegarder l'employé
    Employe updatedEmploye = employeRepository.save(existingEmploye);
    return employeMapper.toDto(updatedEmploye);
}

    // ... getById, delete, et getAll (non modifiés)

    @Override
    public EmployeDto getById(String  matricule) {
        Objects.requireNonNull(matricule, "matricule ne doit pas être null");
        Employe employe = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé avec l'ID : " + matricule));
        return employeMapper.toDto(employe);
    }

    @Override
    @jakarta.transaction.Transactional // Crucial pour que les deux suppressions fonctionnent ensemble
    public void delete(String matricule) {
        Objects.requireNonNull(matricule, "matricule ne doit pas être null");
        
        // 1. Trouver l'Employé par son MATRICULE
        Employe employe = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Employé inexistant : " + matricule));

        // 2. Récupérer le login de l'utilisateur associé AVANT de supprimer l'employé
        String loginAssocie = null;
        if (employe.getUtilisateur() != null) {
            loginAssocie = employe.getUtilisateur().getLogin();
        }

        // 3. Supprimer d'abord l'Employé (pour libérer la contrainte de clé étrangère)
        employeRepository.delete(employe);
        
        // 4. Supprimer l'Utilisateur associé si existant
        if (loginAssocie != null) {
            utilisateurRepository.deleteById(loginAssocie);
        }
    }

    @Override
    public List<EmployeDto> getAll() {
        return employeRepository.findAll().stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
    }

    // --- CRUD Spécialisé (Listage par Rôle) ---

    @Override
    public List<EmployeDto> getSuperviseurs() {
        return employeRepository.findByTypeEmploye(TypeEmploye.SUPERVISEUR).stream()
                .map(employeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EmployeDto> getCaissiers() {
        return employeRepository.findByTypeEmploye(TypeEmploye.CAISSIER).stream()
                .map(employeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EmployeDto> getCollecteurs() {
        return employeRepository.findByTypeEmploye(TypeEmploye.COLLECTEUR).stream()
                .map(employeMapper::toDto).collect(Collectors.toList());
    }

    // --- Fonctions de Listage et Tri Spécialisé (Collecteurs) ---

    @Override
    public List<EmployeDto> getCollecteursOrderedByClientCount() {
        return employeRepository.findCollecteursOrderByClientCountDesc().stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeDto> getCollecteursOrderedByTotalClientScore() {
        return employeRepository.findCollecteursOrderByTotalClientScoreDesc().stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
    }

    // --- Fonctions de Relations ---

    @Override
    public List<EmployeDto> getCollecteursBySuperviseur(String idSuperviseur) {
        // Vérification si le superviseur existe et est bien un SUPERVISEUR
        Objects.requireNonNull(idSuperviseur, "idSuperviseur ne doit pas être null");

        // Correction : Convertir le String en Integer
        Integer idSuperviseurInt = Integer.parseInt(idSuperviseur);
        Employe superviseur = employeRepository.findById(idSuperviseurInt)
                .orElseThrow(() -> new RuntimeException("Superviseur non trouvé : " + idSuperviseur));

        if (superviseur.getTypeEmploye() != TypeEmploye.SUPERVISEUR) {
            throw new IllegalArgumentException("L'employé ID " + idSuperviseur + " n'est pas un superviseur.");
        }

        return employeRepository.findBySuperviseurIdEmploye(idSuperviseurInt).stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientDto> getClientsByCollecteur(String matricule) {
        Objects.requireNonNull(matricule, "Le matricule ne doit pas être null");

        // 1. On cherche l'employé par son MATRICULE (String) et non par son ID technique (Integer)
        // On utilise le repository pour trouver l'entité complète
        Employe collecteur = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Collecteur non trouvé avec le matricule : " + matricule));

        // 2. Vérification du rôle
        if (collecteur.getTypeEmploye() != TypeEmploye.COLLECTEUR) {
            throw new IllegalArgumentException("L'employé avec le matricule " + matricule + " n'est pas un collecteur.");
        }

        // 3. Maintenant qu'on a l'objet, on utilise son ID technique interne (Integer)
        // pour interroger le ClientRepository
        return clientRepository.findByCollecteurAssigneIdEmploye(collecteur.getIdEmploye()).stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }


}