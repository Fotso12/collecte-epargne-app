package com.collecte_epargne.collecte_epargne.utils;


import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;


@Component
public class CodeGenerator {

    private final EmployeRepository employeRepository;

    public CodeGenerator(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    public String generateMatricule(TypeEmploye typeEmploye) {
        String prefix = switch (typeEmploye) {
            case COLLECTEUR -> "COLL";
            case CAISSIER -> "CAIS";
            case SUPERVISEUR -> "SUP";
            default -> null;
        };

        long randomNumber = ThreadLocalRandom.current().nextInt(10000, 100000);
        int year = LocalDate.now().getYear();
        String matricule = prefix + year + randomNumber;

        if (employeRepository.findByMatricule(matricule).isPresent()) {
            return generateMatricule(typeEmploye);
        } else {
            return matricule;
        }
    }
}
