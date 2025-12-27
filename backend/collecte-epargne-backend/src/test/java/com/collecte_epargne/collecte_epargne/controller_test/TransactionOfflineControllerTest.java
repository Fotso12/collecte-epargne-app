package com.collecte_epargne.collecte_epargne.controller_test;


import com.collecte_epargne.collecte_epargne.controllers.TransactionOfflineController;
import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.services.interfaces.TransactionOfflineInterface;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionOfflineController.class)

public class TransactionOfflineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionOfflineInterface service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTransactionOffline_success() throws Exception {
        TransactionOfflineDto dto = new TransactionOfflineDto();
        dto.setIdOffline("OFF1");

        when(service.save(dto)).thenReturn(dto);

        mockMvc.perform(post("/api/transactions-offline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getById_success() throws Exception {
        when(service.getById("OFF1")).thenReturn(new TransactionOfflineDto());

        mockMvc.perform(get("/api/transactions-offline/OFF1"))
                .andExpect(status().isOk());
    }

    @Test
    void getByStatut_success() throws Exception {
        when(service.getByStatutSynchro(StatutSynchroOffline.EN_ATTENTE))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transactions-offline/statut/EN_ATTENTE"))
                .andExpect(status().isOk());
    }

    @Test
    void synchroniser_success() throws Exception {
        when(service.markAsSynced("OFF1", "TX1"))
                .thenReturn(new TransactionOfflineDto());

        mockMvc.perform(put("/api/transactions-offline/OFF1/synchroniser/TX1"))
                .andExpect(status().isOk());
    }
}
