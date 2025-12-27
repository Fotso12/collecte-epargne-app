package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.CompteDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import com.collecte_epargne.collecte_epargne.mappers.CompteMapper;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.TypeCompteRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.CompteInterface;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CompteService implements CompteInterface {

    private final CompteRepository compteRepository;
    private final CompteMapper compteMapper;
    private final ClientRepository clientRepository; // Pour vérifier l'existence du client
    private final TypeCompteRepository typeCompteRepository; // Pour vérifier l'existence du type de compte


    public CompteService(CompteRepository compteRepository, CompteMapper compteMapper, ClientRepository clientRepository, TypeCompteRepository typeCompteRepository) {
        this.compteRepository = compteRepository;
        this.compteMapper = compteMapper;
        this.clientRepository = clientRepository;
        this.typeCompteRepository = typeCompteRepository;
    }

    // Méthode utilitaire pour attacher les entités relationnelles
    private void assignerRelations(Compte compte, CompteDto dto) {
        // 1. Client (CODE_CLIENT)
        if (dto.getCodeClient() != null) {
            Client client = clientRepository.findById(dto.getCodeClient())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + dto.getCodeClient()));
            compte.setClient(client);
        }

        // 2. TypeCompte (ID_TYPE)
        if (dto.getIdTypeCompte() != null) {
            TypeCompte typeCompte = typeCompteRepository.findById(dto.getIdTypeCompte())
                    .orElseThrow(() -> new RuntimeException("Type de compte non trouvé avec l'ID : " + dto.getIdTypeCompte()));
            compte.setTypeCompte(typeCompte);
        }
    }

    @Override
    public CompteDto save(CompteDto compteDto) {
        if (compteDto.getNumCompte() == null || compteDto.getNumCompte().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de compte est obligatoire.");
        }

        // Vérification de l'unicité
        if (compteRepository.findByNumCompte(compteDto.getNumCompte()).isPresent()) {
            throw new RuntimeException("Un compte avec ce numéro existe déjà.");
        }

        // Vérifier que le client existe avant de créer le compte
        if (compteDto.getCodeClient() == null || !clientRepository.existsById(compteDto.getCodeClient())) {
            throw new RuntimeException("Le client doit exister avant de créer un compte. Code client : " + compteDto.getCodeClient());
        }

        Compte compteToSave = compteMapper.toEntity(compteDto);

        // Initialiser les valeurs par défaut si non fournies
        if (compteToSave.getSolde() == null) {
            compteToSave.setSolde(BigDecimal.ZERO);
        }
        if (compteToSave.getSoldeDisponible() == null) {
            compteToSave.setSoldeDisponible(BigDecimal.ZERO);
        }
        if (compteToSave.getDateOuverture() == null) {
            compteToSave.setDateOuverture(LocalDate.now());
        }

        assignerRelations(compteToSave, compteDto);

        Compte savedCompte = compteRepository.save(compteToSave);
        return compteMapper.toDto(savedCompte);
    }

    @Override
    public List<CompteDto> getAll() {
        return compteRepository.findAll().stream()
                .map(compteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompteDto getById(String idCompte) {
        Compte compte = compteRepository.findById(idCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID : " + idCompte));
        return compteMapper.toDto(compte);
    }

    @Override
    public CompteDto update(String idCompte, CompteDto compteDto) {
        Compte existingCompte = compteRepository.findById(idCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé pour la mise à jour : " + idCompte));

        // Mettre à jour les champs non-relationnels
        existingCompte.setNumCompte(compteDto.getNumCompte());
        existingCompte.setSolde(compteDto.getSolde());
        existingCompte.setSoldeDisponible(compteDto.getSoldeDisponible());
        existingCompte.setDateOuverture(compteDto.getDateOuverture());
        existingCompte.setDateDerniereTransaction(compteDto.getDateDerniereTransaction());
        existingCompte.setTauxPenalite(compteDto.getTauxPenalite());
        existingCompte.setTauxBonus(compteDto.getTauxBonus());
        existingCompte.setStatut(compteDto.getStatut());
        existingCompte.setMotifBlocage(compteDto.getMotifBlocage());
        existingCompte.setDateCloture(compteDto.getDateCloture());

        // Mettre à jour les relations
        assignerRelations(existingCompte, compteDto);

        Compte updatedCompte = compteRepository.save(existingCompte);
        return compteMapper.toDto(updatedCompte);
    }

    @Override
    public void delete(String idCompte) {
        if (!compteRepository.existsById(idCompte)) {
            throw new RuntimeException("Compte inexistant : " + idCompte);
        }
        compteRepository.deleteById(idCompte);
    }

    @Override
    public CompteDto getByNumCompte(String numCompte) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec le numéro : " + numCompte));
        return compteMapper.toDto(compte);
    }

    @Override
    public List<CompteDto> getByClient(String codeClient) {
        return compteRepository.findByClientCodeClient(codeClient).stream()
                .map(compteMapper::toDto)
                .collect(Collectors.toList());
    }
}
