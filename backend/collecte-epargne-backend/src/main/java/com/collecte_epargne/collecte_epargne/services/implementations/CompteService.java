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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@Service
@AllArgsConstructor
public class CompteService implements CompteInterface {

    private final CompteRepository compteRepository;
    private final CompteMapper compteMapper;
    private final ClientRepository clientRepository; // Pour vérifier l'existence du client
    private final TypeCompteRepository typeCompteRepository; // Pour vérifier l'existence du type de compte

    // Méthode utilitaire pour attacher les entités relationnelles
    private void assignerRelations(Compte compte, CompteDto dto) {
        // 1. Client (CODE_CLIENT)
        if (dto.getCodeClient() != null) {
            String codeClient = Objects.requireNonNull(dto.getCodeClient());
            Client client = clientRepository.findById(codeClient)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + dto.getCodeClient()));
            compte.setClient(client);
        }

        // 2. TypeCompte (ID_TYPE)
        if (dto.getIdTypeCompte() != null) {
            Integer idType = Objects.requireNonNull(dto.getIdTypeCompte());
            TypeCompte typeCompte = typeCompteRepository.findById(idType)
                    .orElseThrow(() -> new RuntimeException("Type de compte non trouvé avec l'ID : " + dto.getIdTypeCompte()));
            compte.setTypeCompte(typeCompte);
        }
    }

    @Override
    @SuppressWarnings("null")
    public CompteDto save(CompteDto compteDto) {
        Objects.requireNonNull(compteDto, "compteDto ne doit pas être null");
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
        Objects.requireNonNull(idCompte, "idCompte ne doit pas être null");
        Compte compte = compteRepository.findById(idCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID : " + idCompte));
        return compteMapper.toDto(compte);
    }

    @Override
    public CompteDto update(String idCompte, CompteDto compteDto) {
        Objects.requireNonNull(idCompte, "idCompte ne doit pas être null");
        Objects.requireNonNull(compteDto, "compteDto ne doit pas être null");
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
        Objects.requireNonNull(idCompte, "idCompte ne doit pas être null");
        if (!compteRepository.existsById(idCompte)) {
            throw new RuntimeException("Compte inexistant : " + idCompte);
        }
        compteRepository.deleteById(idCompte);
    }

    @Override
    public CompteDto getByNumCompte(String numCompte) {
        Objects.requireNonNull(numCompte, "numCompte ne doit pas être null");
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec le numéro : " + numCompte));
        return compteMapper.toDto(compte);
    }

    @Override
    public List<CompteDto> getByClient(String codeClient) {
        Objects.requireNonNull(codeClient, "codeClient ne doit pas être null");
        return compteRepository.findByClientCodeClient(codeClient).stream()
                .map(compteMapper::toDto)
                .collect(Collectors.toList());
    }
}
