package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.RoleDto;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.RoleMapper;
import com.collecte_epargne.collecte_epargne.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private RoleDto roleDto;
    private Role role;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto();
        roleDto.setId(1);
        roleDto.setCode("ADMIN");
        roleDto.setNom("Administrateur");
        roleDto.setDescription("Rôle administrateur avec tous les droits");

        role = new Role();
        role.setId(1);
        role.setCode("ADMIN");
        role.setNom("Administrateur");
        role.setDescription("Rôle administrateur avec tous les droits");
        role.setUtilisateurs(new HashSet<>());
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() {
        // Given
        when(roleRepository.findByNom(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(roleMapper.toEntity(any(RoleDto.class))).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        // When
        RoleDto result = roleService.save(roleDto);

        // Then
        assertNotNull(result);
        assertEquals("ADMIN", result.getCode());
        assertEquals("Administrateur", result.getNom());
        verify(roleRepository, times(1)).findByNom("Administrateur");
        verify(roleRepository, times(1)).findByCode("ADMIN");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testSave_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> roleService.save(null));
    }

    @Test
    void testSave_WithNullNom_ThrowsException() {
        // Given
        roleDto.setNom(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> roleService.save(roleDto));
    }

    @Test
    void testSave_WithEmptyNom_ThrowsException() {
        // Given
        roleDto.setNom("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> roleService.save(roleDto));
    }

    @Test
    void testSave_WithNullCode_ThrowsException() {
        // Given
        roleDto.setCode(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> roleService.save(roleDto));
    }

    @Test
    void testSave_WithEmptyCode_ThrowsException() {
        // Given
        roleDto.setCode("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> roleService.save(roleDto));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithDuplicateNom_ThrowsException() {
        // Given
        when(roleRepository.findByNom("Administrateur")).thenReturn(Optional.of(role));

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.save(roleDto));
        verify(roleRepository, times(1)).findByNom("Administrateur");
        verify(roleRepository, never()).findByCode(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithDuplicateCode_ThrowsException() {
        // Given
        when(roleRepository.findByNom(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByCode("ADMIN")).thenReturn(Optional.of(role));

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.save(roleDto));
        verify(roleRepository, times(1)).findByNom("Administrateur");
        verify(roleRepository, times(1)).findByCode("ADMIN");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @SuppressWarnings("null")
    void testGetAll_Success() {
        // Given
        List<Role> roles = Arrays.asList(role);
        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        // When
        List<RoleDto> result = roleService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void testGetById_Success() {
        // Given
        Integer id = 1;
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        // When
        RoleDto result = roleService.getById(id);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("ADMIN", result.getCode());
        verify(roleRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_NotFound_ThrowsException() {
        // Given
        Integer id = 999;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.getById(id));
        verify(roleRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> roleService.getById(null));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() {
        // Given
        Integer id = 1;
        roleDto.setNom("Administrateur Modifié");
        roleDto.setDescription("Nouvelle description");
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleRepository.findByNom("Administrateur Modifié")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        // When
        RoleDto result = roleService.update(id, roleDto);

        // Then
        assertNotNull(result);
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_WithSameNom_Success() {
        // Given
        Integer id = 1;
        roleDto.setNom("Administrateur"); // Same name
        roleDto.setDescription("Nouvelle description");
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        // When
        RoleDto result = roleService.update(id, roleDto);

        // Then
        assertNotNull(result);
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, never()).findByNom(anyString());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_WithDuplicateNom_ThrowsException() {
        // Given
        Integer id = 1;
        Role existingRole = new Role();
        existingRole.setId(2);
        existingRole.setNom("Autre Rôle");
        roleDto.setNom("Autre Rôle");
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleRepository.findByNom("Autre Rôle")).thenReturn(Optional.of(existingRole));

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.update(id, roleDto));
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, times(1)).findByNom("Autre Rôle");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_WithDuplicateCode_ThrowsException() {
        // Given
        Integer id = 1;
        Role existingRole = new Role();
        existingRole.setId(2);
        existingRole.setCode("AUTRE");
        roleDto.setCode("AUTRE");
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleRepository.findByCode("AUTRE")).thenReturn(Optional.of(existingRole));

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.update(id, roleDto));
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, times(1)).findByCode("AUTRE");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdate_NotFound_ThrowsException() {
        // Given
        Integer id = 999;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.update(id, roleDto));
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdate_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> roleService.update(null, roleDto));
    }

    @Test
    void testUpdate_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> roleService.update(1, null));
    }

    @Test
    @SuppressWarnings("null")
    void testDelete_Success() {
        // Given
        Integer id = 1;
        when(roleRepository.existsById(id)).thenReturn(true);
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        // When
        roleService.delete(id);

        // Then
        verify(roleRepository, times(1)).existsById(id);
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NotFound_ThrowsException() {
        // Given
        Integer id = 999;
        when(roleRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.delete(id));
        verify(roleRepository, times(1)).existsById(id);
        verify(roleRepository, never()).deleteById(id);
    }

    @Test
    @SuppressWarnings("null")
    void testDelete_WithUtilisateurs_ThrowsException() {
        // Given
        Integer id = 1;
        Utilisateur utilisateur = new Utilisateur();
        Set<Utilisateur> utilisateurs = new HashSet<>();
        utilisateurs.add(utilisateur);
        role.setUtilisateurs(utilisateurs);

        when(roleRepository.existsById(id)).thenReturn(true);
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        // When & Then
        assertThrows(RuntimeException.class, () -> roleService.delete(id));
        verify(roleRepository, times(1)).existsById(id);
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, never()).deleteById(id);
    }

    @Test
    void testDelete_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> roleService.delete(null));
    }
}

