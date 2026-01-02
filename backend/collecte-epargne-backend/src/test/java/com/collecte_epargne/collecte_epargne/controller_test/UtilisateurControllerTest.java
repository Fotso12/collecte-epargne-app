package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.UtilisateurController;
import com.collecte_epargne.collecte_epargne.dtos.UtilisateurCreationRequestDto;
import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;
import com.collecte_epargne.collecte_epargne.services.implementations.UtilisateurService;
import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilisateurController.class)
class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UtilisateurService utilisateurService;

    @Autowired
    private ObjectMapper objectMapper;

    private UtilisateurDto utilisateurDto;
    private UtilisateurCreationRequestDto creationRequestDto;

    @BeforeEach
    void setUp() {
        utilisateurDto = new UtilisateurDto();
        utilisateurDto.setLogin("testuser");
        utilisateurDto.setIdRole(1);
        utilisateurDto.setNom("Test");
        utilisateurDto.setPrenom("User");
        utilisateurDto.setTelephone("0123456789");
        utilisateurDto.setEmail("test@example.com");
        utilisateurDto.setStatut(StatutGenerique.ACTIF);
        utilisateurDto.setDateCreation(Instant.now());

        creationRequestDto = new UtilisateurCreationRequestDto();
        creationRequestDto.setLogin("testuser");
        creationRequestDto.setIdRole(1);
        creationRequestDto.setNom("Test");
        creationRequestDto.setPrenom("User");
        creationRequestDto.setTelephone("0123456789");
        creationRequestDto.setEmail("test@example.com");
        creationRequestDto.setPassword("password123");
        creationRequestDto.setStatut(StatutGenerique.ACTIF);
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() throws Exception {
        // Given
        when(utilisateurService.save(any(UtilisateurDto.class), anyString())).thenReturn(utilisateurDto);

        // When & Then
        mockMvc.perform(post("/api/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(creationRequestDto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("testuser"))
                .andExpect(jsonPath("$.nom").value("Test"));

        verify(utilisateurService, times(1)).save(any(UtilisateurDto.class), anyString());
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithException_ReturnsBadRequest() throws Exception {
        // Given
        when(utilisateurService.save(any(UtilisateurDto.class), anyString()))
                .thenThrow(new IllegalArgumentException("Le login et le mot de passe sont obligatoires."));

        // When & Then
        mockMvc.perform(post("/api/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(creationRequestDto))))
                .andExpect(status().isBadRequest());

        verify(utilisateurService, times(1)).save(any(UtilisateurDto.class), anyString());
    }

    @Test
    void testGetAll_Success() throws Exception {
        // Given
        List<UtilisateurDto> utilisateurs = Arrays.asList(utilisateurDto);
        when(utilisateurService.getAll()).thenReturn(utilisateurs);

        // When & Then
        mockMvc.perform(get("/api/utilisateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].login").value("testuser"));

        verify(utilisateurService, times(1)).getAll();
    }

    @Test
    void testGetAll_WithException_ThrowsResponseStatusException() throws Exception {
        // Given
        when(utilisateurService.getAll()).thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(get("/api/utilisateurs"))
                .andExpect(status().isInternalServerError());

        verify(utilisateurService, times(1)).getAll();
    }

    @Test
    void testGetByLogin_Success() throws Exception {
        // Given
        String login = "testuser";
        when(utilisateurService.getByLogin(login)).thenReturn(utilisateurDto);

        // When & Then
        mockMvc.perform(get("/api/utilisateurs/{login}", login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testuser"))
                .andExpect(jsonPath("$.nom").value("Test"));

        verify(utilisateurService, times(1)).getByLogin(login);
    }

    @Test
    void testGetByLogin_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String login = "nonexistent";
        when(utilisateurService.getByLogin(login))
                .thenThrow(new RuntimeException("Utilisateur non trouvé avec le login : " + login));

        // When & Then
        mockMvc.perform(get("/api/utilisateurs/{login}", login))
                .andExpect(status().isNotFound());

        verify(utilisateurService, times(1)).getByLogin(login);
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() throws Exception {
        // Given
        String login = "testuser";
        utilisateurDto.setNom("Updated Test");
        when(utilisateurService.update(anyString(), any(UtilisateurDto.class))).thenReturn(utilisateurDto);

        // When & Then
        mockMvc.perform(put("/api/utilisateurs/{login}", login)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(utilisateurDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Updated Test"));

        verify(utilisateurService, times(1)).update(eq(login), any(UtilisateurDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_WithException_ReturnsBadRequest() throws Exception {
        // Given
        String login = "nonexistent";
        when(utilisateurService.update(anyString(), any(UtilisateurDto.class)))
                .thenThrow(new RuntimeException("Utilisateur non trouvé pour la mise à jour : " + login));

        // When & Then
        mockMvc.perform(put("/api/utilisateurs/{login}", login)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(utilisateurDto))))
                .andExpect(status().isBadRequest());

        verify(utilisateurService, times(1)).update(eq(login), any(UtilisateurDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdatePassword_Success() throws Exception {
        // Given
        String login = "testuser";
        Map<String, String> payload = new HashMap<>();
        payload.put("newPassword", "newpassword123");
        doNothing().when(utilisateurService).updatePassword(login, "newpassword123");

        // When & Then
        mockMvc.perform(put("/api/utilisateurs/{login}/password", login)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(payload))))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe mis à jour avec succès"));

        verify(utilisateurService, times(1)).updatePassword(login, "newpassword123");
    }

    @Test
    @SuppressWarnings("null")
    void testUpdatePassword_WithException_ReturnsBadRequest() throws Exception {
        // Given
        String login = "testuser";
        Map<String, String> payload = new HashMap<>();
        payload.put("newPassword", "newpassword123");
        doThrow(new RuntimeException("Utilisateur non trouvé : " + login))
                .when(utilisateurService).updatePassword(login, "newpassword123");

        // When & Then
        mockMvc.perform(put("/api/utilisateurs/{login}/password", login)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(payload))))
                .andExpect(status().isBadRequest());

        verify(utilisateurService, times(1)).updatePassword(login, "newpassword123");
    }

    @Test
    void testDelete_Success() throws Exception {
        // Given
        String login = "testuser";
        doNothing().when(utilisateurService).delete(login);

        // When & Then
        mockMvc.perform(delete("/api/utilisateurs/{login}", login))
                .andExpect(status().isNoContent());

        verify(utilisateurService, times(1)).delete(login);
    }

    @Test
    void testDelete_WithException_ReturnsNotFound() throws Exception {
        // Given
        String login = "nonexistent";
        doThrow(new RuntimeException("Utilisateur inexistant : " + login))
                .when(utilisateurService).delete(login);

        // When & Then
        mockMvc.perform(delete("/api/utilisateurs/{login}", login))
                .andExpect(status().isNotFound());

        verify(utilisateurService, times(1)).delete(login);
    }
}
