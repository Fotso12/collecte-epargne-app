package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.RapportCollecteurDto;

import java.util.List;

public interface RapportCollecteurInterface {


    RapportCollecteurDto create(RapportCollecteurDto dto);

    List<RapportCollecteurDto> getAll();

    RapportCollecteurDto getById(String idRapport);

    RapportCollecteurDto update(String idRapport, RapportCollecteurDto dto);

    void delete(String id);
}
