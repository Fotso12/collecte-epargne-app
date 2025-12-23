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
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");
        if (dto.getNom() == null || dto.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom du plan est obligatoire.");
        }
        if (dto.getMontantAttendu() == null) {
            throw new IllegalArgumentException("Le montant attendu est obligatoire.");
        }
        if (dto.getFrequence() == null) {
            throw new IllegalArgumentException("La fréquence est obligatoire.");
        }
        if (dto.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire.");
        }

        planCotisationRepository.findByNom(dto.getNom()).ifPresent(plan -> {
            throw new RuntimeException("Un plan de cotisation avec ce nom existe déjà.");
        });

        PlanCotisation entity = planCotisationMapper.toEntity(dto);
        appliquerValeursParDefaut(entity);
        recalculerDateFinSiNecessaire(entity);

        PlanCotisation saved = planCotisationRepository.save(entity);
        return planCotisationMapper.toDto(saved);
    }

    @Override
    public List<PlanCotisationDto> getAll() {
        return planCotisationRepository.findAll().stream()
                .map(planCotisationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlanCotisationDto getById(String id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        PlanCotisation planCotisation = planCotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan de cotisation non trouvé : " + id));
        return planCotisationMapper.toDto(planCotisation);
    }

    @Override
    public PlanCotisationDto update(String id, PlanCotisationDto dto) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");

        PlanCotisation existing = planCotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan de cotisation non trouvé pour mise à jour : " + id));

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
        return planCotisationMapper.toDto(updated);
    }

    @Override
    public void delete(String id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        if (!planCotisationRepository.existsById(id)) {
            throw new RuntimeException("Plan de cotisation inexistant : " + id);
        }
        if (compteCotisationRepository.existsByPlanCotisationIdPlan(id)) {
            throw new RuntimeException("Impossible de supprimer : des comptes de cotisation utilisent ce plan.");
        }
        planCotisationRepository.deleteById(id);
    }
}


