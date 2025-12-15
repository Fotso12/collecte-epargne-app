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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Service
@AllArgsConstructor
public class EmployeService implements EmployeInterface {

    private final EmployeRepository employeRepository;
    private final EmployeMapper employeMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final AgenceZoneRepository agenceZoneRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final CodeGenerator codeGenerator;

    // --- CRUD Générique ---

    private void assignerRelations(Employe employe, EmployeDto dto) {
        // Utilisateur (LOGIN)
        if (dto.getLoginUtilisateur() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(dto.getLoginUtilisateur())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le login : " + dto.getLoginUtilisateur()));
            employe.setUtilisateur(utilisateur);
        }

        // AgenceZone (ID)
        if (dto.getIdAgence() != null) {
            AgenceZone agenceZone = agenceZoneRepository.findById(dto.getIdAgence())
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
    public EmployeDto save(EmployeDto employeDto) {
        // Auto-générer le matricule si non fourni
        if (employeDto.getMatricule() == null || employeDto.getMatricule().isEmpty() || employeDto.getMatricule() != null && !employeDto.getMatricule().isEmpty()) {
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
    public EmployeDto update(String  matricule, EmployeDto employeDto) {
        Employe existingEmploye = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé : " + matricule));

        // Mettre à jour les champs de l'entité existante depuis le DTO
        existingEmploye.setTypeEmploye(employeDto.getTypeEmploye());
        existingEmploye.setCommissionTaux(employeDto.getCommissionTaux());

        assignerRelations(existingEmploye, employeDto);

        Employe updatedEmploye = employeRepository.save(existingEmploye);
        return employeMapper.toDto(updatedEmploye);
    }

    // ... getById, delete, et getAll (non modifiés)

    @Override
    public EmployeDto getById(String  matricule) {
        Employe employe = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé avec l'ID : " + matricule));
        return employeMapper.toDto(employe);
    }

    @Override
    public void delete(String  matricule) {
        // 1. Trouver l'Employé par son MATRICULE (String)
        Employe employe = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Employé inexistant : " + matricule));

        // 2. Supprimer par son ID PRIMAIRE (Integer)
        employeRepository.deleteById(employe.getIdEmploye());
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
    public List<ClientDto> getClientsByCollecteur(String idCollecteur) {

        // Correction : Convertir le String en Integer
        Integer idCollecteurInt = Integer.parseInt(idCollecteur);
        Employe collecteur = employeRepository.findById(idCollecteurInt)
                .orElseThrow(() -> new RuntimeException("Collecteur non trouvé : " + idCollecteur));

        if (collecteur.getTypeEmploye() != TypeEmploye.COLLECTEUR) {
            throw new IllegalArgumentException("L'employé ID " + idCollecteur + " n'est pas un collecteur.");
        }

        // Utilisation de la relation ManyToOne/OneToMany via ClientRepository
        return clientRepository.findByCollecteurAssigneIdEmploye(idCollecteurInt).stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }
}