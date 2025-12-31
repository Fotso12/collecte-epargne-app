package com.collecte_epargne.collecte_epargne.service_test;

import com.collecte_epargne.collecte_epargne.dtos.TypeCompteDto;
import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import com.collecte_epargne.collecte_epargne.mappers.TypeCompteMapper;
import com.collecte_epargne.collecte_epargne.repositories.TypeCompteRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.TypeCompteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TypeCompteServiceTest {

    @Mock
    private TypeCompteRepository typeCompteRepository;

    @Mock
    private TypeCompteMapper typeCompteMapper;

    @InjectMocks
    private TypeCompteService typeCompteService;

    private TypeCompteDto typeCompteDto;
    private TypeCompte typeCompte;

    @BeforeEach
    void setUp() {
        typeCompteDto = new TypeCompteDto();
        typeCompteDto.setId(1);
        typeCompteDto.setCode("TC001");
        typeCompteDto.setNom("Type Compte Test");
        typeCompteDto.setDescription("Description test");
        typeCompteDto.setTauxInteret(new BigDecimal("5.00"));
        typeCompteDto.setSoldeMinimum(new BigDecimal("100.00"));
        typeCompteDto.setFraisOuverture(new BigDecimal("10.00"));
        typeCompteDto.setFraisCloture(new BigDecimal("5.00"));
        typeCompteDto.setAutoriserRetrait(true);
        typeCompteDto.setDureeBlocageJours(30);

        typeCompte = new TypeCompte();
        typeCompte.setId(1);
        typeCompte.setCode("TC001");
        typeCompte.setNom("Type Compte Test");
        typeCompte.setDescription("Description test");
        typeCompte.setTauxInteret(new BigDecimal("5.00"));
        typeCompte.setSoldeMinimum(new BigDecimal("100.00"));
        typeCompte.setFraisOuverture(new BigDecimal("10.00"));
        typeCompte.setFraisCloture(new BigDecimal("5.00"));
        typeCompte.setAutoriserRetrait(true);
        typeCompte.setDureeBlocageJours(30);
    }

    @Test
    void testSave_Success() {
        when(typeCompteRepository.findByCode("TC001")).thenReturn(Optional.empty());
        when(typeCompteMapper.toEntity(any(TypeCompteDto.class))).thenReturn(typeCompte);
        when(typeCompteRepository.save(any(TypeCompte.class))).thenReturn(typeCompte);
        when(typeCompteMapper.toDto(any(TypeCompte.class))).thenReturn(typeCompteDto);

        TypeCompteDto result = typeCompteService.save(typeCompteDto);

        assertNotNull(result);
        assertEquals("TC001", result.getCode());
        verify(typeCompteRepository).save(any(TypeCompte.class));
    }

    @Test
    void testSave_WithNullDto_ThrowsNullPointerException() {
        // Corrigé : attend NullPointerException (Objects.requireNonNull)
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> typeCompteService.save(null));
        assertEquals("typeCompteDto ne doit pas être null", exception.getMessage());
    }

    @Test
    void testSave_WithNullCode_ThrowsIllegalArgumentException() {
        typeCompteDto.setCode(null);
        // Reste IllegalArgumentException car c'est un check if (code == null) manuel dans votre service
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> typeCompteService.save(typeCompteDto));
        assertEquals("Le code du type de compte est obligatoire.", exception.getMessage());
    }

    @Test
    void testSave_WithExistingCode_ThrowsRuntimeException() {
        when(typeCompteRepository.findByCode("TC001")).thenReturn(Optional.of(typeCompte));
        assertThrows(RuntimeException.class, () -> typeCompteService.save(typeCompteDto));
    }

    @Test
    void testGetAll_Success() {
        when(typeCompteRepository.findAll()).thenReturn(Arrays.asList(typeCompte));
        when(typeCompteMapper.toDto(any(TypeCompte.class))).thenReturn(typeCompteDto);

        List<TypeCompteDto> result = typeCompteService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetById_Success() {
        when(typeCompteRepository.findById(1)).thenReturn(Optional.of(typeCompte));
        when(typeCompteMapper.toDto(typeCompte)).thenReturn(typeCompteDto);

        TypeCompteDto result = typeCompteService.getById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetById_WithNullId_ThrowsNullPointerException() {
        // Corrigé : attend NullPointerException
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> typeCompteService.getById(null));
        assertEquals("id ne doit pas être null", exception.getMessage());
    }

    @Test
    void testGetByCode_Success() {
        when(typeCompteRepository.findByCode("TC001")).thenReturn(Optional.of(typeCompte));
        when(typeCompteMapper.toDto(typeCompte)).thenReturn(typeCompteDto);

        TypeCompteDto result = typeCompteService.getByCode("TC001");

        assertNotNull(result);
        assertEquals("TC001", result.getCode());
    }

    @Test
    void testGetByCode_WithNullCode_ThrowsNullPointerException() {
        // Corrigé : attend NullPointerException
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> typeCompteService.getByCode(null));
        assertEquals("code ne doit pas être null", exception.getMessage());
    }

    @Test
    void testUpdate_Success() {
        when(typeCompteRepository.findById(1)).thenReturn(Optional.of(typeCompte));
        when(typeCompteRepository.save(any(TypeCompte.class))).thenReturn(typeCompte);
        when(typeCompteMapper.toDto(any(TypeCompte.class))).thenReturn(typeCompteDto);

        TypeCompteDto result = typeCompteService.update(1, typeCompteDto);

        assertNotNull(result);
        verify(typeCompteRepository).save(typeCompte);
    }

    @Test
    void testUpdate_WithNullId_ThrowsNullPointerException() {
        // Corrigé : attend NullPointerException
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> typeCompteService.update(null, typeCompteDto));
        assertEquals("id ne doit pas être null", exception.getMessage());
    }

    @Test
    void testUpdate_WithNullDto_ThrowsNullPointerException() {
        // Corrigé : attend NullPointerException
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> typeCompteService.update(1, null));
        assertEquals("typeCompteDto ne doit pas être null", exception.getMessage());
    }

    @Test
    void testDelete_Success() {
        when(typeCompteRepository.existsById(1)).thenReturn(true);
        doNothing().when(typeCompteRepository).deleteById(1);

        typeCompteService.delete(1);

        verify(typeCompteRepository).deleteById(1);
    }

    @Test
    void testDelete_WithNullId_ThrowsNullPointerException() {
        // Corrigé : attend NullPointerException
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> typeCompteService.delete(null));
        assertEquals("id ne doit pas être null", exception.getMessage());
    }

    @Test
    void testDelete_NotFound_ThrowsRuntimeException() {
        when(typeCompteRepository.existsById(999)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> typeCompteService.delete(999));
    }
}