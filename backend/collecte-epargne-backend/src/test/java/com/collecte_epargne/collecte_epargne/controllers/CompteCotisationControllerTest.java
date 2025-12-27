package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteCotisationService;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompteCotisationController.class)
class CompteCotisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompteCotisationService compteCotisationService;

    @Autowired
    private ObjectMapper objectMapper;

    private CompteCotisationDto compteCotisationDto;

    @BeforeEach
    void setUp() {
        compteCotisationDto = new CompteCotisationDto();
        compteCotisationDto.setId("cc-123");
        compteCotisationDto.setDateAdhesion(LocalDate.now());
        compteCotisationDto.setMontantTotalVerse(new BigDecimal("5000.00"));
        compteCotisationDto.setNombreVersements(5);
        compteCotisationDto.setNombreRetards(0);
        compteCotisationDto.setProchaineEcheance(LocalDate.now().plusDays(30));
        compteCotisationDto.setStatut(StatutPlanCotisation.ACTIF);
        compteCotisationDto.setIdCompte("compte-123");
        compteCotisationDto.setIdPlanCotisation("plan-123");
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() throws Exception {
        // Given
        when(compteCotisationService.save(any(CompteCotisationDto.class))).thenReturn(compteCotisationDto);

        // When & Then
        mockMvc.perform(post("/api/compte-cotisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(compteCotisationDto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("cc-123"))
                .andExpect(jsonPath("$.idCompte").value("compte-123"));

        verify(compteCotisationService, times(1)).save(any(CompteCotisationDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithException_ReturnsBadRequest() throws Exception {
        // Given
        when(compteCotisationService.save(any(CompteCotisationDto.class)))
                .thenThrow(new IllegalArgumentException("L'identifiant du compte est obligatoire."));

        // When & Then
        mockMvc.perform(post("/api/compte-cotisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(compteCotisationDto))))
                .andExpect(status().isBadRequest());

        verify(compteCotisationService, times(1)).save(any(CompteCotisationDto.class));
    }

    @Test
    void testGetAll_Success() throws Exception {
        // Given
        List<CompteCotisationDto> comptes = Arrays.asList(compteCotisationDto);
        when(compteCotisationService.getAll()).thenReturn(comptes);

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("cc-123"));

        verify(compteCotisationService, times(1)).getAll();
    }

    @Test
    void testGetAll_WithException_ThrowsResponseStatusException() throws Exception {
        // Given
        when(compteCotisationService.getAll()).thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations"))
                .andExpect(status().isInternalServerError());

        verify(compteCotisationService, times(1)).getAll();
    }

    @Test
    void testGetById_Success() throws Exception {
        // Given
        String id = "cc-123";
        when(compteCotisationService.getById(id)).thenReturn(compteCotisationDto);

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cc-123"))
                .andExpect(jsonPath("$.idCompte").value("compte-123"));

        verify(compteCotisationService, times(1)).getById(id);
    }

    @Test
    void testGetById_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String id = "cc-999";
        when(compteCotisationService.getById(id))
                .thenThrow(new RuntimeException("Compte de cotisation non trouvé : " + id));

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations/{id}", id))
                .andExpect(status().isNotFound());

        verify(compteCotisationService, times(1)).getById(id);
    }

    @Test
    void testGetByCompte_Success() throws Exception {
        // Given
        String idCompte = "compte-123";
        List<CompteCotisationDto> comptes = Arrays.asList(compteCotisationDto);
        when(compteCotisationService.getByCompte(idCompte)).thenReturn(comptes);

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations/compte/{idCompte}", idCompte))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idCompte").value("compte-123"));

        verify(compteCotisationService, times(1)).getByCompte(idCompte);
    }

    @Test
    void testGetByCompte_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String idCompte = "compte-999";
        when(compteCotisationService.getByCompte(idCompte))
                .thenThrow(new RuntimeException("Aucun compte de cotisation trouvé"));

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations/compte/{idCompte}", idCompte))
                .andExpect(status().isNotFound());

        verify(compteCotisationService, times(1)).getByCompte(idCompte);
    }

    @Test
    void testGetByPlan_Success() throws Exception {
        // Given
        String idPlan = "plan-123";
        List<CompteCotisationDto> comptes = Arrays.asList(compteCotisationDto);
        when(compteCotisationService.getByPlanCotisation(idPlan)).thenReturn(comptes);

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations/plan/{idPlan}", idPlan))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idPlanCotisation").value("plan-123"));

        verify(compteCotisationService, times(1)).getByPlanCotisation(idPlan);
    }

    @Test
    void testGetByPlan_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String idPlan = "plan-999";
        when(compteCotisationService.getByPlanCotisation(idPlan))
                .thenThrow(new RuntimeException("Aucun compte de cotisation trouvé"));

        // When & Then
        mockMvc.perform(get("/api/compte-cotisations/plan/{idPlan}", idPlan))
                .andExpect(status().isNotFound());

        verify(compteCotisationService, times(1)).getByPlanCotisation(idPlan);
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() throws Exception {
        // Given
        String id = "cc-123";
        compteCotisationDto.setMontantTotalVerse(new BigDecimal("10000.00"));
        when(compteCotisationService.update(anyString(), any(CompteCotisationDto.class))).thenReturn(compteCotisationDto);

        // When & Then
        mockMvc.perform(put("/api/compte-cotisations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(compteCotisationDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantTotalVerse").value(10000.00));

        verify(compteCotisationService, times(1)).update(eq(id), any(CompteCotisationDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_WithException_ReturnsBadRequest() throws Exception {
        // Given
        String id = "cc-999";
        when(compteCotisationService.update(anyString(), any(CompteCotisationDto.class)))
                .thenThrow(new RuntimeException("Compte de cotisation non trouvé pour mise à jour : " + id));

        // When & Then
        mockMvc.perform(put("/api/compte-cotisations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(compteCotisationDto))))
                .andExpect(status().isBadRequest());

        verify(compteCotisationService, times(1)).update(eq(id), any(CompteCotisationDto.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        // Given
        String id = "cc-123";
        doNothing().when(compteCotisationService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/compte-cotisations/{id}", id))
                .andExpect(status().isNoContent());

        verify(compteCotisationService, times(1)).delete(id);
    }

    @Test
    void testDelete_WithException_ReturnsNotFound() throws Exception {
        // Given
        String id = "cc-999";
        doThrow(new RuntimeException("Compte de cotisation inexistant : " + id))
                .when(compteCotisationService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/compte-cotisations/{id}", id))
                .andExpect(status().isNotFound());

        verify(compteCotisationService, times(1)).delete(id);
    }
}

