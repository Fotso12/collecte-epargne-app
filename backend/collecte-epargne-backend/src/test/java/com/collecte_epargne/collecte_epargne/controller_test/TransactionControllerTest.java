package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.TransactionController;
import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.services.implementations.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTransaction_success() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setIdTransaction("TX1");

        when(transactionService.create(dto)).thenReturn(dto);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getTransactionById_success() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setIdTransaction("TX1");

        when(transactionService.getById("TX1")).thenReturn(dto);

        mockMvc.perform(get("/api/transactions/TX1"))
                .andExpect(status().isOk());
    }

    @Test
    void validerParCaissier_success() throws Exception {
        when(transactionService.validerParCaissier("TX1", "1"))
                .thenReturn(new TransactionDto());

        mockMvc.perform(put("/api/transactions/TX1/valider-caissier/1"))
                .andExpect(status().isOk());
    }

    @Test
    void rejeterTransaction_success() throws Exception {
        mockMvc.perform(put("/api/transactions/TX1/rejeter")
                        .param("motif", "Erreur"))
                .andExpect(status().isNoContent());
    }

}
