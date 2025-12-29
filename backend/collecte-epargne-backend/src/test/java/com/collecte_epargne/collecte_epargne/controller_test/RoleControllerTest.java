package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.RoleController;
import com.collecte_epargne.collecte_epargne.dtos.RoleDto;
import com.collecte_epargne.collecte_epargne.services.implementations.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto();
        roleDto.setId(1);
        roleDto.setCode("ADMIN");
        roleDto.setNom("Administrateur");
        roleDto.setDescription("Rôle administrateur avec tous les droits");
    }

    @Test
    void save_Success() throws Exception {
        // Given
        when(roleService.save(any(RoleDto.class))).thenReturn(roleDto);

        // When & Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ADMIN"))
                .andExpect(jsonPath("$.nom").value("Administrateur"));

        verify(roleService, times(1)).save(any(RoleDto.class));
    }

    @Test
    void save_WithException_ReturnsBadRequest() throws Exception {
        // Given
        when(roleService.save(any(RoleDto.class)))
                .thenThrow(new RuntimeException("Un rôle avec le nom 'Administrateur' existe déjà."));

        // When & Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).save(any(RoleDto.class));
    }

    @Test
    void getAll_Success() throws Exception {
        // Given
        List<RoleDto> roles = Arrays.asList(roleDto);
        when(roleService.getAll()).thenReturn(roles);

        // When & Then
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].code").value("ADMIN"))
                .andExpect(jsonPath("$[0].nom").value("Administrateur"));

        verify(roleService, times(1)).getAll();
    }

    @Test
    void getAll_WithException_ThrowsResponseStatusException() throws Exception {
        // Given
        when(roleService.getAll())
                .thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isInternalServerError());

        verify(roleService, times(1)).getAll();
    }

    @Test
    void getById_Success() throws Exception {
        // Given
        Integer id = 1;
        when(roleService.getById(id)).thenReturn(roleDto);

        // When & Then
        mockMvc.perform(get("/api/roles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("ADMIN"))
                .andExpect(jsonPath("$.nom").value("Administrateur"));

        verify(roleService, times(1)).getById(id);
    }

    @Test
    void getById_NotFound_ReturnsNotFound() throws Exception {
        // Given
        Integer id = 999;
        when(roleService.getById(id))
                .thenThrow(new RuntimeException("Rôle non trouvé avec l'ID : " + id));

        // When & Then
        mockMvc.perform(get("/api/roles/{id}", id))
                .andExpect(status().isNotFound());

        verify(roleService, times(1)).getById(id);
    }

    @Test
    void update_Success() throws Exception {
        // Given
        Integer id = 1;
        roleDto.setNom("Administrateur Modifié");
        roleDto.setDescription("Nouvelle description");
        when(roleService.update(anyInt(), any(RoleDto.class))).thenReturn(roleDto);

        // When & Then
        mockMvc.perform(put("/api/roles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Administrateur Modifié"));

        verify(roleService, times(1)).update(id, roleDto);
    }

    @Test
    void update_WithException_ReturnsBadRequest() throws Exception {
        // Given
        Integer id = 1;
        when(roleService.update(anyInt(), any(RoleDto.class)))
                .thenThrow(new RuntimeException("Impossible de renommer : Un rôle avec le nom existe déjà."));

        // When & Then
        mockMvc.perform(put("/api/roles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).update(id, roleDto);
    }

    @Test
    void delete_Success() throws Exception {
        // Given
        Integer id = 1;
        doNothing().when(roleService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/roles/{id}", id))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).delete(id);
    }

    @Test
    void delete_WithException_ReturnsBadRequest() throws Exception {
        // Given
        Integer id = 1;
        doThrow(new RuntimeException("Impossible de supprimer le rôle, il est utilisé par 5 utilisateur(s)."))
                .when(roleService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/roles/{id}", id))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).delete(id);
    }

    @Test
    void delete_NotFound_ReturnsBadRequest() throws Exception {
        // Given
        Integer id = 999;
        doThrow(new RuntimeException("Rôle inexistant : " + id))
                .when(roleService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/roles/{id}", id))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).delete(id);
    }
}

