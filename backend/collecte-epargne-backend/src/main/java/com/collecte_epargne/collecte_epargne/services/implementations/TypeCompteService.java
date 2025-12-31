package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.TypeCompteDto;
import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import com.collecte_epargne.collecte_epargne.mappers.TypeCompteMapper;
import com.collecte_epargne.collecte_epargne.repositories.TypeCompteRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.TypeCompteInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class TypeCompteService implements TypeCompteInterface {

    private final TypeCompteRepository typeCompteRepository;
    private final TypeCompteMapper typeCompteMapper;

    private static final Logger log = LoggerFactory.getLogger(TypeCompteService.class);

    public TypeCompteService(TypeCompteRepository typeCompteRepository, TypeCompteMapper typeCompteMapper) {
        this.typeCompteRepository = typeCompteRepository;
        this.typeCompteMapper = typeCompteMapper;
    }

    @Override
    @SuppressWarnings("null")
    public TypeCompteDto save(TypeCompteDto typeCompteDto) {
        log.info("Sauvegarde de type de compte avec code: {}", typeCompteDto.getCode());
        Objects.requireNonNull(typeCompteDto, "typeCompteDto ne doit pas être null");
        if (typeCompteDto.getCode() == null || typeCompteDto.getCode().isEmpty()) {
            throw new IllegalArgumentException("Le code du type de compte est obligatoire.");
        }

        // Vérification de l'unicité du code
        if (typeCompteRepository.findByCode(typeCompteDto.getCode()).isPresent()) {
            throw new RuntimeException("Un type de compte avec ce code existe déjà.");
        }

        TypeCompte typeCompteToSave = typeCompteMapper.toEntity(typeCompteDto);
        TypeCompte savedTypeCompte = typeCompteRepository.save(typeCompteToSave);
        log.info("Type de compte sauvegardé avec succès avec code: {}", savedTypeCompte.getCode());
        return typeCompteMapper.toDto(savedTypeCompte);
    }

    @Override
    public List<TypeCompteDto> getAll() {
        log.info("Récupération de tous les types de compte");
        return typeCompteRepository.findAll().stream()
                .map(typeCompteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TypeCompteDto getById(Integer id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        log.info("Récupération de type de compte avec id: {}", id);
        TypeCompte typeCompte = typeCompteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type de compte non trouvé avec l'ID : " + id));
        return typeCompteMapper.toDto(typeCompte);
    }

    @Override
    public TypeCompteDto update(Integer id, TypeCompteDto typeCompteDto) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        Objects.requireNonNull(typeCompteDto, "typeCompteDto ne doit pas être null");
        log.info("Mise à jour de type de compte avec id: {}", id);
        TypeCompte existingTypeCompte = typeCompteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type de compte non trouvé pour la mise à jour : " + id));

        // Mettre à jour les champs
        existingTypeCompte.setCode(typeCompteDto.getCode());
        existingTypeCompte.setNom(typeCompteDto.getNom());
        existingTypeCompte.setDescription(typeCompteDto.getDescription());
        existingTypeCompte.setTauxInteret(typeCompteDto.getTauxInteret());
        existingTypeCompte.setSoldeMinimum(typeCompteDto.getSoldeMinimum());
        existingTypeCompte.setFraisOuverture(typeCompteDto.getFraisOuverture());
        existingTypeCompte.setFraisCloture(typeCompteDto.getFraisCloture());
        existingTypeCompte.setAutoriserRetrait(typeCompteDto.getAutoriserRetrait());
        existingTypeCompte.setDureeBlocageJours(typeCompteDto.getDureeBlocageJours());

        TypeCompte updatedTypeCompte = typeCompteRepository.save(existingTypeCompte);
        log.info("Type de compte mis à jour avec succès avec id: {}", updatedTypeCompte.getId());
        return typeCompteMapper.toDto(updatedTypeCompte);
    }

    @Override
    public void delete(Integer id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        log.info("Suppression de type de compte avec id: {}", id);
        if (!typeCompteRepository.existsById(id)) {
            throw new RuntimeException("Type de compte inexistant : " + id);
        }
        typeCompteRepository.deleteById(id);
        log.info("Type de compte supprimé avec id: {}", id);
    }

    @Override
    public TypeCompteDto getByCode(String code) {
        Objects.requireNonNull(code, "code ne doit pas être null");
        log.info("Récupération de type de compte avec code: {}", code);
        TypeCompte typeCompte = typeCompteRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Type de compte non trouvé avec le code : " + code));
        return typeCompteMapper.toDto(typeCompte);
    }
}
