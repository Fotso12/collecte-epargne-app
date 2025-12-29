package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.EmployeController;
import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.services.implementations.EmployeService;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeController.class)
class EmployeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeService employeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeDto employeDto;

    @BeforeEach
    void setUp() {
        employeDto = new EmployeDto();
        employeDto.setIdEmploye(1);
        employeDto.setMatricule("COLL2025001");
        employeDto.setDateEmbauche(LocalDate.now());
        employeDto.setTypeEmploye(TypeEmploye.COLLECTEUR);
        employeDto.setCommissionTaux(new BigDecimal("5.00"));
        employeDto.setLoginUtilisateur("user123");
        employeDto.setIdAgence(1);
        employeDto.setIdSuperviseur("1");
    }

    @Test
    void save_Success() throws Exception {
        // Given
        when(employeService.save(any(EmployeDto.class))).thenReturn(employeDto);

        // When & Then
        mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricule").value("COLL2025001"));

        verify(employeService, times(1)).save(any(EmployeDto.class));
    }

    @Test
    void save_WithException_ReturnsBadRequest() throws Exception {
        // Given
        when(employeService.save(any(EmployeDto.class)))
                .thenThrow(new RuntimeException("Erreur de validation"));

        // When & Then
        mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeDto)))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).save(any(EmployeDto.class));
    }

    @Test
    void getAll_Success() throws Exception {
        // Given
        List<EmployeDto> employes = Arrays.asList(employeDto);
        when(employeService.getAll()).thenReturn(employes);

        // When & Then
        mockMvc.perform(get("/api/employes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].matricule").value("COLL2025001"));

        verify(employeService, times(1)).getAll();
    }

    @Test
    void getAll_WithException_ThrowsResponseStatusException() throws Exception {
        // Given
        when(employeService.getAll())
                .thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(get("/api/employes"))
                .andExpect(status().isInternalServerError());

        verify(employeService, times(1)).getAll();
    }

    @Test
    void getById_Success() throws Exception {
        // Given
        String matricule = "COLL2025001";
        when(employeService.getById(matricule)).thenReturn(employeDto);

        // When & Then
        mockMvc.perform(get("/api/employes/{matricule}", matricule))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("COLL2025001"));

        verify(employeService, times(1)).getById(matricule);
    }

    @Test
    void getById_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String matricule = "COLL9999999";
        when(employeService.getById(matricule))
                .thenThrow(new RuntimeException("Employé non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/employes/{matricule}", matricule))
                .andExpect(status().isNotFound());

        verify(employeService, times(1)).getById(matricule);
    }

    @Test
    void update_Success() throws Exception {
        // Given
        String matricule = "COLL2025001";
        employeDto.setCommissionTaux(new BigDecimal("7.50"));
        when(employeService.update(anyString(), any(EmployeDto.class))).thenReturn(employeDto);

        // When & Then
        mockMvc.perform(put("/api/employes/{matricule}", matricule)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("COLL2025001"));

        verify(employeService, times(1)).update(matricule, employeDto);
    }

    @Test
    void update_WithException_ReturnsBadRequest() throws Exception {
        // Given
        String matricule = "COLL2025001";
        when(employeService.update(anyString(), any(EmployeDto.class)))
                .thenThrow(new RuntimeException("Erreur de validation"));

        // When & Then
        mockMvc.perform(put("/api/employes/{matricule}", matricule)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeDto)))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).update(matricule, employeDto);
    }

    @Test
    void delete_Success() throws Exception {
        // Given
        String matricule = "COLL2025001";
        doNothing().when(employeService).delete(matricule);

        // When & Then
        mockMvc.perform(delete("/api/employes/{matricule}", matricule))
                .andExpect(status().isNoContent());

        verify(employeService, times(1)).delete(matricule);
    }

    @Test
    void delete_NotFound_ReturnsNotFound() throws Exception {
        // Given
        String matricule = "COLL9999999";
        doThrow(new RuntimeException("Employé inexistant"))
                .when(employeService).delete(matricule);

        // When & Then
        mockMvc.perform(delete("/api/employes/{matricule}", matricule))
                .andExpect(status().isNotFound());

        verify(employeService, times(1)).delete(matricule);
    }

    @Test
    void getSuperviseurs_Success() throws Exception {
        // Given
        List<EmployeDto> superviseurs = Arrays.asList(employeDto);
        when(employeService.getSuperviseurs()).thenReturn(superviseurs);

        // When & Then
        mockMvc.perform(get("/api/employes/superviseurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getSuperviseurs();
    }

    @Test
    void getCaissiers_Success() throws Exception {
        // Given
        List<EmployeDto> caissiers = Arrays.asList(employeDto);
        when(employeService.getCaissiers()).thenReturn(caissiers);

        // When & Then
        mockMvc.perform(get("/api/employes/caissiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getCaissiers();
    }

    @Test
    void getCollecteurs_Success() throws Exception {
        // Given
        List<EmployeDto> collecteurs = Arrays.asList(employeDto);
        when(employeService.getCollecteurs()).thenReturn(collecteurs);

        // When & Then
        mockMvc.perform(get("/api/employes/collecteurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getCollecteurs();
    }

    @Test
    void getCollecteursBySuperviseur_Success() throws Exception {
        // Given
        String idSuperviseur = "1";
        List<EmployeDto> collecteurs = Arrays.asList(employeDto);
        when(employeService.getCollecteursBySuperviseur(idSuperviseur)).thenReturn(collecteurs);

        // When & Then
        mockMvc.perform(get("/api/employes/{idSuperviseur}/collecteurs", idSuperviseur))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getCollecteursBySuperviseur(idSuperviseur);
    }

    @Test
    void getCollecteursBySuperviseur_WithException_ReturnsBadRequest() throws Exception {
        // Given
        String idSuperviseur = "999";
        when(employeService.getCollecteursBySuperviseur(idSuperviseur))
                .thenThrow(new RuntimeException("Superviseur non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/employes/{idSuperviseur}/collecteurs", idSuperviseur))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).getCollecteursBySuperviseur(idSuperviseur);
    }

    @Test
    void getClientsByCollecteur_Success() throws Exception {
        // Given
        String idCollecteur = "2";
        ClientDto clientDto = new ClientDto();
        clientDto.setCodeClient("CLT2025001");
        List<ClientDto> clients = Arrays.asList(clientDto);
        when(employeService.getClientsByCollecteur(idCollecteur)).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/employes/collecteurs/{idCollecteur}/clients", idCollecteur))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getClientsByCollecteur(idCollecteur);
    }

    @Test
    void getClientsByCollecteur_WithException_ThrowsResponseStatusException() throws Exception {
        // Given
        String idCollecteur = "999";
        when(employeService.getClientsByCollecteur(idCollecteur))
                .thenThrow(new RuntimeException("Collecteur non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/employes/collecteurs/{idCollecteur}/clients", idCollecteur))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).getClientsByCollecteur(idCollecteur);
    }

    @Test
    void getCollecteursByClientCount_Success() throws Exception {
        // Given
        List<EmployeDto> collecteurs = Arrays.asList(employeDto);
        when(employeService.getCollecteursOrderedByClientCount()).thenReturn(collecteurs);

        // When & Then
        mockMvc.perform(get("/api/employes/collecteurs/performance/client-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getCollecteursOrderedByClientCount();
    }

    @Test
    void getCollecteursByTotalClientScore_Success() throws Exception {
        // Given
        List<EmployeDto> collecteurs = Arrays.asList(employeDto);
        when(employeService.getCollecteursOrderedByTotalClientScore()).thenReturn(collecteurs);

        // When & Then
        mockMvc.perform(get("/api/employes/collecteurs/performance/score-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getCollecteursOrderedByTotalClientScore();
    }
}

