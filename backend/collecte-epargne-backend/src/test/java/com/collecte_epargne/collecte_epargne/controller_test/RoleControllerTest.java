package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.RoleController;
import com.collecte_epargne.collecte_epargne.dtos.RoleDto;
import com.collecte_epargne.collecte_epargne.services.implementations.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveRole_success() throws Exception {
        // GIVEN
        RoleDto dto = new RoleDto();
        dto.setCode("ADMIN");
        dto.setNom("Administrateur");
        dto.setDescription("Rôle administrateur");

        when(roleService.save(any(RoleDto.class))).thenReturn(dto);

        // WHEN & THEN
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ADMIN"))
                .andExpect(jsonPath("$.nom").value("Administrateur"));

        verify(roleService, times(1)).save(any(RoleDto.class));
    }

    @Test
    void saveRole_badRequest() throws Exception {
        // GIVEN
        RoleDto dto = new RoleDto();
        when(roleService.save(any(RoleDto.class)))
                .thenThrow(new RuntimeException("Un rôle avec le nom 'ADMIN' existe déjà."));

        // WHEN & THEN
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).save(any(RoleDto.class));
    }

    @Test
    void getAllRoles_success() throws Exception {
        // GIVEN
        RoleDto dto1 = new RoleDto();
        dto1.setId(1);
        dto1.setCode("ADMIN");
        dto1.setNom("Administrateur");

        RoleDto dto2 = new RoleDto();
        dto2.setId(2);
        dto2.setCode("USER");
        dto2.setNom("Utilisateur");

        List<RoleDto> roles = Arrays.asList(dto1, dto2);

        when(roleService.getAll()).thenReturn(roles);

        // WHEN & THEN
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("ADMIN"))
                .andExpect(jsonPath("$[1].code").value("USER"));

        verify(roleService, times(1)).getAll();
    }

    @Test
    void getAllRoles_internalServerError() throws Exception {
        // GIVEN
        when(roleService.getAll())
                .thenThrow(new RuntimeException("Erreur serveur"));

        // WHEN & THEN
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isInternalServerError());

        verify(roleService, times(1)).getAll();
    }

    @Test
    void getRoleById_success() throws Exception {
        // GIVEN
        Integer id = 1;
        RoleDto dto = new RoleDto();
        dto.setId(id);
        dto.setCode("ADMIN");
        dto.setNom("Administrateur");
        dto.setDescription("Rôle administrateur");

        when(roleService.getById(id)).thenReturn(dto);

        // WHEN & THEN
        mockMvc.perform(get("/api/roles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.code").value("ADMIN"))
                .andExpect(jsonPath("$.nom").value("Administrateur"));

        verify(roleService, times(1)).getById(id);
    }

    @Test
    void getRoleById_notFound() throws Exception {
        // GIVEN
        Integer id = 999;
        when(roleService.getById(id))
                .thenThrow(new RuntimeException("Rôle non trouvé avec l'ID : " + id));

        // WHEN & THEN
        mockMvc.perform(get("/api/roles/{id}", id))
                .andExpect(status().isNotFound());

        verify(roleService, times(1)).getById(id);
    }

    @Test
    void updateRole_success() throws Exception {
        // GIVEN
        Integer id = 1;
        RoleDto dto = new RoleDto();
        dto.setId(id);
        dto.setCode("ADMIN");
        dto.setNom("Administrateur Modifié");
        dto.setDescription("Description modifiée");

        when(roleService.update(anyInt(), any(RoleDto.class))).thenReturn(dto);

        // WHEN & THEN
        mockMvc.perform(put("/api/roles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nom").value("Administrateur Modifié"));

        verify(roleService, times(1)).update(anyInt(), any(RoleDto.class));
    }

    @Test
    void updateRole_badRequest() throws Exception {
        // GIVEN
        Integer id = 1;
        RoleDto dto = new RoleDto();
        when(roleService.update(anyInt(), any(RoleDto.class)))
                .thenThrow(new RuntimeException("Un rôle avec le nom 'ADMIN' existe déjà."));

        // WHEN & THEN
        mockMvc.perform(put("/api/roles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).update(anyInt(), any(RoleDto.class));
    }

    @Test
    void deleteRole_success() throws Exception {
        // GIVEN
        Integer id = 1;
        doNothing().when(roleService).delete(id);

        // WHEN & THEN
        mockMvc.perform(delete("/api/roles/{id}", id))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).delete(id);
    }

    @Test
    void deleteRole_badRequest() throws Exception {
        // GIVEN
        Integer id = 1;
        doThrow(new RuntimeException("Impossible de supprimer le rôle, il est utilisé par 5 utilisateur(s)."))
                .when(roleService).delete(id);

        // WHEN & THEN
        mockMvc.perform(delete("/api/roles/{id}", id))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).delete(id);
    }
}

