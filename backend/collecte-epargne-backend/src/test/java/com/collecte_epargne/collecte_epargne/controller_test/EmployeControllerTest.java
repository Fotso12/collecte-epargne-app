package com.collecte_epargne.collecte_epargne.controller_test;

import com.collecte_epargne.collecte_epargne.controllers.EmployeController;
import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.services.implementations.EmployeService;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class EmployeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeService employeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveEmploye_success() throws Exception {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("EMP001");
        dto.setDateEmbauche(LocalDate.now());
        dto.setTypeEmploye(TypeEmploye.COLLECTEUR);
        dto.setCommissionTaux(BigDecimal.valueOf(5.0));
        dto.setLoginUtilisateur("user1");
        dto.setIdAgence(1);

        when(employeService.save(any(EmployeDto.class))).thenReturn(dto);

        // WHEN & THEN
        mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricule").value("EMP001"));

        verify(employeService, times(1)).save(any(EmployeDto.class));
    }

    @Test
    void saveEmploye_badRequest() throws Exception {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        when(employeService.save(any(EmployeDto.class)))
                .thenThrow(new RuntimeException("Erreur de validation"));

        // WHEN & THEN
        mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).save(any(EmployeDto.class));
    }

    @Test
    void getAllEmployes_success() throws Exception {
        // GIVEN
        EmployeDto dto1 = new EmployeDto();
        dto1.setMatricule("EMP001");
        EmployeDto dto2 = new EmployeDto();
        dto2.setMatricule("EMP002");
        List<EmployeDto> employes = Arrays.asList(dto1, dto2);

        when(employeService.getAll()).thenReturn(employes);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(employeService, times(1)).getAll();
    }

    @Test
    void getAllEmployes_internalServerError() throws Exception {
        // GIVEN
        when(employeService.getAll())
                .thenThrow(new RuntimeException("Erreur serveur"));

        // WHEN & THEN
        mockMvc.perform(get("/api/employes"))
                .andExpect(status().isInternalServerError());

        verify(employeService, times(1)).getAll();
    }

    @Test
    void getEmployeById_success() throws Exception {
        // GIVEN
        String matricule = "EMP001";
        EmployeDto dto = new EmployeDto();
        dto.setMatricule(matricule);
        dto.setTypeEmploye(TypeEmploye.COLLECTEUR);

        when(employeService.getById(matricule)).thenReturn(dto);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/{matricule}", matricule))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value(matricule));

        verify(employeService, times(1)).getById(matricule);
    }

    @Test
    void getEmployeById_notFound() throws Exception {
        // GIVEN
        String matricule = "INVALID";
        when(employeService.getById(matricule))
                .thenThrow(new RuntimeException("Employé non trouvé"));

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/{matricule}", matricule))
                .andExpect(status().isNotFound());

        verify(employeService, times(1)).getById(matricule);
    }

    @Test
    void updateEmploye_success() throws Exception {
        // GIVEN
        String matricule = "EMP001";
        EmployeDto dto = new EmployeDto();
        dto.setMatricule(matricule);
        dto.setTypeEmploye(TypeEmploye.SUPERVISEUR);
        dto.setCommissionTaux(BigDecimal.valueOf(10.0));

        when(employeService.update(anyString(), any(EmployeDto.class))).thenReturn(dto);

        // WHEN & THEN
        mockMvc.perform(put("/api/employes/{matricule}", matricule)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value(matricule));

        verify(employeService, times(1)).update(anyString(), any(EmployeDto.class));
    }

    @Test
    void updateEmploye_badRequest() throws Exception {
        // GIVEN
        String matricule = "EMP001";
        EmployeDto dto = new EmployeDto();
        when(employeService.update(anyString(), any(EmployeDto.class)))
                .thenThrow(new RuntimeException("Erreur de mise à jour"));

        // WHEN & THEN
        mockMvc.perform(put("/api/employes/{matricule}", matricule)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).update(anyString(), any(EmployeDto.class));
    }

    @Test
    void deleteEmploye_success() throws Exception {
        // GIVEN
        String matricule = "EMP001";
        doNothing().when(employeService).delete(matricule);

        // WHEN & THEN
        mockMvc.perform(delete("/api/employes/{matricule}", matricule))
                .andExpect(status().isNoContent());

        verify(employeService, times(1)).delete(matricule);
    }

    @Test
    void deleteEmploye_notFound() throws Exception {
        // GIVEN
        String matricule = "INVALID";
        doThrow(new RuntimeException("Employé inexistant"))
                .when(employeService).delete(matricule);

        // WHEN & THEN
        mockMvc.perform(delete("/api/employes/{matricule}", matricule))
                .andExpect(status().isNotFound());

        verify(employeService, times(1)).delete(matricule);
    }

    @Test
    void getSuperviseurs_success() throws Exception {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("SUP001");
        dto.setTypeEmploye(TypeEmploye.SUPERVISEUR);
        List<EmployeDto> superviseurs = Arrays.asList(dto);

        when(employeService.getSuperviseurs()).thenReturn(superviseurs);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/superviseurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(employeService, times(1)).getSuperviseurs();
    }

    @Test
    void getCaissiers_success() throws Exception {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("CAI001");
        dto.setTypeEmploye(TypeEmploye.CAISSIER);
        List<EmployeDto> caissiers = Arrays.asList(dto);

        when(employeService.getCaissiers()).thenReturn(caissiers);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/caissiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(employeService, times(1)).getCaissiers();
    }

    @Test
    void getCollecteurs_success() throws Exception {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");
        dto.setTypeEmploye(TypeEmploye.COLLECTEUR);
        List<EmployeDto> collecteurs = Arrays.asList(dto);

        when(employeService.getCollecteurs()).thenReturn(collecteurs);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/collecteurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(employeService, times(1)).getCollecteurs();
    }

    @Test
    void getCollecteursBySuperviseur_success() throws Exception {
        // GIVEN
        String idSuperviseur = "1";
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");
        List<EmployeDto> collecteurs = Arrays.asList(dto);

        when(employeService.getCollecteursBySuperviseur(idSuperviseur)).thenReturn(collecteurs);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/{idSuperviseur}/collecteurs", idSuperviseur))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(employeService, times(1)).getCollecteursBySuperviseur(idSuperviseur);
    }

    @Test
    void getCollecteursBySuperviseur_badRequest() throws Exception {
        // GIVEN
        String idSuperviseur = "INVALID";
        when(employeService.getCollecteursBySuperviseur(idSuperviseur))
                .thenThrow(new RuntimeException("Superviseur non trouvé"));

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/{idSuperviseur}/collecteurs", idSuperviseur))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).getCollecteursBySuperviseur(idSuperviseur);
    }

    @Test
    void getClientsByCollecteur_success() throws Exception {
        // GIVEN
        String idCollecteur = "1";
        ClientDto clientDto = new ClientDto();
        clientDto.setNumeroClient("1");
        List<ClientDto> clients = Arrays.asList(clientDto);

        when(employeService.getClientsByCollecteur(idCollecteur)).thenReturn(clients);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/collecteurs/{idCollecteur}/clients", idCollecteur))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(employeService, times(1)).getClientsByCollecteur(idCollecteur);
    }

    @Test
    void getClientsByCollecteur_badRequest() throws Exception {
        // GIVEN
        String idCollecteur = "INVALID";
        when(employeService.getClientsByCollecteur(idCollecteur))
                .thenThrow(new RuntimeException("Collecteur non trouvé"));

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/collecteurs/{idCollecteur}/clients", idCollecteur))
                .andExpect(status().isBadRequest());

        verify(employeService, times(1)).getClientsByCollecteur(idCollecteur);
    }

    @Test
    void getCollecteursByClientCount_success() throws Exception {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");
        List<EmployeDto> collecteurs = Arrays.asList(dto);

        when(employeService.getCollecteursOrderedByClientCount()).thenReturn(collecteurs);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/collecteurs/performance/client-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getCollecteursOrderedByClientCount();
    }

    @Test
    void getCollecteursByTotalClientScore_success() throws Exception {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");
        List<EmployeDto> collecteurs = Arrays.asList(dto);

        when(employeService.getCollecteursOrderedByTotalClientScore()).thenReturn(collecteurs);

        // WHEN & THEN
        mockMvc.perform(get("/api/employes/collecteurs/performance/score-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeService, times(1)).getCollecteursOrderedByTotalClientScore();
    }
}

