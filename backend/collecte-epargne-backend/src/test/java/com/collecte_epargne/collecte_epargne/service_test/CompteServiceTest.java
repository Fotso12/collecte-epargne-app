package com.collecte_epargne.collecte_epargne.service_test;
import com.collecte_epargne.collecte_epargne.dtos.CompteDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import com.collecte_epargne.collecte_epargne.mappers.CompteMapper;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.CompteRepository;
import com.collecte_epargne.collecte_epargne.repositories.TypeCompteRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompteServiceTest {

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private CompteMapper compteMapper;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TypeCompteRepository typeCompteRepository;

    @InjectMocks
    private CompteService compteService;

    private Compte compte;
    private CompteDto compteDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        compte = new Compte();
        compte.setIdCompte("CMP001");
        compte.setSolde(BigDecimal.ZERO);

        compteDto = new CompteDto();
        compteDto.setNumCompte("0001");
        compteDto.setCodeClient("CL001");
        compteDto.setIdTypeCompte(1);
    }

    @Test
    void save_shouldCreateCompte_whenDataIsValid() {
        when(compteRepository.findByNumCompte("0001")).thenReturn(Optional.empty());
        when(clientRepository.existsByCodeClient("CL001")).thenReturn(true);
        when(compteMapper.toEntity(compteDto)).thenReturn(compte);
        when(compteRepository.save(compte)).thenReturn(compte);
        when(compteMapper.toDto(compte)).thenReturn(compteDto);

        CompteDto result = compteService.save(compteDto);

        assertNotNull(result);
        verify(compteRepository).save(compte);
    }

    @Test
    void save_shouldThrowException_whenNumCompteExists() {
        when(compteRepository.findByNumCompte("0001")).thenReturn(Optional.of(compte));

        assertThrows(RuntimeException.class, () -> compteService.save(compteDto));
    }

    @Test
    void save_shouldThrowException_whenClientDoesNotExist() {
        when(compteRepository.findByNumCompte("0001")).thenReturn(Optional.empty());
        when(clientRepository.existsByCodeClient("CL001")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> compteService.save(compteDto));
    }

    @Test
    void getAll_shouldReturnList() {
        when(compteRepository.findAll()).thenReturn(List.of(compte));
        when(compteMapper.toDto(compte)).thenReturn(compteDto);

        List<CompteDto> result = compteService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getById_shouldReturnCompte() {
        when(compteRepository.findById("CMP001")).thenReturn(Optional.of(compte));
        when(compteMapper.toDto(compte)).thenReturn(compteDto);

        CompteDto result = compteService.getById("CMP001");

        assertNotNull(result);
    }

    @Test
    void update_shouldUpdateCompte() {
        when(compteRepository.findById("CMP001")).thenReturn(Optional.of(compte));
        when(compteRepository.save(compte)).thenReturn(compte);
        when(compteMapper.toDto(compte)).thenReturn(compteDto);

        CompteDto result = compteService.update("CMP001", compteDto);

        assertNotNull(result);
    }

    @Test
    void delete_shouldDeleteCompte_whenExists() {
        when(compteRepository.existsById("CMP001")).thenReturn(true);

        compteService.delete("CMP001");

        verify(compteRepository).deleteById("CMP001");
    }

    @Test
    void delete_shouldThrowException_whenNotExists() {
        when(compteRepository.existsById("CMP001")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> compteService.delete("CMP001"));
    }

    @Test
    void getByNumCompte_shouldReturnCompte() {
        when(compteRepository.findByNumCompte("0001")).thenReturn(Optional.of(compte));
        when(compteMapper.toDto(compte)).thenReturn(compteDto);

        CompteDto result = compteService.getByNumCompte("0001");

        assertNotNull(result);
    }

    @Test
    void getByClient_shouldReturnComptes() {
        when(compteRepository.findByClientCodeClient("CL001")).thenReturn(List.of(compte));
        when(compteMapper.toDto(compte)).thenReturn(compteDto);

        List<CompteDto> result = compteService.getByClient("CL001");

        assertEquals(1, result.size());
    }
}
