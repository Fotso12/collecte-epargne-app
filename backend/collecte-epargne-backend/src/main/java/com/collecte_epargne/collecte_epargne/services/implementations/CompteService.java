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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class CompteService implements CompteInterface {

    private static final Logger log = LoggerFactory.getLogger(CompteService.class);

    private final CompteRepository compteRepository;
    private final CompteMapper compteMapper;
    private final ClientRepository clientRepository;
    private final TypeCompteRepository typeCompteRepository;

    public CompteService(CompteRepository compteRepository,
                         CompteMapper compteMapper,
                         ClientRepository clientRepository,
                         TypeCompteRepository typeCompteRepository) {
        this.compteRepository = compteRepository;
        this.compteMapper = compteMapper;
        this.clientRepository = clientRepository;
        this.typeCompteRepository = typeCompteRepository;
    }

    private void assignerRelations(Compte compte, CompteDto dto) {
        log.debug("Assignation des relations pour le compte {}", dto.getNumCompte());

        if (dto.getCodeClient() != null) {
            Client client = clientRepository.findByCodeClient(dto.getCodeClient())
                    .orElseThrow(() -> {
                        log.error("Client non trouvé code={}", dto.getCodeClient());
                        return new RuntimeException("Client non trouvé avec le code : " + dto.getCodeClient());
                    });
            compte.setClient(client);
        }

        if (dto.getIdTypeCompte() != null) {
            TypeCompte typeCompte = typeCompteRepository.findById(dto.getIdTypeCompte())
                    .orElseThrow(() -> {
                        log.error("TypeCompte non trouvé id={}", dto.getIdTypeCompte());
                        return new RuntimeException("Type de compte non trouvé");
                    });
            compte.setTypeCompte(typeCompte);
        }
    }

    @Override
    public CompteDto save(CompteDto compteDto) {
        log.info("Création d'un compte numCompte={}", compteDto.getNumCompte());

        if (compteDto.getNumCompte() == null || compteDto.getNumCompte().isEmpty()) {
            log.warn("Numéro de compte manquant");
            throw new IllegalArgumentException("Le numéro de compte est obligatoire.");
        }

        if (compteRepository.findByNumCompte(compteDto.getNumCompte()).isPresent()) {
            log.warn("Compte déjà existant numCompte={}", compteDto.getNumCompte());
            throw new RuntimeException("Un compte avec ce numéro existe déjà.");
        }

        if (compteDto.getCodeClient() == null ||
                !clientRepository.existsByCodeClient(compteDto.getCodeClient())) {
            log.warn("Client inexistant pour création compte codeClient={}", compteDto.getCodeClient());
            throw new RuntimeException("Le client doit exister avant de créer un compte.");
        }

        Compte compte = compteMapper.toEntity(compteDto);

        if (compte.getSolde() == null) compte.setSolde(BigDecimal.ZERO);
        if (compte.getSoldeDisponible() == null) compte.setSoldeDisponible(BigDecimal.ZERO);
        if (compte.getDateOuverture() == null) compte.setDateOuverture(LocalDate.now());

        assignerRelations(compte, compteDto);

        Compte savedCompte = compteRepository.save(compte);
        log.info("Compte créé avec succès id={}", savedCompte.getIdCompte());

        return compteMapper.toDto(savedCompte);
    }

    @Override
    public List<CompteDto> getAll() {
        log.info("Récupération de tous les comptes");

        List<CompteDto> comptes = compteRepository.findAll()
                .stream()
                .map(compteMapper::toDto)
                .collect(Collectors.toList());

        log.info("Nombre de comptes trouvés : {}", comptes.size());
        return comptes;
    }

    @Override
    public CompteDto getById(String idCompte) {
        log.info("Recherche compte id={}", idCompte);

        Compte compte = compteRepository.findById(idCompte)
                .orElseThrow(() -> {
                    log.error("Compte non trouvé id={}", idCompte);
                    return new RuntimeException("Compte non trouvé");
                });

        return compteMapper.toDto(compte);
    }

    @Override
    public CompteDto getByNumCompte(String numCompte) {
        log.info("Recherche compte numCompte={}", numCompte);

        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> {
                    log.error("Compte non trouvé numCompte={}", numCompte);
                    return new RuntimeException("Compte non trouvé");
                });

        return compteMapper.toDto(compte);
    }

    @Override
    public List<CompteDto> getByClient(String codeClient) {
        log.info("Recherche comptes par client codeClient={}", codeClient);

        return compteRepository.findByClientCodeClient(codeClient)
                .stream()
                .map(compteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompteDto update(String idCompte, CompteDto compteDto) {
        log.info("Mise à jour compte id={}", idCompte);

        Compte compte = compteRepository.findById(idCompte)
                .orElseThrow(() -> {
                    log.error("Compte non trouvé pour update id={}", idCompte);
                    return new RuntimeException("Compte non trouvé");
                });

        compte.setNumCompte(compteDto.getNumCompte());
        compte.setSolde(compteDto.getSolde());
        compte.setSoldeDisponible(compteDto.getSoldeDisponible());
        compte.setDateOuverture(compteDto.getDateOuverture());
        compte.setDateDerniereTransaction(compteDto.getDateDerniereTransaction());
        compte.setTauxPenalite(compteDto.getTauxPenalite());
        compte.setTauxBonus(compteDto.getTauxBonus());
        compte.setStatut(compteDto.getStatut());
        compte.setMotifBlocage(compteDto.getMotifBlocage());
        compte.setDateCloture(compteDto.getDateCloture());

        assignerRelations(compte, compteDto);

        Compte updated = compteRepository.save(compte);
        log.info("Compte mis à jour id={}", idCompte);

        return compteMapper.toDto(updated);
    }

    @Override
    public void delete(String idCompte) {
        log.info("Suppression compte id={}", idCompte);

        if (!compteRepository.existsById(idCompte)) {
            log.warn("Tentative suppression compte inexistant id={}", idCompte);
            throw new RuntimeException("Compte inexistant");
        }

        compteRepository.deleteById(idCompte);
        log.info("Compte supprimé id={}", idCompte);
    }
}
