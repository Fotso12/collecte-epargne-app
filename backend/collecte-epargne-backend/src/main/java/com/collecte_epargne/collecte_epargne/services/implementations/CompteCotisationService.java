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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
@Service
@AllArgsConstructor
public class CompteCotisationService implements CompteCotisationInterface {

    private final CompteCotisationRepository compteCotisationRepository;
    private final CompteCotisationMapper compteCotisationMapper;
    private final CompteRepository compteRepository;
    private final PlanCotisationRepository planCotisationRepository;

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
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");
        if (dto.getIdCompte() == null) {
            throw new IllegalArgumentException("L'identifiant du compte est obligatoire pour créer un compte de cotisation.");
        }
        if (dto.getIdPlanCotisation() == null) {
            throw new IllegalArgumentException("L'identifiant du plan de cotisation est obligatoire.");
        }

        CompteCotisation entity = compteCotisationMapper.toEntity(dto);
        initialiserValeursParDefaut(entity);
        assignerRelations(entity, dto);

        CompteCotisation saved = Objects.requireNonNull(compteCotisationRepository.save(entity));
        return compteCotisationMapper.toDto(saved);
    }

    @Override
    public List<CompteCotisationDto> getAll() {
        return compteCotisationRepository.findAll().stream()
                .map(compteCotisationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompteCotisationDto getById(String id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        CompteCotisation compteCotisation = compteCotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte de cotisation non trouvé : " + id));
        return compteCotisationMapper.toDto(compteCotisation);
    }

    @Override
    public CompteCotisationDto update(String id, CompteCotisationDto dto) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        Objects.requireNonNull(dto, "Le payload ne peut pas être null");
        CompteCotisation existing = compteCotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte de cotisation non trouvé pour mise à jour : " + id));

        existing.setDateAdhesion(dto.getDateAdhesion());
        existing.setMontantTotalVerse(dto.getMontantTotalVerse());
        existing.setNombreVersements(dto.getNombreVersements());
        existing.setNombreRetards(dto.getNombreRetards());
        existing.setProchaineEcheance(dto.getProchaineEcheance());
        existing.setStatut(dto.getStatut());

        assignerRelations(existing, dto);

        CompteCotisation updated = compteCotisationRepository.save(existing);
        return compteCotisationMapper.toDto(updated);
    }

    @Override
    public void delete(String id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        if (!compteCotisationRepository.existsById(id)) {
            throw new RuntimeException("Compte de cotisation inexistant : " + id);
        }
        compteCotisationRepository.deleteById(id);
    }

    @Override
    public List<CompteCotisationDto> getByCompte(String idCompte) {
        Objects.requireNonNull(idCompte, "idCompte ne doit pas être null");
        return compteCotisationRepository.findByCompteIdCompte(idCompte).stream()
                .map(compteCotisationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompteCotisationDto> getByPlanCotisation(String idPlanCotisation) {
        Objects.requireNonNull(idPlanCotisation, "idPlanCotisation ne doit pas être null");
        return compteCotisationRepository.findByPlanCotisationIdPlan(idPlanCotisation).stream()
                .map(compteCotisationMapper::toDto)
                .collect(Collectors.toList());
    }
}

