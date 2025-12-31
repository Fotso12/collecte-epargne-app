package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.PlanCotisationDto;

import java.util.List;

public interface PlanCotisationInterface {

    PlanCotisationDto save(PlanCotisationDto dto);

    List<PlanCotisationDto> getAll();

    PlanCotisationDto getById(String id);

    PlanCotisationDto update(String id, PlanCotisationDto dto);

    void delete(String id);
}


