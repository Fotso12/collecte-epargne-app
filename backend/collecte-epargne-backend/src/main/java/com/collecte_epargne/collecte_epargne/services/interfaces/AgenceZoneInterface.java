package com.collecte_epargne.collecte_epargne.services.interfaces;


import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;

import java.util.List;

public interface AgenceZoneInterface {

    AgenceZoneDto save(AgenceZoneDto agenceZoneDto);

    List<AgenceZoneDto> getAll();

    AgenceZoneDto getById(Integer idAgence);

    AgenceZoneDto update(Integer idAgence ,AgenceZoneDto agenceZoneDto);

    void delete (Integer  idAgence);

}
