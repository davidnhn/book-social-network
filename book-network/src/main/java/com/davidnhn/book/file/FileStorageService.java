package com.davidnhn.book.file;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service // Indique que cette classe est un service Spring
@RequiredArgsConstructor // Génère un constructeur avec les champs finals
@Slf4j // Génère un logger pour cette classe
public class FileStorageService {

    @Value("${application.file.upload.photo-output-path}")
    private String fileUploadPath; // Chemin de base pour les fichiers téléchargés, injecté depuis les propriétés de l'application

    /**
     * Sauvegarde un fichier pour un utilisateur spécifique.
     * @param sourceFile le fichier à sauvegarder
     * @param userId l'identifiant de l'utilisateur
     * @return le chemin où le fichier a été sauvegardé, ou null si la sauvegarde a échoué
     */
    public String saveFile(
            @Nonnull MultipartFile sourceFile, // Assure que le fichier source n'est pas null
            @Nonnull Integer userId // Assure que l'ID de l'utilisateur n'est pas null
    ) {
        final String fileUploadSubPath = "users" + File.separator + userId; // Chemin spécifique pour l'utilisateur
        return uploadFile(sourceFile, fileUploadSubPath); // Appelle la méthode d'upload avec le sous-chemin utilisateur
    }

    /**
     * Gère le téléchargement du fichier.
     * @param sourceFile le fichier à télécharger
     * @param fileUploadSubPath le sous-chemin spécifique où sauvegarder le fichier
     * @return le chemin complet où le fichier a été sauvegardé, ou null si la sauvegarde a échoué
     */
    private String uploadFile(
            @Nonnull MultipartFile sourceFile, // Assure que le fichier source n'est pas null
            @Nonnull String fileUploadSubPath // Assure que le sous-chemin n'est pas null
    ) {
        // Gestion du DOSSIER à créer
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath; // Chemin complet pour la sauvegarde (ex: upload/users/2)
        File targetFolder = new File(finalUploadPath); // Crée un objet/instance File pour représenter le dossier cible
        if (!targetFolder.exists()) { // Vérifie si le dossier n'existe pas
            boolean folderCreated = targetFolder.mkdirs(); // Crée le dossier et ses parents si nécessaire
            if (!folderCreated) { // Si la création du dossier a échoué
                log.warn("Could not create folder: {}", targetFolder); // Log un avertissement
                return null; // Retourne null pour indiquer l'échec
            }
        }

        // Gestion du FICHIER à créer dans le dossier
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename()); // Obtient l'extension du fichier
        // ./upload/users/1/232655555.jpg
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension; // Génère un chemin unique pour le fichier

        Path targetPath = Paths.get(targetFilePath); // Crée un objet Path pour le chemin cible

        try {
            Files.write(targetPath, sourceFile.getBytes()); // Écrit les octets du fichier source dans le fichier cible
            log.info("File saved to: {}", targetFilePath); // Log l'emplacement du fichier sauvegardé
            return targetFilePath; // Retourne le chemin du fichier sauvegardé
        } catch (IOException e) { // En cas d'erreur d'entrée/sortie
            log.error("File was not saved", e); // Log une erreur
        }
        return null; // Retourne null en cas d'échec
    }

    /**
     * Obtient l'extension d'un fichier à partir de son nom.
     * @param filename le nom du fichier
     * @return l'extension du fichier en minuscules, ou une chaîne vide si aucune extension n'est trouvée
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) { // Si le nom du fichier est null ou vide
            return ""; // Retourne une chaîne vide
        }

        // something.jpg
        int lastDotIndex = filename.lastIndexOf('.'); // Trouve la position du dernier point
        if (lastDotIndex == -1) { // Si aucun point n'est trouvé (pas d'extension)
            return ""; // Retourne une chaîne vide
        }
        return filename.substring(lastDotIndex + 1).toLowerCase(); // Retourne l'extension en minuscules
    }
}
