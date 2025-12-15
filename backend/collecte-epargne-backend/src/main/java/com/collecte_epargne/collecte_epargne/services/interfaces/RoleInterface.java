package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.RoleDto;

import java.util.List;

public interface RoleInterface {

    RoleDto save(RoleDto roleDto);

    List<RoleDto> getAll();

    RoleDto getById(Integer id);

    RoleDto update(Integer id, RoleDto roleDto);

    void delete(Integer id);
}