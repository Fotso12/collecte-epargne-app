package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;

import java.util.List;

public interface ClientInterface {
    ClientDto save(ClientDto clientDto);

    List<ClientDto> getAll();

    ClientDto getById(String codeClient);

    ClientDto update(String codeClient, ClientDto clientDto);

    void delete(String codeClient);

    // Ajout de fonctions de recherche spécifiques si nécessaire
    ClientDto getByNumeroClient(String numeroClient);
}
