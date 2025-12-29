package com.collecte_epargne.collecte_epargne.controller_test;
import com.collecte_epargne.collecte_epargne.controllers.CompteController;
import com.collecte_epargne.collecte_epargne.dtos.CompteDto;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompteController.class)

public class CompteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompteService compteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void save_shouldReturnCreated() throws Exception {
        CompteDto dto = new CompteDto();

        when(compteService.save(dto)).thenReturn(dto);

        mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(compteService.getAll()).thenReturn(List.of(new CompteDto()));

        mockMvc.perform(get("/api/comptes"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(compteService.getById("CMP001")).thenReturn(new CompteDto());

        mockMvc.perform(get("/api/comptes/CMP001"))
                .andExpect(status().isOk());
    }

    @Test
    void getByNumCompte_shouldReturnOk() throws Exception {
        when(compteService.getByNumCompte("0001")).thenReturn(new CompteDto());

        mockMvc.perform(get("/api/comptes/numero/0001"))
                .andExpect(status().isOk());
    }

    @Test
    void getByClient_shouldReturnOk() throws Exception {
        when(compteService.getByClient("CL001")).thenReturn(List.of(new CompteDto()));

        mockMvc.perform(get("/api/comptes/client/CL001"))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        CompteDto dto = new CompteDto();

        when(compteService.update("CMP001", dto)).thenReturn(dto);

        mockMvc.perform(put("/api/comptes/CMP001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/comptes/CMP001"))
                .andExpect(status().isNoContent());
    }
}
