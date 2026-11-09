package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.DemandeOuvertureCompteDto;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.services.interfaces.DemandeOuvertureCompteInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutCompte;
import com.collecte_epargne.collecte_epargne.utils.StatutDemande;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DemandeOuvertureCompteService implements DemandeOuvertureCompteInterface {

    private final DemandeOuvertureCompteRepository demandeRepository;
    private final ClientRepository clientRepository;
    private final TypeCompteRepository typeCompteRepository;
    private final EmployeRepository employeRepository;
    private final CompteRepository compteRepository;

    public DemandeOuvertureCompteService(DemandeOuvertureCompteRepository demandeRepository,
                                         ClientRepository clientRepository,
                                         TypeCompteRepository typeCompteRepository,
                                         EmployeRepository employeRepository,
                                         CompteRepository compteRepository) {
        this.demandeRepository = demandeRepository;
        this.clientRepository = clientRepository;
        this.typeCompteRepository = typeCompteRepository;
        this.employeRepository = employeRepository;
        this.compteRepository = compteRepository;
    }

    @Override
    @Transactional
    public DemandeOuvertureCompteDto createDemande(DemandeOuvertureCompteDto demandeDto) {
        // Vérifier que le client existe
        Client client = clientRepository.findByCodeClient(demandeDto.getCodeClient())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code : " + demandeDto.getCodeClient()));

        // Vérifier que le type de compte existe
        TypeCompte typeCompte = typeCompteRepository.findById(demandeDto.getIdTypeCompte())
                .orElseThrow(() -> new RuntimeException("Type de compte non trouvé avec l'ID : " + demandeDto.getIdTypeCompte()));

        // Vérifier qu'il n'y a pas déjà une demande en attente pour ce client et ce type de compte
        demandeRepository.findByClientCodeClientAndTypeCompteIdAndStatut(
                demandeDto.getCodeClient(), 
                demandeDto.getIdTypeCompte(), 
                StatutDemande.EN_ATTENTE
        ).ifPresent(d -> {
            throw new RuntimeException("Une demande en attente existe déjà pour ce type de compte");
        });

        // Créer la demande
        DemandeOuvertureCompte demande = new DemandeOuvertureCompte();
        demande.setClient(client);
        demande.setTypeCompte(typeCompte);
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demande.setMontantInitial(demandeDto.getMontantInitial());
        demande.setMotif(demandeDto.getMotif());
        demande.setDateDemande(Instant.now());

        DemandeOuvertureCompte savedDemande = demandeRepository.save(demande);
        return toDto(savedDemande);
    }

    @Override
    public List<DemandeOuvertureCompteDto> getAll() {
        return demandeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DemandeOuvertureCompteDto> getByClient(String codeClient) {
        return demandeRepository.findByClientCodeClient(codeClient).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DemandeOuvertureCompteDto> getByStatut(StatutDemande statut) {
        return demandeRepository.findByStatutOrderByDateDemandeDesc(statut).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DemandeOuvertureCompteDto getById(Long idDemande) {
        DemandeOuvertureCompte demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID : " + idDemande));
        return toDto(demande);
    }

    @Override
    @Transactional
    public DemandeOuvertureCompteDto validerDemande(Long idDemande, String loginSuperviseur, String motifRejet) {
        DemandeOuvertureCompte demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID : " + idDemande));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new RuntimeException("Cette demande ne peut plus être validée (statut : " + demande.getStatut() + ")");
        }

        // Vérifier que le superviseur existe et est bien un superviseur
        Employe superviseur = employeRepository.findByUtilisateurLogin(loginSuperviseur)
                .orElseThrow(() -> new RuntimeException("Aucun employé trouvé pour ce login"));

        if (superviseur.getTypeEmploye() != TypeEmploye.SUPERVISEUR) {
            throw new RuntimeException("L'utilisateur n'est pas un superviseur");
        }

        // Valider la demande
        demande.setStatut(StatutDemande.VALIDEE);
        demande.setSuperviseurValidateur(superviseur);
        demande.setDateValidation(Instant.now());

        // Créer automatiquement le compte
        Compte nouveauCompte = new Compte();
        nouveauCompte.setIdCompte("COMPTE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        nouveauCompte.setNumCompte("NUM_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        nouveauCompte.setClient(demande.getClient());
        nouveauCompte.setTypeCompte(demande.getTypeCompte());
        nouveauCompte.setSolde(demande.getMontantInitial() != null ? demande.getMontantInitial() : BigDecimal.ZERO);
        nouveauCompte.setSoldeDisponible(demande.getMontantInitial() != null ? demande.getMontantInitial() : BigDecimal.ZERO);
        nouveauCompte.setDateOuverture(LocalDate.now());
        nouveauCompte.setStatut(StatutCompte.ACTIF);

        compteRepository.save(nouveauCompte);

        DemandeOuvertureCompte savedDemande = demandeRepository.save(demande);
        return toDto(savedDemande);
    }

    @Override
    @Transactional
    public DemandeOuvertureCompteDto rejeterDemande(Long idDemande, String loginSuperviseur, String motifRejet) {
        DemandeOuvertureCompte demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID : " + idDemande));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new RuntimeException("Cette demande ne peut plus être rejetée (statut : " + demande.getStatut() + ")");
        }

        // Vérifier que le superviseur existe et est bien un superviseur
        Employe superviseur = employeRepository.findByUtilisateurLogin(loginSuperviseur)
                .orElseThrow(() -> new RuntimeException("Aucun employé trouvé pour ce login"));

        if (superviseur.getTypeEmploye() != TypeEmploye.SUPERVISEUR) {
            throw new RuntimeException("L'utilisateur n'est pas un superviseur");
        }

        // Rejeter la demande
        demande.setStatut(StatutDemande.REJETEE);
        demande.setSuperviseurValidateur(superviseur);
        demande.setMotifRejet(motifRejet);
        demande.setDateValidation(Instant.now());

        DemandeOuvertureCompte savedDemande = demandeRepository.save(demande);
        return toDto(savedDemande);
    }

    private DemandeOuvertureCompteDto toDto(DemandeOuvertureCompte demande) {
        DemandeOuvertureCompteDto dto = new DemandeOuvertureCompteDto();
        dto.setIdDemande(demande.getIdDemande());
        dto.setCodeClient(demande.getClient().getCodeClient());
        dto.setIdTypeCompte(demande.getTypeCompte().getId());
        dto.setNomTypeCompte(demande.getTypeCompte().getNom());
        dto.setStatut(demande.getStatut());
        dto.setMontantInitial(demande.getMontantInitial());
        dto.setMotif(demande.getMotif());
        dto.setMotifRejet(demande.getMotifRejet());
        dto.setDateDemande(demande.getDateDemande());
        dto.setDateValidation(demande.getDateValidation());
        
        if (demande.getSuperviseurValidateur() != null) {
            dto.setIdSuperviseurValidateur(demande.getSuperviseurValidateur().getIdEmploye());
        }
        
        // Informations du client
        if (demande.getClient().getUtilisateur() != null) {
            dto.setNomClient(demande.getClient().getUtilisateur().getNom());
            dto.setPrenomClient(demande.getClient().getUtilisateur().getPrenom());
            dto.setEmailClient(demande.getClient().getUtilisateur().getEmail());
        }
        
        return dto;
    }
}

