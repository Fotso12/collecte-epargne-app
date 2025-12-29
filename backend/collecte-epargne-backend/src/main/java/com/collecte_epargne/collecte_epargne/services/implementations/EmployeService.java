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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmployeService implements EmployeInterface {

    private static final Logger logger = LoggerFactory.getLogger(EmployeService.class);

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
        logger.info("Début de la création d'un employé - Type: {}, Login: {}", 
                employeDto.getTypeEmploye(), employeDto.getLoginUtilisateur());
        Objects.requireNonNull(employeDto, "employeDto ne doit pas être null");
        // Auto-générer le matricule si non fourni
        if (employeDto.getMatricule() == null || employeDto.getMatricule().isEmpty() || (employeDto.getMatricule() != null && !employeDto.getMatricule().isEmpty())) {
            String matriculeGenere = codeGenerator.generateMatricule(employeDto.getTypeEmploye());
            employeDto.setMatricule(matriculeGenere);
            logger.debug("Matricule auto-généré pour l'employé: {}", matriculeGenere);
        }

        // Vérifier l'unicité du matricule
        if (employeRepository.findByMatricule(employeDto.getMatricule()).isPresent()) {
            logger.warn("Tentative de création d'un employé avec un matricule existant: {}", employeDto.getMatricule());
            throw new RuntimeException("Un employé avec ce matricule existe déjà.");
        }

        Employe employeToSave = employeMapper.toEntity(employeDto);
        assignerRelations(employeToSave, employeDto);

        Employe savedEmploye = employeRepository.save(employeToSave);
        logger.info("Employé créé avec succès - ID: {}, Matricule: {}, Type: {}", 
                savedEmploye.getIdEmploye(), savedEmploye.getMatricule(), savedEmploye.getTypeEmploye());
        return employeMapper.toDto(savedEmploye);
    }

    @Override
    public EmployeDto update(String  matricule, EmployeDto employeDto) {
        logger.info("Début de la mise à jour de l'employé - Matricule: {}, Type: {}", 
                matricule, employeDto.getTypeEmploye());
        Objects.requireNonNull(matricule, "matricule ne doit pas être null");
        Objects.requireNonNull(employeDto, "employeDto ne doit pas être null");
        Employe existingEmploye = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> {
                    logger.warn("Tentative de mise à jour d'un employé inexistant - Matricule: {}", matricule);
                    return new RuntimeException("Employé non trouvé : " + matricule);
                });

        // Mettre à jour les champs de l'entité existante depuis le DTO
        logger.debug("Mise à jour des champs de l'employé - Matricule: {}", matricule);
        existingEmploye.setTypeEmploye(employeDto.getTypeEmploye());
        existingEmploye.setCommissionTaux(employeDto.getCommissionTaux());

        assignerRelations(existingEmploye, employeDto);

        Employe updatedEmploye = employeRepository.save(existingEmploye);
        logger.info("Employé mis à jour avec succès - ID: {}, Matricule: {}, Type: {}", 
                updatedEmploye.getIdEmploye(), updatedEmploye.getMatricule(), updatedEmploye.getTypeEmploye());
        return employeMapper.toDto(updatedEmploye);
    }

    // ... getById, delete, et getAll (non modifiés)

    @Override
    public EmployeDto getById(String  matricule) {
        logger.debug("Récupération de l'employé avec le matricule: {}", matricule);
        Objects.requireNonNull(matricule, "matricule ne doit pas être null");
        Employe employe = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> {
                    logger.warn("Employé non trouvé avec le matricule: {}", matricule);
                    return new RuntimeException("Employé non trouvé avec l'ID : " + matricule);
                });
        logger.info("Employé récupéré avec succès - Matricule: {}, Type: {}", 
                employe.getMatricule(), employe.getTypeEmploye());
        return employeMapper.toDto(employe);
    }

    @Override
    public void delete(String  matricule) {
        logger.info("Début de la suppression de l'employé - Matricule: {}", matricule);
        Objects.requireNonNull(matricule, "matricule ne doit pas être null");
        // 1. Trouver l'Employé par son MATRICULE (String)
        Employe employe = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> {
                    logger.warn("Tentative de suppression d'un employé inexistant - Matricule: {}", matricule);
                    return new RuntimeException("Employé inexistant : " + matricule);
                });

        // 2. Supprimer par son ID PRIMAIRE (Integer)
        Integer idEmploye = Objects.requireNonNull(employe.getIdEmploye());
        employeRepository.deleteById(idEmploye);
        logger.info("Employé supprimé avec succès - ID: {}, Matricule: {}", idEmploye, matricule);
    }

    @Override
    public List<EmployeDto> getAll() {
        logger.debug("Récupération de tous les employés");
        List<EmployeDto> employes = employeRepository.findAll().stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} employé(s)", employes.size());
        return employes;
    }

    // --- CRUD Spécialisé (Listage par Rôle) ---

    @Override
    public List<EmployeDto> getSuperviseurs() {
        logger.debug("Récupération de tous les superviseurs");
        List<EmployeDto> superviseurs = employeRepository.findByTypeEmploye(TypeEmploye.SUPERVISEUR).stream()
                .map(employeMapper::toDto).collect(Collectors.toList());
        logger.info("Récupération réussie de {} superviseur(s)", superviseurs.size());
        return superviseurs;
    }

    @Override
    public List<EmployeDto> getCaissiers() {
        logger.debug("Récupération de tous les caissiers");
        List<EmployeDto> caissiers = employeRepository.findByTypeEmploye(TypeEmploye.CAISSIER).stream()
                .map(employeMapper::toDto).collect(Collectors.toList());
        logger.info("Récupération réussie de {} caissier(s)", caissiers.size());
        return caissiers;
    }

    @Override
    public List<EmployeDto> getCollecteurs() {
        logger.debug("Récupération de tous les collecteurs");
        List<EmployeDto> collecteurs = employeRepository.findByTypeEmploye(TypeEmploye.COLLECTEUR).stream()
                .map(employeMapper::toDto).collect(Collectors.toList());
        logger.info("Récupération réussie de {} collecteur(s)", collecteurs.size());
        return collecteurs;
    }

    // --- Fonctions de Listage et Tri Spécialisé (Collecteurs) ---

    @Override
    public List<EmployeDto> getCollecteursOrderedByClientCount() {
        logger.debug("Récupération des collecteurs triés par nombre de clients");
        List<EmployeDto> collecteurs = employeRepository.findCollecteursOrderByClientCountDesc().stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} collecteur(s) triés par nombre de clients", collecteurs.size());
        return collecteurs;
    }

    @Override
    public List<EmployeDto> getCollecteursOrderedByTotalClientScore() {
        logger.debug("Récupération des collecteurs triés par score total des clients");
        List<EmployeDto> collecteurs = employeRepository.findCollecteursOrderByTotalClientScoreDesc().stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} collecteur(s) triés par score total", collecteurs.size());
        return collecteurs;
    }

    // --- Fonctions de Relations ---

    @Override
    public List<EmployeDto> getCollecteursBySuperviseur(String idSuperviseur) {
        logger.debug("Récupération des collecteurs du superviseur - ID: {}", idSuperviseur);
        // Vérification si le superviseur existe et est bien un SUPERVISEUR
        Objects.requireNonNull(idSuperviseur, "idSuperviseur ne doit pas être null");

        // Correction : Convertir le String en Integer
        Integer idSuperviseurInt = Integer.parseInt(idSuperviseur);
        Employe superviseur = employeRepository.findById(idSuperviseurInt)
                .orElseThrow(() -> {
                    logger.warn("Superviseur non trouvé - ID: {}", idSuperviseur);
                    return new RuntimeException("Superviseur non trouvé : " + idSuperviseur);
                });

        if (superviseur.getTypeEmploye() != TypeEmploye.SUPERVISEUR) {
            logger.error("L'employé ID {} n'est pas un superviseur - Type: {}", 
                    idSuperviseur, superviseur.getTypeEmploye());
            throw new IllegalArgumentException("L'employé ID " + idSuperviseur + " n'est pas un superviseur.");
        }

        List<EmployeDto> collecteurs = employeRepository.findBySuperviseurIdEmploye(idSuperviseurInt).stream()
                .map(employeMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} collecteur(s) pour le superviseur ID: {}", 
                collecteurs.size(), idSuperviseur);
        return collecteurs;
    }

    @Override
    public List<ClientDto> getClientsByCollecteur(String idCollecteur) {
        logger.debug("Récupération des clients du collecteur - ID: {}", idCollecteur);
        // Correction : Convertir le String en Integer
        Objects.requireNonNull(idCollecteur, "idCollecteur ne doit pas être null");
        Integer idCollecteurInt = Integer.parseInt(idCollecteur);
        Employe collecteur = employeRepository.findById(idCollecteurInt)
                .orElseThrow(() -> {
                    logger.warn("Collecteur non trouvé - ID: {}", idCollecteur);
                    return new RuntimeException("Collecteur non trouvé : " + idCollecteur);
                });

        if (collecteur.getTypeEmploye() != TypeEmploye.COLLECTEUR) {
            logger.error("L'employé ID {} n'est pas un collecteur - Type: {}", 
                    idCollecteur, collecteur.getTypeEmploye());
            throw new IllegalArgumentException("L'employé ID " + idCollecteur + " n'est pas un collecteur.");
        }

        // Utilisation de la relation ManyToOne/OneToMany via ClientRepository
        List<ClientDto> clients = clientRepository.findByCollecteurAssigneIdEmploye(idCollecteurInt).stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} client(s) pour le collecteur ID: {}", 
                clients.size(), idCollecteur);
        return clients;
    }
}