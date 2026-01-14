package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.DemandeOuvertureCompteDto;
import com.collecte_epargne.collecte_epargne.utils.StatutDemande;

import java.util.List;

public interface DemandeOuvertureCompteInterface {
    DemandeOuvertureCompteDto createDemande(DemandeOuvertureCompteDto demandeDto);
    
    List<DemandeOuvertureCompteDto> getAll();
    
    List<DemandeOuvertureCompteDto> getByClient(String codeClient);
    
    List<DemandeOuvertureCompteDto> getByStatut(StatutDemande statut);
    
    DemandeOuvertureCompteDto getById(Long idDemande);
    
    DemandeOuvertureCompteDto validerDemande(Long idDemande, String loginSuperviseur, String motifRejet);
    
    DemandeOuvertureCompteDto rejeterDemande(Long idDemande, String loginSuperviseur, String motifRejet);
}

