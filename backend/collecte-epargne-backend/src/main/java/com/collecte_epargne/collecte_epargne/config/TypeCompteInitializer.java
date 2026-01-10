package com.collecte_epargne.collecte_epargne.config;

import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import com.collecte_epargne.collecte_epargne.repositories.TypeCompteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TypeCompteInitializer {

    private final TypeCompteRepository typeCompteRepository;

    @PostConstruct
    public void init() {
        // Vérifier si le type de compte "EPARGNE" existe déjà
        if (typeCompteRepository.findByCode("EPARGNE").isEmpty()) {
            TypeCompte epargne = new TypeCompte();
            epargne.setCode("EPARGNE");
            epargne.setNom("Compte d'épargne");
            epargne.setDescription("Compte d'épargne par défaut pour les clients");
            epargne.setTauxInteret(new BigDecimal("2.5")); // 2.5% par défaut
            epargne.setSoldeMinimum(new BigDecimal("1000")); // 1000 FCFA minimum
            epargne.setFraisOuverture(new BigDecimal("0")); // Pas de frais d'ouverture
            epargne.setFraisCloture(new BigDecimal("0")); // Pas de frais de clôture
            epargne.setAutoriserRetrait(true);
            epargne.setDureeBlocageJours(0); // Pas de blocage par défaut

            typeCompteRepository.save(epargne);
            System.out.println("✅ Type de compte 'EPARGNE' créé par défaut");
        } else {
            System.out.println("ℹ️ Type de compte 'EPARGNE' existe déjà");
        }
    }
}

