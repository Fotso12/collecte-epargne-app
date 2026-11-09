package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.TypeCompteController;
import com.collecte_epargne.collecte_epargne.dtos.TypeCompteDto;
import com.collecte_epargne.collecte_epargne.services.implementations.TypeCompteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TypeCompteController.class)
class TypeCompteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TypeCompteService typeCompteService;

    @Autowired
    private ObjectMapper objectMapper;

    private TypeCompteDto typeCompteDto;

    @BeforeEach
    void setUp() {
        typeCompteDto = new TypeCompteDto();
        typeCompteDto.setId(1);
        typeCompteDto.setCode("TC001");
        typeCompteDto.setNom("Type Compte Test");
        typeCompteDto.setDescription("Description test");
        typeCompteDto.setTauxInteret(new BigDecimal("5.00"));
        typeCompteDto.setSoldeMinimum(new BigDecimal("100.00"));
        typeCompteDto.setFraisOuverture(new BigDecimal("10.00"));
        typeCompteDto.setFraisCloture(new BigDecimal("5.00"));
        typeCompteDto.setAutoriserRetrait(true);
        typeCompteDto.setDureeBlocageJours(30);
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() throws Exception {
        // Given
        when(typeCompteService.save(any(TypeCompteDto.class))).thenReturn(typeCompteDto);

        // When & Then
        mockMvc.perform(post("/api/type-comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(typeCompteDto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("TC001"));

        verify(typeCompteService, times(1)).save(any(TypeCompteDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithException_ReturnsBadRequest() throws Exception {
        // Given
        when(typeCompteService.save(any(TypeCompteDto.class)))
                .thenThrow(new IllegalArgumentException("Le code du type de compte est obligatoire."));

        // When & Then
        mockMvc.perform(post("/api/type-comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(typeCompteDto))))
                .andExpect(status().isBadRequest());

        verify(typeCompteService, times(1)).save(any(TypeCompteDto.class));
    }

    @Test
    void testGetAll_Success() throws Exception {
        // Given
        List<TypeCompteDto> types = Arrays.asList(typeCompteDto);
        when(typeCompteService.getAll()).thenReturn(types);

        // When & Then
        mockMvc.perform(get("/api/type-comptes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(typeCompteService, times(1)).getAll();
    }

    @Test
    void testGetAll_WithException_ThrowsResponseStatusException() throws Exception {
        // Given
        when(typeCompteService.getAll()).thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(get("/api/type-comptes"))
                .andExpect(status().isInternalServerError());

        verify(typeCompteService, times(1)).getAll();
    }

    @Test
    void testGetById_Success() throws Exception {
        // Given
        Integer id = 1;
        when(typeCompteService.getById(id)).thenReturn(typeCompteDto);

        // When & Then
        mockMvc.perform(get("/api/type-comptes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("TC001"));

        verify(typeCompteService, times(1)).getById(id);
    }

    @Test
    void testGetById_NotFound_ReturnsNotFound() throws Exception {
        // Given
        Integer id = 999;
        when(typeCompteService.getById(id))
                .thenThrow(new RuntimeException("Type de compte non trouvé avec l'ID : " + id));

        // When & Then
        mockMvc.perform(get("/api/type-comptes/{id}", id))
                .andExpect(status().isNotFound());

        verify(typeCompteService, times(1)).getById(id);
    }

    @Test
    void testGetByCode_Success() throws Exception {
        // Given
        String code = "TC001";
        when(typeCompteService.getByCode(code)).thenReturn(typeCompteDto);

        // When & Then
        mockMvc.perform(get("/api/type-comptes/code/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TC001"))
                .andExpect(jsonPath("$.nom").value("Type Compte Test"));

        verify(typeCompteService, times(1)).getByCode(code);
    }

    @Test
    void testGetByCode_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String code = "TC999";
        when(typeCompteService.getByCode(code))
                .thenThrow(new RuntimeException("Type de compte non trouvé avec le code : " + code));

        // When & Then
        mockMvc.perform(get("/api/type-comptes/code/{code}", code))
                .andExpect(status().isNotFound());

        verify(typeCompteService, times(1)).getByCode(code);
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() throws Exception {
        // Given
        Integer id = 1;
        typeCompteDto.setNom("Type Compte Updated");
        when(typeCompteService.update(anyInt(), any(TypeCompteDto.class))).thenReturn(typeCompteDto);

        // When & Then
        mockMvc.perform(put("/api/type-comptes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(typeCompteDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Type Compte Updated"));

        verify(typeCompteService, times(1)).update(eq(id), any(TypeCompteDto.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_WithException_ReturnsBadRequest() throws Exception {
        // Given
        Integer id = 999;
        when(typeCompteService.update(anyInt(), any(TypeCompteDto.class)))
                .thenThrow(new RuntimeException("Type de compte non trouvé pour la mise à jour : " + id));

        // When & Then
        mockMvc.perform(put("/api/type-comptes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(typeCompteDto))))
                .andExpect(status().isBadRequest());

        verify(typeCompteService, times(1)).update(eq(id), any(TypeCompteDto.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        // Given
        Integer id = 1;
        doNothing().when(typeCompteService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/type-comptes/{id}", id))
                .andExpect(status().isNoContent());

        verify(typeCompteService, times(1)).delete(id);
    }

    @Test
    void testDelete_WithException_ReturnsNotFound() throws Exception {
        // Given
        Integer id = 999;
        doThrow(new RuntimeException("Type de compte inexistant : " + id))
                .when(typeCompteService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/type-comptes/{id}", id))
                .andExpect(status().isNotFound());

        verify(typeCompteService, times(1)).delete(id);
    }
}
