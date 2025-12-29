package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import com.collecte_epargne.collecte_epargne.mappers.CompteCotisationMapper;
import com.collecte_epargne.collecte_epargne.repositories.CompteCotisationRepository;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.PlanCotisationRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.CompteCotisationInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompteCotisationService implements CompteCotisationInterface {

    private static final Logger logger = LoggerFactory.getLogger(CompteCotisationService.class);

    private final CompteCotisationRepository compteCotisationRepository;
    private final CompteCotisationMapper compteCotisationMapper;
    private final CompteRepository compteRepository;
    private final PlanCotisationRepository planCotisationRepository;

    public CompteCotisationService(CompteCotisationRepository compteCotisationRepository, CompteCotisationMapper compteCotisationMapper, CompteRepository compteRepository, PlanCotisationRepository planCotisationRepository) {
        this.compteCotisationRepository = compteCotisationRepository;
        this.compteCotisationMapper = compteCotisationMapper;
        this.compteRepository = compteRepository;
        this.planCotisationRepository = planCotisationRepository;
    }

    private void assignerRelations(CompteCotisation entity, CompteCotisationDto dto) {
        if (dto.getIdCompte() != null) {
            String idCompte = Objects.requireNonNull(dto.getIdCompte());
            Compte compte = compteRepository.findById(idCompte)
                    .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID : " + dto.getIdCompte()));
            entity.setCompte(compte);
        } else {
            entity.setCompte(null);
        }

        if (dto.getIdPlanCotisation() != null) {
            String idPlan = Objects.requireNonNull(dto.getIdPlanCotisation());
            PlanCotisation planCotisation = planCotisationRepository.findById(idPlan)
                    .orElseThrow(() -> new RuntimeException("Plan de cotisation non trouvé : " + dto.getIdPlanCotisation()));
            entity.setPlanCotisation(planCotisation);
        } else {
            entity.setPlanCotisation(null);
        }
    }

    private void initialiserValeursParDefaut(CompteCotisation entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        if (entity.getDateAdhesion() == null) {
            entity.setDateAdhesion(LocalDate.now());
        }
        if (entity.getMontantTotalVerse() == null) {
            entity.setMontantTotalVerse(BigDecimal.ZERO);
        }
        if (entity.getNombreVersements() == null) {
            entity.setNombreVersements(0);
        }
        if (entity.getNombreRetards() == null) {
            entity.setNombreRetards(0);
        }
        if (entity.getStatut() == null) {
            entity.setStatut(StatutPlanCotisation.ACTIF);
        }
    }

    @Override
    @SuppressWarnings("null")
    public CompteCotisationDto save(CompteCotisationDto dto) {
        logger.info("Début de la création d'un compte de cotisation - Compte: {}, Plan: {}", 
                dto.getIdCompte(), dto.getIdPlanCotisation());
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");
        if (dto.getIdCompte() == null) {
            logger.error("Échec de la création du compte de cotisation : L'identifiant du compte est obligatoire");
            throw new IllegalArgumentException("L'identifiant du compte est obligatoire pour créer un compte de cotisation.");
        }
        if (dto.getIdPlanCotisation() == null) {
            logger.error("Échec de la création du compte de cotisation : L'identifiant du plan de cotisation est obligatoire");
            throw new IllegalArgumentException("L'identifiant du plan de cotisation est obligatoire.");
        }

        CompteCotisation entity = compteCotisationMapper.toEntity(dto);
        initialiserValeursParDefaut(entity);
        assignerRelations(entity, dto);

        CompteCotisation saved = Objects.requireNonNull(compteCotisationRepository.save(entity));
        logger.info("Compte de cotisation créé avec succès - ID: {}, Compte: {}, Plan: {}, Statut: {}", 
                saved.getId(), saved.getCompte().getIdCompte(), saved.getPlanCotisation().getIdPlan(), saved.getStatut());
        return compteCotisationMapper.toDto(saved);
    }

    @Override
    public List<CompteCotisationDto> getAll() {
        logger.debug("Récupération de tous les comptes de cotisation");
        List<CompteCotisationDto> comptes = compteCotisationRepository.findAll().stream()
                .map(compteCotisationMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} compte(s) de cotisation", comptes.size());
        return comptes;
    }

    @Override
    public CompteCotisationDto getById(String id) {
        logger.debug("Récupération du compte de cotisation avec l'ID: {}", id);
        Objects.requireNonNull(id, "id ne doit pas être null");
        CompteCotisation compteCotisation = compteCotisationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Compte de cotisation non trouvé avec l'ID: {}", id);
                    return new RuntimeException("Compte de cotisation non trouvé : " + id);
                });
        logger.info("Compte de cotisation récupéré avec succès - ID: {}, Statut: {}", 
                compteCotisation.getId(), compteCotisation.getStatut());
        return compteCotisationMapper.toDto(compteCotisation);
    }

    @Override
    public CompteCotisationDto update(String id, CompteCotisationDto dto) {
        logger.info("Début de la mise à jour du compte de cotisation - ID: {}, Statut: {}", id, dto.getStatut());
        Objects.requireNonNull(id, "id ne doit pas être null");
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");
        CompteCotisation existing = compteCotisationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentative de mise à jour d'un compte de cotisation inexistant - ID: {}", id);
                    return new RuntimeException("Compte de cotisation non trouvé pour mise à jour : " + id);
                });

        logger.debug("Mise à jour des champs du compte de cotisation - ID: {}", id);
        existing.setDateAdhesion(dto.getDateAdhesion());
        existing.setMontantTotalVerse(dto.getMontantTotalVerse());
        existing.setNombreVersements(dto.getNombreVersements());
        existing.setNombreRetards(dto.getNombreRetards());
        existing.setProchaineEcheance(dto.getProchaineEcheance());
        existing.setStatut(dto.getStatut());

        assignerRelations(existing, dto);

        CompteCotisation updated = compteCotisationRepository.save(existing);
        logger.info("Compte de cotisation mis à jour avec succès - ID: {}, Montant total versé: {}, Statut: {}", 
                updated.getId(), updated.getMontantTotalVerse(), updated.getStatut());
        return compteCotisationMapper.toDto(updated);
    }

    @Override
    public void delete(String id) {
        logger.info("Début de la suppression du compte de cotisation - ID: {}", id);
        Objects.requireNonNull(id, "id ne doit pas être null");
        if (!compteCotisationRepository.existsById(id)) {
            logger.warn("Tentative de suppression d'un compte de cotisation inexistant - ID: {}", id);
            throw new RuntimeException("Compte de cotisation inexistant : " + id);
        }
        compteCotisationRepository.deleteById(id);
        logger.info("Compte de cotisation supprimé avec succès - ID: {}", id);
    }

    @Override
    public List<CompteCotisationDto> getByCompte(String idCompte) {
        logger.debug("Récupération des comptes de cotisation pour le compte - ID: {}", idCompte);
        Objects.requireNonNull(idCompte, "idCompte ne doit pas être null");
        List<CompteCotisationDto> comptes = compteCotisationRepository.findByCompteIdCompte(idCompte).stream()
                .map(compteCotisationMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} compte(s) de cotisation pour le compte ID: {}", 
                comptes.size(), idCompte);
        return comptes;
    }

    @Override
    public List<CompteCotisationDto> getByPlanCotisation(String idPlanCotisation) {
        logger.debug("Récupération des comptes de cotisation pour le plan - ID: {}", idPlanCotisation);
        Objects.requireNonNull(idPlanCotisation, "idPlanCotisation ne doit pas être null");
        List<CompteCotisationDto> comptes = compteCotisationRepository.findByPlanCotisationIdPlan(idPlanCotisation).stream()
                .map(compteCotisationMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} compte(s) de cotisation pour le plan ID: {}", 
                comptes.size(), idPlanCotisation);
        return comptes;
    }
}

