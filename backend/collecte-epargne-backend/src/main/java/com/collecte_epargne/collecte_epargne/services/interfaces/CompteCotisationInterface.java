package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;

import java.util.List;

public interface CompteCotisationInterface {

    CompteCotisationDto save(CompteCotisationDto dto);

    List<CompteCotisationDto> getAll();

    CompteCotisationDto getById(String id);

    CompteCotisationDto update(String id, CompteCotisationDto dto);

    void delete(String id);

    List<CompteCotisationDto> getByCompte(String idCompte);

    List<CompteCotisationDto> getByPlanCotisation(String idPlanCotisation);
}

