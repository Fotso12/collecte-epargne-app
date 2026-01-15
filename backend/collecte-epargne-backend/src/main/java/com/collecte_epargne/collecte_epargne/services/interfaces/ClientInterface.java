package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;

import java.util.List;

public interface ClientInterface {
    ClientDto save(ClientDto clientDto);

    List<ClientDto> getAll();

    ClientDto getById(Long numeroClient);

    ClientDto update(Long numeroClient, ClientDto clientDto);

    void delete(Long numeroClient);

    ClientDto updateByCodeClient(String codeClient, ClientDto clientDto);

    void deleteByCodeClient(String codeClient);

    // Ajout de fonctions de recherche spécifiques si nécessaire
    ClientDto getByNumeroClient(Long numeroClient);

    // Récupérer un client par le login de l'utilisateur
    ClientDto getByLogin(String login);

    ClientDto getByCodeClient(String codeClient);

    List<ClientDto> getClientsByAgence(Integer idAgence);
}
