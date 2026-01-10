package com.collecte_epargne.collecte_epargne.service_test;
import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.mappers.AgenceZoneMapper;
import com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.AgenceZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AgenceZoneServiceTest {

    @Mock
    private AgenceZoneRepository agenceZoneRepository;

    @Mock
    private AgenceZoneMapper agenceZoneMapper;

    @InjectMocks
    private AgenceZoneService agenceZoneService;

    private AgenceZoneDto agenceZoneDto;
    private AgenceZone agenceZone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        agenceZoneDto = new AgenceZoneDto();
        agenceZoneDto.setNom("Agence Centrale");
        agenceZoneDto.setCode("AG001");

        agenceZone = new AgenceZone();
        agenceZone.setIdAgence(1);
        agenceZone.setNom("Agence Centrale");
        agenceZone.setCode("AG001");
    }

    @Test
    void save_shouldReturnAgenceZoneDto_whenDataIsValid() {
        when(agenceZoneMapper.toEntity(agenceZoneDto)).thenReturn(agenceZone);
        when(agenceZoneRepository.save(agenceZone)).thenReturn(agenceZone);
        when(agenceZoneMapper.toDto(agenceZone)).thenReturn(agenceZoneDto);

        AgenceZoneDto result = agenceZoneService.save(agenceZoneDto);

        assertNotNull(result);
        assertEquals("Agence Centrale", result.getNom());
        verify(agenceZoneRepository, times(1)).save(agenceZone);
    }

    @Test
    void getAll_shouldReturnListOfAgenceZoneDto() {
        when(agenceZoneRepository.findAll()).thenReturn(List.of(agenceZone));
        when(agenceZoneMapper.toDto(agenceZone)).thenReturn(agenceZoneDto);

        List<AgenceZoneDto> result = agenceZoneService.getAll();

        assertEquals(1, result.size());
        verify(agenceZoneRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnAgenceZoneDto_whenIdExists() {
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.of(agenceZone));
        when(agenceZoneMapper.toDto(agenceZone)).thenReturn(agenceZoneDto);

        AgenceZoneDto result = agenceZoneService.getById(1);

        assertNotNull(result);
        verify(agenceZoneRepository, times(1)).findById(1);
    }

    @Test
    void getById_shouldThrowException_whenIdDoesNotExist() {
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> agenceZoneService.getById(1));
    }

    @Test
    void delete_shouldCallRepositoryDelete_whenIdExists() {
        when(agenceZoneRepository.existsById(1)).thenReturn(true);

        agenceZoneService.delete(1);

        verify(agenceZoneRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_shouldThrowException_whenIdDoesNotExist() {
        when(agenceZoneRepository.existsById(1)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> agenceZoneService.delete(1));
    }
}
