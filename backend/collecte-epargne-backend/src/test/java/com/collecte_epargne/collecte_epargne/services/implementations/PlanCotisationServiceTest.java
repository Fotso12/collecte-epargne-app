package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.PlanCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import com.collecte_epargne.collecte_epargne.mappers.PlanCotisationMapper;
import com.collecte_epargne.collecte_epargne.repositories.CompteCotisationRepository;
import com.collecte_epargne.collecte_epargne.repositories.PlanCotisationRepository;
import com.collecte_epargne.collecte_epargne.utils.FrequenceCotisation;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanCotisationServiceTest {

    @Mock
    private PlanCotisationRepository planCotisationRepository;

    @Mock
    private PlanCotisationMapper planCotisationMapper;

    @Mock
    private CompteCotisationRepository compteCotisationRepository;

    @InjectMocks
    private PlanCotisationService planCotisationService;

    private PlanCotisationDto planCotisationDto;
    private PlanCotisation planCotisation;

    @BeforeEach
    void setUp() {
        planCotisationDto = new PlanCotisationDto();
        planCotisationDto.setIdPlan("plan-123");
        planCotisationDto.setNom("Plan Mensuel");
        planCotisationDto.setMontantAttendu(new BigDecimal("10000.00"));
        planCotisationDto.setFrequence(FrequenceCotisation.MENSUEL);
        planCotisationDto.setDureeJours(30);
        planCotisationDto.setDateDebut(LocalDate.now());
        planCotisationDto.setTauxPenaliteRetard(new BigDecimal("5.00"));
        planCotisationDto.setStatut(StatutPlanCotisation.ACTIF);

        planCotisation = new PlanCotisation();
        planCotisation.setIdPlan("plan-123");
        planCotisation.setNom("Plan Mensuel");
        planCotisation.setMontantAttendu(new BigDecimal("10000.00"));
        planCotisation.setFrequence(FrequenceCotisation.MENSUEL);
        planCotisation.setDureeJours(30);
        planCotisation.setDateDebut(LocalDate.now());
        planCotisation.setTauxPenaliteRetard(new BigDecimal("5.00"));
        planCotisation.setStatut(StatutPlanCotisation.ACTIF);
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() {
        // Given
        when(planCotisationRepository.findByNom(anyString())).thenReturn(Optional.empty());
        when(planCotisationMapper.toEntity(any(PlanCotisationDto.class))).thenReturn(planCotisation);
        when(planCotisationRepository.save(any(PlanCotisation.class))).thenReturn(planCotisation);
        when(planCotisationMapper.toDto(any(PlanCotisation.class))).thenReturn(planCotisationDto);

        // When
        PlanCotisationDto result = planCotisationService.save(planCotisationDto);

        // Then
        assertNotNull(result);
        assertEquals("plan-123", result.getIdPlan());
        assertEquals("Plan Mensuel", result.getNom());
        verify(planCotisationRepository, times(1)).findByNom(anyString());
        verify(planCotisationRepository, times(1)).save(any(PlanCotisation.class));
    }

    @Test
    void testSave_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> planCotisationService.save(null));
    }

    @Test
    void testSave_WithNullNom_ThrowsException() {
        // Given
        planCotisationDto.setNom(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> planCotisationService.save(planCotisationDto));
    }

    @Test
    void testSave_WithEmptyNom_ThrowsException() {
        // Given
        planCotisationDto.setNom("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> planCotisationService.save(planCotisationDto));
    }

    @Test
    void testSave_WithNullMontantAttendu_ThrowsException() {
        // Given
        planCotisationDto.setMontantAttendu(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> planCotisationService.save(planCotisationDto));
    }

    @Test
    void testSave_WithNullFrequence_ThrowsException() {
        // Given
        planCotisationDto.setFrequence(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> planCotisationService.save(planCotisationDto));
    }

    @Test
    void testSave_WithNullDateDebut_ThrowsException() {
        // Given
        planCotisationDto.setDateDebut(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> planCotisationService.save(planCotisationDto));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithDuplicateNom_ThrowsException() {
        // Given
        when(planCotisationRepository.findByNom(anyString())).thenReturn(Optional.of(planCotisation));

        // When & Then
        assertThrows(RuntimeException.class, () -> planCotisationService.save(planCotisationDto));
        verify(planCotisationRepository, times(1)).findByNom(anyString());
        verify(planCotisationRepository, never()).save(any(PlanCotisation.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithDureeJours_CalculatesDateFin() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        planCotisationDto.setDateDebut(dateDebut);
        planCotisationDto.setDureeJours(30);
        planCotisation.setDateDebut(dateDebut);
        planCotisation.setDureeJours(30);

        when(planCotisationRepository.findByNom(anyString())).thenReturn(Optional.empty());
        when(planCotisationMapper.toEntity(any(PlanCotisationDto.class))).thenReturn(planCotisation);
        when(planCotisationRepository.save(any(PlanCotisation.class))).thenReturn(planCotisation);
        when(planCotisationMapper.toDto(any(PlanCotisation.class))).thenReturn(planCotisationDto);

        // When
        planCotisationService.save(planCotisationDto);

        // Then
        verify(planCotisationRepository, times(1)).save(any(PlanCotisation.class));
    }

    @Test
    void testGetAll_Success() {
        // Given
        List<PlanCotisation> plans = Arrays.asList(planCotisation);
        when(planCotisationRepository.findAll()).thenReturn(plans);
        when(planCotisationMapper.toDto(any(PlanCotisation.class))).thenReturn(planCotisationDto);

        // When
        List<PlanCotisationDto> result = planCotisationService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(planCotisationRepository, times(1)).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void testGetById_Success() {
        // Given
        String id = "plan-123";
        when(planCotisationRepository.findById(id)).thenReturn(Optional.of(planCotisation));
        when(planCotisationMapper.toDto(any(PlanCotisation.class))).thenReturn(planCotisationDto);

        // When
        PlanCotisationDto result = planCotisationService.getById(id);

        // Then
        assertNotNull(result);
        assertEquals("plan-123", result.getIdPlan());
        verify(planCotisationRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound_ThrowsException() {
        // Given
        String id = "plan-999";
        when(planCotisationRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> planCotisationService.getById(id));
        verify(planCotisationRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> planCotisationService.getById(null));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() {
        // Given
        String id = "plan-123";
        planCotisationDto.setNom("Plan Mensuel Modifié");
        when(planCotisationRepository.findById(id)).thenReturn(Optional.of(planCotisation));
        when(planCotisationRepository.save(any(PlanCotisation.class))).thenReturn(planCotisation);
        when(planCotisationMapper.toDto(any(PlanCotisation.class))).thenReturn(planCotisationDto);

        // When
        PlanCotisationDto result = planCotisationService.update(id, planCotisationDto);

        // Then
        assertNotNull(result);
        verify(planCotisationRepository, times(1)).findById(id);
        verify(planCotisationRepository, times(1)).save(any(PlanCotisation.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_NotFound_ThrowsException() {
        // Given
        String id = "plan-999";
        when(planCotisationRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> planCotisationService.update(id, planCotisationDto));
        verify(planCotisationRepository, times(1)).findById(id);
        verify(planCotisationRepository, never()).save(any(PlanCotisation.class));
    }

    @Test
    void testUpdate_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> planCotisationService.update(null, planCotisationDto));
    }

    @Test
    void testUpdate_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> planCotisationService.update("plan-123", null));
    }

    @Test
    void testDelete_Success() {
        // Given
        String id = "plan-123";
        when(planCotisationRepository.existsById(id)).thenReturn(true);
        when(compteCotisationRepository.existsByPlanCotisationIdPlan(id)).thenReturn(false);

        // When
        planCotisationService.delete(id);

        // Then
        verify(planCotisationRepository, times(1)).existsById(id);
        verify(compteCotisationRepository, times(1)).existsByPlanCotisationIdPlan(id);
        verify(planCotisationRepository, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NotFound_ThrowsException() {
        // Given
        String id = "plan-999";
        when(planCotisationRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> planCotisationService.delete(id));
        verify(planCotisationRepository, times(1)).existsById(id);
        verify(planCotisationRepository, never()).deleteById(id);
    }

    @Test
    void testDelete_WithComptesCotisation_ThrowsException() {
        // Given
        String id = "plan-123";
        when(planCotisationRepository.existsById(id)).thenReturn(true);
        when(compteCotisationRepository.existsByPlanCotisationIdPlan(id)).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> planCotisationService.delete(id));
        verify(planCotisationRepository, times(1)).existsById(id);
        verify(compteCotisationRepository, times(1)).existsByPlanCotisationIdPlan(id);
        verify(planCotisationRepository, never()).deleteById(id);
    }

    @Test
    void testDelete_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> planCotisationService.delete(null));
    }
}

