package com.collecte_epargne.collecte_epargne.services.implementations;


import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import com.collecte_epargne.collecte_epargne.mappers.CompteCotisationMapper;
import com.collecte_epargne.collecte_epargne.repositories.CompteCotisationRepository;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.PlanCotisationRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.CompteCotisationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CompteCotisationService implements CompteCotisationInterface {

    private static final Logger log = LoggerFactory.getLogger(CompteCotisationService.class);

    private final CompteCotisationRepository compteCotisationRepository;
    private final CompteCotisationMapper compteCotisationMapper;
    private final CompteRepository compteRepository;
    private final PlanCotisationRepository planCotisationRepository;

    public CompteCotisationService(CompteCotisationRepository compteCotisationRepository,
                                   CompteCotisationMapper compteCotisationMapper,
                                   CompteRepository compteRepository,
                                   PlanCotisationRepository planCotisationRepository) {
        this.compteCotisationRepository = compteCotisationRepository;
        this.compteCotisationMapper = compteCotisationMapper;
        this.compteRepository = compteRepository;
        this.planCotisationRepository = planCotisationRepository;
    }

    @Override
    public CompteCotisationDto save(CompteCotisationDto dto) {
        log.info("Début création compte cotisation");

        Objects.requireNonNull(dto, "Le payload ne peut pas être null");

        if (dto.getIdCompte() == null || dto.getIdPlanCotisation() == null) {
            log.error("Données invalides : idCompte ou idPlanCotisation manquant");
            throw new IllegalArgumentException("Compte et plan de cotisation obligatoires");
        }

        CompteCotisation entity = compteCotisationMapper.toEntity(dto);
        initialiserValeursParDefaut(entity);
        assignerRelations(entity, dto);

        CompteCotisation saved = compteCotisationRepository.save(entity);

        log.info("Compte cotisation créé avec succès, id={}", saved.getId());
        return compteCotisationMapper.toDto(saved);
    }

    private void assignerRelations(CompteCotisation entity, CompteCotisationDto dto) {
    }

    private void initialiserValeursParDefaut(CompteCotisation entity) {
    }

    @Override
    public List<CompteCotisationDto> getAll() {
        log.info("Récupération de tous les comptes cotisation");
        return compteCotisationRepository.findAll().stream()
                .map(compteCotisationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompteCotisationDto getById(String id) {
        log.info("Recherche compte cotisation id={}", id);
        CompteCotisation cc = compteCotisationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Compte cotisation non trouvé : {}", id);
                    return new RuntimeException("Compte de cotisation non trouvé");
                });
        return compteCotisationMapper.toDto(cc);
    }

    @Override
    public CompteCotisationDto update(String id, CompteCotisationDto dto) {
        log.info("Mise à jour compte cotisation {}", id);

        CompteCotisation existing = compteCotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte de cotisation non trouvé"));

        existing.setDateAdhesion(dto.getDateAdhesion());
        existing.setMontantTotalVerse(dto.getMontantTotalVerse());
        existing.setNombreVersements(dto.getNombreVersements());
        existing.setNombreRetards(dto.getNombreRetards());
        existing.setProchaineEcheance(dto.getProchaineEcheance());
        existing.setStatut(dto.getStatut());

        assignerRelations(existing, dto);

        CompteCotisation updated = compteCotisationRepository.save(existing);
        log.info("Compte cotisation mis à jour avec succès : {}", id);

        return compteCotisationMapper.toDto(updated);
    }

    @Override
    public void delete(String id) {
        log.info("Suppression compte cotisation {}", id);

        if (!compteCotisationRepository.existsById(id)) {
            log.warn("Suppression impossible, compte cotisation inexistant : {}", id);
            throw new RuntimeException("Compte de cotisation inexistant");
        }

        compteCotisationRepository.deleteById(id);
        log.info("Compte cotisation supprimé : {}", id);
    }

    @Override
    public List<CompteCotisationDto> getByCompte(String idCompte) {
        return null;
    }

    @Override
    public List<CompteCotisationDto> getByPlanCotisation(String idPlanCotisation) {
        return null;
    }
}
