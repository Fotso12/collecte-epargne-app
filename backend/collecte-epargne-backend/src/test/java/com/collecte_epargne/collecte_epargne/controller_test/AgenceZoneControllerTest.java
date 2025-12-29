package com.collecte_epargne.collecte_epargne.controller_test;
import com.collecte_epargne.collecte_epargne.controllers.AgenceZoneController;
import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;
import com.collecte_epargne.collecte_epargne.services.implementations.AgenceZoneService;
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

@WebMvcTest(AgenceZoneController.class)

public class AgenceZoneControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgenceZoneService agenceZoneService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void save_shouldReturnCreated() throws Exception {
        AgenceZoneDto dto = new AgenceZoneDto();
        dto.setNom("Agence Test");
        dto.setCode("AGT01");

        when(agenceZoneService.save(dto)).thenReturn(dto);

        mockMvc.perform(post("/api/AgenceZone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(agenceZoneService.getAll()).thenReturn(List.of(new AgenceZoneDto()));

        mockMvc.perform(get("/api/AgenceZone"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(agenceZoneService.getById(1)).thenReturn(new AgenceZoneDto());

        mockMvc.perform(get("/api/AgenceZone/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/AgenceZone/1"))
                .andExpect(status().isOk());
    }
}
