package com.collecte_epargne.collecte_epargne.service_test;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import com.collecte_epargne.collecte_epargne.mappers.CompteCotisationMapper;
import com.collecte_epargne.collecte_epargne.repositories.CompteCotisationRepository;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.PlanCotisationRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteCotisationService;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompteCotisationServiceTest {

    @Mock
    private CompteCotisationRepository compteCotisationRepository;

    @Mock
    private CompteCotisationMapper compteCotisationMapper;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private PlanCotisationRepository planCotisationRepository;

    @InjectMocks
    private CompteCotisationService compteCotisationService;

    private CompteCotisationDto compteCotisationDto;
    private CompteCotisation compteCotisation;
    private Compte compte;
    private PlanCotisation planCotisation;

    @BeforeEach
    void setUp() {
        compte = new Compte();
        compte.setIdCompte("compte-123");

        planCotisation = new PlanCotisation();
        planCotisation.setIdPlan("plan-123");

        compteCotisationDto = new CompteCotisationDto();
        compteCotisationDto.setId("cc-123");
        compteCotisationDto.setDateAdhesion(LocalDate.now());
        compteCotisationDto.setMontantTotalVerse(new BigDecimal("5000.00"));
        compteCotisationDto.setNombreVersements(5);
        compteCotisationDto.setNombreRetards(0);
        compteCotisationDto.setProchaineEcheance(LocalDate.now().plusDays(30));
        compteCotisationDto.setStatut(StatutPlanCotisation.ACTIF);
        compteCotisationDto.setIdCompte("compte-123");
        compteCotisationDto.setIdPlanCotisation("plan-123");

        compteCotisation = new CompteCotisation();
        compteCotisation.setId("cc-123");
        compteCotisation.setDateAdhesion(LocalDate.now());
        compteCotisation.setMontantTotalVerse(new BigDecimal("5000.00"));
        compteCotisation.setNombreVersements(5);
        compteCotisation.setNombreRetards(0);
        compteCotisation.setProchaineEcheance(LocalDate.now().plusDays(30));
        compteCotisation.setStatut(StatutPlanCotisation.ACTIF);
        compteCotisation.setCompte(compte);
        compteCotisation.setPlanCotisation(planCotisation);
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() {
        // Given
        when(compteRepository.findById("compte-123")).thenReturn(Optional.of(compte));
        when(planCotisationRepository.findById("plan-123")).thenReturn(Optional.of(planCotisation));
        when(compteCotisationMapper.toEntity(any(CompteCotisationDto.class))).thenReturn(compteCotisation);
        when(compteCotisationRepository.save(any(CompteCotisation.class))).thenReturn(compteCotisation);
        when(compteCotisationMapper.toDto(any(CompteCotisation.class))).thenReturn(compteCotisationDto);

        // When
        CompteCotisationDto result = compteCotisationService.save(compteCotisationDto);

        // Then
        assertNotNull(result);
        assertEquals("cc-123", result.getId());
        verify(compteRepository, times(1)).findById("compte-123");
        verify(planCotisationRepository, times(1)).findById("plan-123");
        verify(compteCotisationRepository, times(1)).save(any(CompteCotisation.class));
    }

    @Test
    void testSave_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> compteCotisationService.save(null));
    }

    @Test
    void testSave_WithNullIdCompte_ThrowsException() {
        // Given
        compteCotisationDto.setIdCompte(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> compteCotisationService.save(compteCotisationDto));
    }

    @Test
    void testSave_WithNullIdPlanCotisation_ThrowsException() {
        // Given
        compteCotisationDto.setIdPlanCotisation(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> compteCotisationService.save(compteCotisationDto));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithCompteNotFound_ThrowsException() {
        // Given
        compteCotisationDto.setIdCompte("compte-999");
        when(compteCotisationMapper.toEntity(any(CompteCotisationDto.class))).thenReturn(compteCotisation);
        when(compteRepository.findById("compte-999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> compteCotisationService.save(compteCotisationDto));
        verify(compteRepository, times(1)).findById("compte-999");
        verify(compteCotisationRepository, never()).save(any(CompteCotisation.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithPlanCotisationNotFound_ThrowsException() {
        // Given
        compteCotisationDto.setIdPlanCotisation("plan-999");
        when(compteCotisationMapper.toEntity(any(CompteCotisationDto.class))).thenReturn(compteCotisation);
        when(compteRepository.findById("compte-123")).thenReturn(Optional.of(compte));
        when(planCotisationRepository.findById("plan-999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> compteCotisationService.save(compteCotisationDto));
        verify(compteRepository, times(1)).findById("compte-123");
        verify(planCotisationRepository, times(1)).findById("plan-999");
        verify(compteCotisationRepository, never()).save(any(CompteCotisation.class));
    }

    @Test
    @SuppressWarnings("null")
    void testGetAll_Success() {
        // Given
        List<CompteCotisation> comptes = Arrays.asList(compteCotisation);
        when(compteCotisationRepository.findAll()).thenReturn(comptes);
        when(compteCotisationMapper.toDto(any(CompteCotisation.class))).thenReturn(compteCotisationDto);

        // When
        List<CompteCotisationDto> result = compteCotisationService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(compteCotisationRepository, times(1)).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void testGetById_Success() {
        // Given
        String id = "cc-123";
        when(compteCotisationRepository.findById(id)).thenReturn(Optional.of(compteCotisation));
        when(compteCotisationMapper.toDto(any(CompteCotisation.class))).thenReturn(compteCotisationDto);

        // When
        CompteCotisationDto result = compteCotisationService.getById(id);

        // Then
        assertNotNull(result);
        assertEquals("cc-123", result.getId());
        verify(compteCotisationRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound_ThrowsException() {
        // Given
        String id = "cc-999";
        when(compteCotisationRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> compteCotisationService.getById(id));
        verify(compteCotisationRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> compteCotisationService.getById(null));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() {
        // Given
        String id = "cc-123";
        compteCotisationDto.setMontantTotalVerse(new BigDecimal("10000.00"));
        when(compteCotisationRepository.findById(id)).thenReturn(Optional.of(compteCotisation));
        when(compteRepository.findById("compte-123")).thenReturn(Optional.of(compte));
        when(planCotisationRepository.findById("plan-123")).thenReturn(Optional.of(planCotisation));
        when(compteCotisationRepository.save(any(CompteCotisation.class))).thenReturn(compteCotisation);
        when(compteCotisationMapper.toDto(any(CompteCotisation.class))).thenReturn(compteCotisationDto);

        // When
        CompteCotisationDto result = compteCotisationService.update(id, compteCotisationDto);

        // Then
        assertNotNull(result);
        verify(compteCotisationRepository, times(1)).findById(id);
        verify(compteCotisationRepository, times(1)).save(any(CompteCotisation.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_NotFound_ThrowsException() {
        // Given
        String id = "cc-999";
        when(compteCotisationRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> compteCotisationService.update(id, compteCotisationDto));
        verify(compteCotisationRepository, times(1)).findById(id);
        verify(compteCotisationRepository, never()).save(any(CompteCotisation.class));
    }

    @Test
    void testUpdate_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> compteCotisationService.update(null, compteCotisationDto));
    }

    @Test
    void testUpdate_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> compteCotisationService.update("cc-123", null));
    }

    @Test
    void testDelete_Success() {
        // Given
        String id = "cc-123";
        when(compteCotisationRepository.existsById(id)).thenReturn(true);

        // When
        compteCotisationService.delete(id);

        // Then
        verify(compteCotisationRepository, times(1)).existsById(id);
        verify(compteCotisationRepository, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NotFound_ThrowsException() {
        // Given
        String id = "cc-999";
        when(compteCotisationRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> compteCotisationService.delete(id));
        verify(compteCotisationRepository, times(1)).existsById(id);
        verify(compteCotisationRepository, never()).deleteById(id);
    }

    @Test
    void testDelete_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> compteCotisationService.delete(null));
    }

    @Test
    @SuppressWarnings("null")
    void testGetByCompte_Success() {
        // Given
        String idCompte = "compte-123";
        List<CompteCotisation> comptes = Arrays.asList(compteCotisation);
        when(compteCotisationRepository.findByCompteIdCompte(idCompte)).thenReturn(comptes);
        when(compteCotisationMapper.toDto(any(CompteCotisation.class))).thenReturn(compteCotisationDto);

        // When
        List<CompteCotisationDto> result = compteCotisationService.getByCompte(idCompte);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(compteCotisationRepository, times(1)).findByCompteIdCompte(idCompte);
    }

    @Test
    void testGetByCompte_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> compteCotisationService.getByCompte(null));
    }

    @Test
    @SuppressWarnings("null")
    void testGetByPlanCotisation_Success() {
        // Given
        String idPlan = "plan-123";
        List<CompteCotisation> comptes = Arrays.asList(compteCotisation);
        when(compteCotisationRepository.findByPlanCotisationIdPlan(idPlan)).thenReturn(comptes);
        when(compteCotisationMapper.toDto(any(CompteCotisation.class))).thenReturn(compteCotisationDto);

        // When
        List<CompteCotisationDto> result = compteCotisationService.getByPlanCotisation(idPlan);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(compteCotisationRepository, times(1)).findByPlanCotisationIdPlan(idPlan);
    }

    @Test
    void testGetByPlanCotisation_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> compteCotisationService.getByPlanCotisation(null));
    }
}

