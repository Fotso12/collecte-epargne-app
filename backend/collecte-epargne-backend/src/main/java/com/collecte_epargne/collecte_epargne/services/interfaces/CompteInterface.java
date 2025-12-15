package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.CompteDto;

import java.util.List;

public interface CompteInterface {

    CompteDto save(CompteDto compteDto);

    List<CompteDto> getAll();

    CompteDto getById(String idCompte);

    CompteDto update(String idCompte, CompteDto compteDto);

    void delete(String idCompte);

    CompteDto getByNumCompte(String numCompte);

    List<CompteDto> getByClient(String codeClient);
}
