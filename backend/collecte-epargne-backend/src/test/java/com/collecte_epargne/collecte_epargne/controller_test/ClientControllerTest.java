package com.collecte_epargne.collecte_epargne.controller_test;
import com.collecte_epargne.collecte_epargne.controllers.ClientController;
import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.services.implementations.ClientService;
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

@WebMvcTest(ClientController.class)

public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void save_shouldReturnCreated() throws Exception {
        ClientDto dto = new ClientDto();
        dto.setNumeroClient(1001L);

        when(clientService.save(dto)).thenReturn(dto);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(clientService.getAll()).thenReturn(List.of(new ClientDto()));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(clientService.getById(1001L)).thenReturn(new ClientDto());

        mockMvc.perform(get("/api/clients/1001"))
                .andExpect(status().isOk());
    }

    @Test
    void getByCodeClient_shouldReturnOk() throws Exception {
        when(clientService.getByCodeClient("C001")).thenReturn(new ClientDto());

        mockMvc.perform(get("/api/clients/code/C001"))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        ClientDto dto = new ClientDto();

        when(clientService.update(1001L, dto)).thenReturn(dto);

        mockMvc.perform(put("/api/clients/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/clients/1001"))
                .andExpect(status().isNoContent());
    }
}
