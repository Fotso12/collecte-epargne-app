package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.RapportCollecteurDto;
import com.collecte_epargne.collecte_epargne.entities.RapportCollecteur;
import com.collecte_epargne.collecte_epargne.mappers.RapportCollecteurMapper;
import com.collecte_epargne.collecte_epargne.repositories.RapportCollecteurRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.RapportCollecteurInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class RapportCollecteurService implements RapportCollecteurInterface {

    private final RapportCollecteurRepository rapportCollecteurRepository;
    private final RapportCollecteurMapper mapper;

    public RapportCollecteurService(RapportCollecteurRepository rapportCollecteurRepository,
                                        RapportCollecteurMapper mapper) {
        this.rapportCollecteurRepository = rapportCollecteurRepository;
        this.mapper = mapper;
    }

    @Override
    public RapportCollecteurDto create(RapportCollecteurDto dto) {
        RapportCollecteur entity = mapper.toEntity(dto);
        RapportCollecteur saved = rapportCollecteurRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public List<RapportCollecteurDto> getAll() {
        return rapportCollecteurRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RapportCollecteurDto getById(String idRapport) {
        RapportCollecteur entity = rapportCollecteurRepository.findByIdRapport(idRapport)
                .orElseThrow(() -> new RuntimeException("Rapport collecteur introuvable avec l'id : " + idRapport));
        return mapper.toDto(entity);
    }

    @Override
    public RapportCollecteurDto update(String idRapport, RapportCollecteurDto dto) {
//        RapportCollecteur entity = rapportCollecteurRepository.findByIdRapport(idRapport);
                //.orElseThrow(() -> new RuntimeException("Rapport collecteur introuvable avec l'id : " + idRapport));

//        entity.set(dto.getNomCollecteur());
//        entity.setMontantCollecte(dto.getMontantCollecte());
//        entity.setDateRapport(dto.getDateRapport());

//        RapportCollecteur updated = rapportCollecteurRepository.save(entity);
        return dto;//"mapper.toDto(updated)";
    }

    @Override
    public void delete(String id) {
        if (!rapportCollecteurRepository.existsById(id)) {
            throw new RuntimeException("Rapport collecteur introuvable avec l'id : " + id);
        }
        rapportCollecteurRepository.deleteById(id);
    }
}
