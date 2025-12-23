package com.collecte_epargne.collecte_epargne.services.implementations;


import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.mappers.AgenceZoneMapper;
import com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.AgenceZoneInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@Service
@AllArgsConstructor
public class AgenceZoneService implements AgenceZoneInterface {


    private AgenceZoneRepository agenceZoneRepository;
    private AgenceZoneMapper agenceZoneMapper;
    @Override
    @SuppressWarnings("null")
    public AgenceZoneDto save(AgenceZoneDto agenceZoneDto) {
        Objects.requireNonNull(agenceZoneDto, "agenceZoneDto ne doit pas être null");
        if(agenceZoneDto.getCode().isEmpty() && agenceZoneDto.getNom().isEmpty() || agenceZoneDto.getNom()==null) {

            throw new RuntimeException("Données incorret");

        }else{
            AgenceZone agenceZone = agenceZoneRepository.save(agenceZoneMapper.toEntity(agenceZoneDto));
            return agenceZoneMapper.toDto(agenceZone);
        }
    }

    @Override
    public List<AgenceZoneDto> getAll() {
        return agenceZoneRepository.findAll().stream().map(
                agenceZoneMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AgenceZoneDto getById(Integer idAgence) {
        Objects.requireNonNull(idAgence, "idAgence ne doit pas être null");
        AgenceZone agenceZone = agenceZoneRepository.findById(idAgence)
                .orElseThrow(() -> new RuntimeException("Agence non trouvée"));
        return agenceZoneMapper.toDto(agenceZone);
    }

    @Override
    public AgenceZoneDto update(Integer idAgence, AgenceZoneDto agenceZoneDto) {
        Objects.requireNonNull(idAgence, "idAgence ne doit pas être null");
        Objects.requireNonNull(agenceZoneDto, "agenceZoneDto ne doit pas être null");
        AgenceZone agenceZone = agenceZoneRepository.findById(idAgence)
                .orElseThrow(() -> new RuntimeException("agence non trouvée"));
        agenceZone.setAdresse(agenceZoneDto.getAdresse());
        agenceZone.setCode(agenceZoneDto.getCode());
        agenceZone.setNom(agenceZoneDto.getNom());
        agenceZone.setTelephone(agenceZoneDto.getTelephone());
        agenceZone.setStatut(agenceZoneDto.getStatut());
        agenceZone.setQuartier(agenceZoneDto.getQuartier());
        agenceZone.setVille(agenceZoneDto.getVille());
        agenceZone.setDescription(agenceZoneDto.getDescription());
        agenceZoneRepository.save(agenceZone);
        return agenceZoneMapper.toDto(agenceZone);
    }

    @Override
    public void delete(Integer idAgence) {
        Objects.requireNonNull(idAgence, "idAgence ne doit pas être null");
        boolean exist = agenceZoneRepository.existsById(idAgence);
        if(!exist){
            throw new RuntimeException("Agence inexistante");
        }else{
            agenceZoneRepository.deleteById(idAgence);
        }
    }
}
