package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.TypeCompteDto;

import java.util.List;

public interface TypeCompteInterface {

    TypeCompteDto save(TypeCompteDto typeCompteDto);

    List<TypeCompteDto> getAll();

    TypeCompteDto getById(Integer id);

    TypeCompteDto update(Integer id, TypeCompteDto typeCompteDto);

    void delete(Integer id);

    TypeCompteDto getByCode(String code);
}
