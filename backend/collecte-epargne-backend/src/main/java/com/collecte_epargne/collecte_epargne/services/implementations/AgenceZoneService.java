package com.collecte_epargne.collecte_epargne.services.implementations;


import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.mappers.AgenceZoneMapper;
import com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.AgenceZoneInterface;
import com.collecte_epargne.collecte_epargne.utils.CodeGenerator;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


    @Service
    public class AgenceZoneService implements AgenceZoneInterface {

        private static final Logger log = LoggerFactory.getLogger(AgenceZoneService.class);

        private final AgenceZoneRepository agenceZoneRepository;
        private final AgenceZoneMapper agenceZoneMapper;
        private final CodeGenerator codeGenerator;

        public AgenceZoneService(AgenceZoneRepository agenceZoneRepository,
                                 AgenceZoneMapper agenceZoneMapper,
                                 CodeGenerator codeGenerator) {
            this.agenceZoneRepository = agenceZoneRepository;
            this.agenceZoneMapper = agenceZoneMapper;
            this.codeGenerator = codeGenerator;
        }

        @Override
        public AgenceZoneDto save(AgenceZoneDto agenceZoneDto) {
            log.info("Début création AgenceZone : {}", agenceZoneDto);

            Objects.requireNonNull(agenceZoneDto, "agenceZoneDto ne doit pas être null");

            if ((agenceZoneDto.getCode().isEmpty() && agenceZoneDto.getNom().isEmpty())
                    || agenceZoneDto.getNom() == null) {

                log.warn("Données invalides pour création AgenceZone : {}", agenceZoneDto);
                throw new RuntimeException("Données incorrectes");
            }

            if(agenceZoneDto.getCode() == null || agenceZoneDto.getCode().isEmpty()) {
                agenceZoneDto.setCode(codeGenerator.generateAgenceCode(agenceZoneDto.getVille()));
            }

            AgenceZone agenceZone = agenceZoneMapper.toEntity(agenceZoneDto);
            agenceZone.setPosition(agenceZoneDto.getPosition());
            
            agenceZone = agenceZoneRepository.save(agenceZone);

            log.info("AgenceZone créée avec succès, id={}", agenceZone.getId());

            AgenceZoneDto dto = agenceZoneMapper.toDto(agenceZone);
            dto.setPosition(agenceZone.getPosition());
            return dto;
        }

        @Override
        public List<AgenceZoneDto> getAll() {
            log.info("Récupération de toutes les AgenceZone");

            List<AgenceZoneDto> result = agenceZoneRepository.findAll()
                    .stream()
                    .map(az -> {
                        AgenceZoneDto dto = agenceZoneMapper.toDto(az);
                        dto.setPosition(az.getPosition());
                        return dto;
                    })
                    .collect(Collectors.toList());

            log.info("Nombre d’AgenceZone trouvées : {}", result.size());

            return result;
        }

        @Override
        public AgenceZoneDto getById(Integer idAgence) {
            log.info("Recherche AgenceZone avec id={}", idAgence);

            Objects.requireNonNull(idAgence, "idAgence ne doit pas être null");

            AgenceZone agenceZone = agenceZoneRepository.findById(idAgence)
                    .orElseThrow(() -> {
                        log.error("AgenceZone non trouvée avec id={}", idAgence);
                        return new RuntimeException("Agence non trouvée");
                    });

            log.info("AgenceZone trouvée : {}", agenceZone.getId());

            return agenceZoneMapper.toDto(agenceZone);
        }

        @Override
        public AgenceZoneDto update(Integer idAgence, AgenceZoneDto agenceZoneDto) {
            log.info("Début mise à jour AgenceZone id={}", idAgence);

            Objects.requireNonNull(idAgence, "idAgence ne doit pas être null");
            Objects.requireNonNull(agenceZoneDto, "agenceZoneDto ne doit pas être null");

            AgenceZone agenceZone = agenceZoneRepository.findById(idAgence)
                    .orElseThrow(() -> {
                        log.error("Tentative de mise à jour d’une AgenceZone inexistante id={}", idAgence);
                        return new RuntimeException("Agence non trouvée");
                    });

            agenceZone.setAdresse(agenceZoneDto.getAdresse());
            agenceZone.setCode(agenceZoneDto.getCode());
            agenceZone.setNom(agenceZoneDto.getNom());
            agenceZone.setTelephone(agenceZoneDto.getTelephone());
            agenceZone.setStatut(agenceZoneDto.getStatut());
            agenceZone.setQuartier(agenceZoneDto.getQuartier());
            agenceZone.setVille(agenceZoneDto.getVille());
            agenceZone.setDescription(agenceZoneDto.getDescription());
            agenceZone.setPosition(agenceZoneDto.getPosition());

            agenceZoneRepository.save(agenceZone);

            log.info("AgenceZone mise à jour avec succès id={}", idAgence);

            return agenceZoneMapper.toDto(agenceZone);
        }

        @Override
        public void delete(Integer idAgence) {
            log.info("Suppression AgenceZone id={}", idAgence);

            Objects.requireNonNull(idAgence, "idAgence ne doit pas être null");

            boolean exist = agenceZoneRepository.existsById(idAgence);

            if (!exist) {
                log.warn("Tentative de suppression d’une AgenceZone inexistante id={}", idAgence);
                throw new RuntimeException("Agence inexistante");
            }

            agenceZoneRepository.deleteById(idAgence);

            log.info("AgenceZone supprimée avec succès id={}", idAgence);
        }
    }
