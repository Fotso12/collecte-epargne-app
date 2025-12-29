package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.PlanCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import com.collecte_epargne.collecte_epargne.mappers.PlanCotisationMapper;
import com.collecte_epargne.collecte_epargne.repositories.CompteCotisationRepository;
import com.collecte_epargne.collecte_epargne.repositories.PlanCotisationRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.PlanCotisationInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
@Service
@AllArgsConstructor
public class PlanCotisationService implements PlanCotisationInterface {

    private static final Logger logger = LoggerFactory.getLogger(PlanCotisationService.class);

    private final PlanCotisationRepository planCotisationRepository;
    private final PlanCotisationMapper planCotisationMapper;
    private final CompteCotisationRepository compteCotisationRepository;

    private void appliquerValeursParDefaut(PlanCotisation entity) {
        if (entity.getIdPlan() == null || entity.getIdPlan().isEmpty()) {
            entity.setIdPlan(UUID.randomUUID().toString());
        }
        if (entity.getStatut() == null) {
            entity.setStatut(StatutPlanCotisation.ACTIF);
        }
    }

    private void recalculerDateFinSiNecessaire(PlanCotisation entity) {
        if (entity.getDateFin() == null && entity.getDateDebut() != null && entity.getDureeJours() != null) {
            entity.setDateFin(entity.getDateDebut().plusDays(entity.getDureeJours()));
        }
    }

    @Override
    @SuppressWarnings("null")
    public PlanCotisationDto save(PlanCotisationDto dto) {
        logger.info("Début de la création d'un plan de cotisation - Nom: {}", dto.getNom());
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");
        if (dto.getNom() == null || dto.getNom().isEmpty()) {
            logger.error("Échec de la création du plan de cotisation : Le nom est obligatoire");
            throw new IllegalArgumentException("Le nom du plan est obligatoire.");
        }
        if (dto.getMontantAttendu() == null) {
            logger.error("Échec de la création du plan de cotisation : Le montant attendu est obligatoire");
            throw new IllegalArgumentException("Le montant attendu est obligatoire.");
        }
        if (dto.getFrequence() == null) {
            logger.error("Échec de la création du plan de cotisation : La fréquence est obligatoire");
            throw new IllegalArgumentException("La fréquence est obligatoire.");
        }
        if (dto.getDateDebut() == null) {
            logger.error("Échec de la création du plan de cotisation : La date de début est obligatoire");
            throw new IllegalArgumentException("La date de début est obligatoire.");
        }

        planCotisationRepository.findByNom(dto.getNom()).ifPresent(plan -> {
            logger.warn("Tentative de création d'un plan de cotisation avec un nom existant : {}", dto.getNom());
            throw new RuntimeException("Un plan de cotisation avec ce nom existe déjà.");
        });

        PlanCotisation entity = planCotisationMapper.toEntity(dto);
        appliquerValeursParDefaut(entity);
        recalculerDateFinSiNecessaire(entity);

        PlanCotisation saved = planCotisationRepository.save(entity);
        logger.info("Plan de cotisation créé avec succès - ID: {}, Nom: {}, Montant: {}", 
                saved.getIdPlan(), saved.getNom(), saved.getMontantAttendu());
        return planCotisationMapper.toDto(saved);
    }

    @Override
    public List<PlanCotisationDto> getAll() {
        logger.debug("Récupération de tous les plans de cotisation");
        List<PlanCotisationDto> plans = planCotisationRepository.findAll().stream()
                .map(planCotisationMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Récupération réussie de {} plan(s) de cotisation", plans.size());
        return plans;
    }

    @Override
    public PlanCotisationDto getById(String id) {
        logger.debug("Récupération du plan de cotisation avec l'ID: {}", id);
        Objects.requireNonNull(id, "id ne doit pas être null");
        PlanCotisation planCotisation = planCotisationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Plan de cotisation non trouvé avec l'ID: {}", id);
                    return new RuntimeException("Plan de cotisation non trouvé : " + id);
                });
        logger.info("Plan de cotisation récupéré avec succès - ID: {}, Nom: {}", 
                planCotisation.getIdPlan(), planCotisation.getNom());
        return planCotisationMapper.toDto(planCotisation);
    }

    @Override
    public PlanCotisationDto update(String id, PlanCotisationDto dto) {
        logger.info("Début de la mise à jour du plan de cotisation - ID: {}, Nom: {}", id, dto.getNom());
        Objects.requireNonNull(id, "id ne doit pas être null");
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");

        PlanCotisation existing = planCotisationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentative de mise à jour d'un plan de cotisation inexistant - ID: {}", id);
                    return new RuntimeException("Plan de cotisation non trouvé pour mise à jour : " + id);
                });

        logger.debug("Mise à jour des champs du plan de cotisation - ID: {}", id);
        existing.setNom(dto.getNom());
        existing.setMontantAttendu(dto.getMontantAttendu());
        existing.setFrequence(dto.getFrequence());
        existing.setDureeJours(dto.getDureeJours());
        existing.setDateDebut(dto.getDateDebut());
        existing.setDateFin(dto.getDateFin());
        existing.setTauxPenaliteRetard(dto.getTauxPenaliteRetard());
        existing.setStatut(dto.getStatut());

        appliquerValeursParDefaut(existing);
        recalculerDateFinSiNecessaire(existing);

        PlanCotisation updated = planCotisationRepository.save(existing);
        logger.info("Plan de cotisation mis à jour avec succès - ID: {}, Nom: {}, Statut: {}", 
                updated.getIdPlan(), updated.getNom(), updated.getStatut());
        return planCotisationMapper.toDto(updated);
    }

    @Override
    public void delete(String id) {
        logger.info("Début de la suppression du plan de cotisation - ID: {}", id);
        Objects.requireNonNull(id, "id ne doit pas être null");
        if (!planCotisationRepository.existsById(id)) {
            logger.warn("Tentative de suppression d'un plan de cotisation inexistant - ID: {}", id);
            throw new RuntimeException("Plan de cotisation inexistant : " + id);
        }
        if (compteCotisationRepository.existsByPlanCotisationIdPlan(id)) {
            logger.error("Impossible de supprimer le plan de cotisation - ID: {} - Des comptes de cotisation l'utilisent", id);
            throw new RuntimeException("Impossible de supprimer : des comptes de cotisation utilisent ce plan.");
        }
        planCotisationRepository.deleteById(id);
        logger.info("Plan de cotisation supprimé avec succès - ID: {}", id);
    }
}


