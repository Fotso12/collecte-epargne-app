package com.collecte_epargne.collecte_epargne.services.implementations;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Chemin racine : uploads/clients
    private final Path root = Paths.get("uploads/clients");

    public FileStorageService() {
        init();
    }

    /**
     * Initialise le dossier racine s'il n'existe pas
     */
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'initialiser le dossier de stockage racine", e);
        }
    }

    /**
     * Sauvegarde un fichier dans un sous-dossier spécifique (ex: "photos", "cni")
     */
    public String save(MultipartFile file, String subFolder) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            // Nettoyage du nom du sous-dossier et création
            Path destinationFolder = this.root.resolve(subFolder);
            if (!Files.exists(destinationFolder)) {
                Files.createDirectories(destinationFolder);
            }

            // Génération d'un nom unique pour éviter les collisions
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path target = destinationFolder.resolve(fileName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Retourne le chemin relatif (plus facile pour la portabilité de la base de données)
            return target.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du stockage du fichier : " + e.getMessage());
        }
    }

    /**
     * Charge un fichier en tant que Ressource pour l'affichage (Frontend)
     */
    public Resource load(String filename) {
        try {
            Path file = Paths.get(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Impossible de lire le fichier : " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur : " + e.getMessage());
        }
    }

    /**
     * Supprime un fichier physique (Utile lors de la suppression d'un client ou update de photo)
     */
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Avertissement : Impossible de supprimer le fichier physique " + filePath);
        }
    }

    /**
     * Nettoie tout le stockage (Attention : à utiliser avec prudence, ex: reset système)
     */
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
}