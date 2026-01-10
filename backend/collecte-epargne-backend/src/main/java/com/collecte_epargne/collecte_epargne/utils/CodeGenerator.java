package com.collecte_epargne.collecte_epargne.utils;

import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CodeGenerator {

    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;

    public CodeGenerator(EmployeRepository employeRepository, ClientRepository clientRepository) {
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
    }

    // Génère un matricule employé (ex: COLL202512345)
    public String generateMatricule(TypeEmploye typeEmploye) {
        String prefix = switch (typeEmploye) {
            case COLLECTEUR -> "COLL";
            case CAISSIER -> "CAIS";
            case SUPERVISEUR -> "SUP";
            default -> "EMP";
        };
        return generateUniqueCode(prefix, true);
    }

    // Génère un code client (ex: CLT202567890)
    public String generateClientCode() {
        return generateUniqueCode("CLT", false);
    }

    // Génère un code agence (ex: AGC-ABIDJAN-123)
    public String generateAgenceCode(String ville) {
        String prefix = "AGC";
        if (ville != null && !ville.isEmpty()) {
            prefix += "-" + ville.substring(0, Math.min(ville.length(), 3)).toUpperCase();
        }
        return generateUniqueCode(prefix + "-", false);
    }

    // Génère une référence de transaction (ex: TXN-20250101-123456)
    public String generateTransactionRef() {
        String datePart = LocalDate.now().toString().replace("-", "");
        return "TXN-" + datePart + "-" + ThreadLocalRandom.current().nextInt(100000, 999999);
    }

    // Logique commune de génération avec vérification d'unicité
    private String generateUniqueCode(String prefix, boolean isEmployee) {
        long randomNumber = ThreadLocalRandom.current().nextInt(10000, 100000);
        int year = LocalDate.now().getYear();
        String finalCode = prefix + year + randomNumber;

        // Vérifie si le code existe déjà pour éviter les doublons
        boolean exists = isEmployee
                ? employeRepository.findByMatricule(finalCode).isPresent()
                : clientRepository.findByCodeClient(finalCode).isPresent();

        // Note: Pour les agences et transactions, nous ne vérifions pas l'unicité ici dans cet extrait simplifié
        // mais idéalement il faudrait injecter leurs repositories respectifs.
        // Pour l'instant, la probabilité de collision est faible avec le timestamp/random.

        return exists ? generateUniqueCode(prefix, isEmployee) : finalCode;
    }
}