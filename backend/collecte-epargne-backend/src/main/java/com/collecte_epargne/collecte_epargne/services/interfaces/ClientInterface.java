package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;

import java.util.List;

public interface ClientInterface {
    ClientDto save(ClientDto clientDto);

    List<ClientDto> getAll();

    ClientDto getById(Long numeroClient);

    ClientDto update(Long numeroClient, ClientDto clientDto);

    void delete(Long numClient);

    // Ajout de fonctions de recherche spécifiques si nécessaire
    ClientDto getByNumeroClient(Long numeroClient);

    ClientDto getByCodeClient(String codeClient);

    ClientDto updateByCodeClient(String codeClient, ClientDto clientDto);

    void deleteByCodeClient(String codeClient);
}
