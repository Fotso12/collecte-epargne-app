package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.PlanCotisationController;
import com.collecte_epargne.collecte_epargne.dtos.PlanCotisationDto;
import com.collecte_epargne.collecte_epargne.services.implementations.PlanCotisationService;
import com.collecte_epargne.collecte_epargne.utils.FrequenceCotisation;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(PlanCotisationController.class)
class PlanCotisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanCotisationService planCotisationService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlanCotisationDto planCotisationDto;

    @BeforeEach
    void setUp() {
        planCotisationDto = new PlanCotisationDto();
        planCotisationDto.setIdPlan("plan-123");
        planCotisationDto.setNom("Plan Mensuel");
        planCotisationDto.setMontantAttendu(new BigDecimal("10000.00"));
        planCotisationDto.setFrequence(FrequenceCotisation.MENSUEL);
        planCotisationDto.setDureeJours(30);
        planCotisationDto.setDateDebut(LocalDate.now());
        planCotisationDto.setTauxPenaliteRetard(new BigDecimal("5.00"));
        planCotisationDto.setStatut(StatutPlanCotisation.ACTIF);
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() throws Exception {
        // Given
        when(planCotisationService.save(any(PlanCotisationDto.class))).thenReturn(planCotisationDto);

        // When & Then
        mockMvc.perform(post("/api/plan-cotisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(planCotisationDto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPlan").value("plan-123"))
                .andExpect(jsonPath("$.nom").value("Plan Mensuel"));

        verify(planCotisationService, times(1)).save(any(PlanCotisationDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithException_ReturnsBadRequest() throws Exception {
        // Given
        when(planCotisationService.save(any(PlanCotisationDto.class)))
                .thenThrow(new IllegalArgumentException("Le nom du plan est obligatoire."));

        // When & Then
        mockMvc.perform(post("/api/plan-cotisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(planCotisationDto))))
                .andExpect(status().isBadRequest());

        verify(planCotisationService, times(1)).save(any(PlanCotisationDto.class));
    }

    @Test
    void testGetAll_Success() throws Exception {
        // Given
        List<PlanCotisationDto> plans = Arrays.asList(planCotisationDto);
        when(planCotisationService.getAll()).thenReturn(plans);

        // When & Then
        mockMvc.perform(get("/api/plan-cotisations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idPlan").value("plan-123"));

        verify(planCotisationService, times(1)).getAll();
    }

    @Test
    void testGetAll_WithException_ThrowsResponseStatusException() throws Exception {
        // Given
        when(planCotisationService.getAll()).thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(get("/api/plan-cotisations"))
                .andExpect(status().isInternalServerError());

        verify(planCotisationService, times(1)).getAll();
    }

    @Test
    void testGetById_Success() throws Exception {
        // Given
        String id = "plan-123";
        when(planCotisationService.getById(id)).thenReturn(planCotisationDto);

        // When & Then
        mockMvc.perform(get("/api/plan-cotisations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlan").value("plan-123"))
                .andExpect(jsonPath("$.nom").value("Plan Mensuel"));

        verify(planCotisationService, times(1)).getById(id);
    }

    @Test
    void testGetById_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String id = "plan-999";
        when(planCotisationService.getById(id))
                .thenThrow(new RuntimeException("Plan de cotisation non trouvé : " + id));

        // When & Then
        mockMvc.perform(get("/api/plan-cotisations/{id}", id))
                .andExpect(status().isNotFound());

        verify(planCotisationService, times(1)).getById(id);
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() throws Exception {
        // Given
        String id = "plan-123";
        planCotisationDto.setNom("Plan Mensuel Modifié");
        when(planCotisationService.update(anyString(), any(PlanCotisationDto.class))).thenReturn(planCotisationDto);

        // When & Then
        mockMvc.perform(put("/api/plan-cotisations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(planCotisationDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Plan Mensuel Modifié"));

        verify(planCotisationService, times(1)).update(eq(id), any(PlanCotisationDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_WithException_ReturnsBadRequest() throws Exception {
        // Given
        String id = "plan-999";
        when(planCotisationService.update(anyString(), any(PlanCotisationDto.class)))
                .thenThrow(new RuntimeException("Plan de cotisation non trouvé pour mise à jour : " + id));

        // When & Then
        mockMvc.perform(put("/api/plan-cotisations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(planCotisationDto))))
                .andExpect(status().isBadRequest());

        verify(planCotisationService, times(1)).update(eq(id), any(PlanCotisationDto.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        // Given
        String id = "plan-123";
        doNothing().when(planCotisationService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/plan-cotisations/{id}", id))
                .andExpect(status().isNoContent());

        verify(planCotisationService, times(1)).delete(id);
    }

    @Test
    void testDelete_WithException_ReturnsBadRequest() throws Exception {
        // Given
        String id = "plan-123";
        doThrow(new RuntimeException("Impossible de supprimer : des comptes de cotisation utilisent ce plan."))
                .when(planCotisationService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/plan-cotisations/{id}", id))
                .andExpect(status().isBadRequest());

        verify(planCotisationService, times(1)).delete(id);
    }
}

