package com.collecte_epargne.collecte_epargne.service_test;

import com.collecte_epargne.collecte_epargne.dtos.RoleDto;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.RoleMapper;
import com.collecte_epargne.collecte_epargne.repositories.RoleRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveRole_success() {
        // GIVEN
        RoleDto dto = new RoleDto();
        dto.setCode("ADMIN");
        dto.setNom("Administrateur");
        dto.setDescription("Rôle administrateur");

        Role role = new Role();
        role.setId(1);
        role.setCode("ADMIN");
        role.setNom("Administrateur");
        role.setDescription("Rôle administrateur");

        when(roleRepository.findByNom("Administrateur")).thenReturn(Optional.empty());
        when(roleRepository.findByCode("ADMIN")).thenReturn(Optional.empty());
        when(roleMapper.toEntity(dto)).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(dto);

        // WHEN
        RoleDto result = roleService.save(dto);

        // THEN
        assertNotNull(result);
        verify(roleRepository).findByNom("Administrateur");
        verify(roleRepository).findByCode("ADMIN");
        verify(roleRepository).save(any(Role.class));
        verify(roleMapper).toEntity(dto);
        verify(roleMapper).toDto(role);
    }

    @Test
    void saveRole_nullDto() {
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> roleService.save(null));
    }

    @Test
    void saveRole_emptyNom() {
        // GIVEN
        RoleDto dto = new RoleDto();
        dto.setCode("ADMIN");
        dto.setNom("");

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> roleService.save(dto),
                "Le nom et le code du rôle sont obligatoires");
    }

    @Test
    void saveRole_emptyCode() {
        // GIVEN
        RoleDto dto = new RoleDto();
        dto.setCode("");
        dto.setNom("Administrateur");

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> roleService.save(dto),
                "Le nom et le code du rôle sont obligatoires");
    }

    @Test
    void saveRole_duplicateNom() {
        // GIVEN
        RoleDto dto = new RoleDto();
        dto.setCode("ADMIN");
        dto.setNom("Administrateur");

        Role existingRole = new Role();
        when(roleRepository.findByNom("Administrateur")).thenReturn(Optional.of(existingRole));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.save(dto),
                "Un rôle avec le nom 'Administrateur' existe déjà");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void saveRole_duplicateCode() {
        // GIVEN
        RoleDto dto = new RoleDto();
        dto.setCode("ADMIN");
        dto.setNom("Administrateur");

        Role existingRole = new Role();
        when(roleRepository.findByNom("Administrateur")).thenReturn(Optional.empty());
        when(roleRepository.findByCode("ADMIN")).thenReturn(Optional.of(existingRole));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.save(dto),
                "Un rôle avec le code 'ADMIN' existe déjà");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getAll_success() {
        // GIVEN
        Role role1 = new Role();
        role1.setId(1);
        role1.setCode("ADMIN");
        role1.setNom("Administrateur");

        Role role2 = new Role();
        role2.setId(2);
        role2.setCode("USER");
        role2.setNom("Utilisateur");

        List<Role> roles = Arrays.asList(role1, role2);

        RoleDto dto1 = new RoleDto();
        dto1.setId(1);
        dto1.setCode("ADMIN");
        dto1.setNom("Administrateur");

        RoleDto dto2 = new RoleDto();
        dto2.setId(2);
        dto2.setCode("USER");
        dto2.setNom("Utilisateur");

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toDto(role1)).thenReturn(dto1);
        when(roleMapper.toDto(role2)).thenReturn(dto2);

        // WHEN
        List<RoleDto> result = roleService.getAll();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roleRepository).findAll();
    }

    @Test
    void getById_success() {
        // GIVEN
        Integer id = 1;
        Role role = new Role();
        role.setId(id);
        role.setCode("ADMIN");
        role.setNom("Administrateur");

        RoleDto dto = new RoleDto();
        dto.setId(id);
        dto.setCode("ADMIN");
        dto.setNom("Administrateur");

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(dto);

        // WHEN
        RoleDto result = roleService.getById(id);

        // THEN
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("ADMIN", result.getCode());
        verify(roleRepository).findById(id);
        verify(roleMapper).toDto(role);
    }

    @Test
    void getById_notFound() {
        // GIVEN
        Integer id = 999;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.getById(id),
                "Rôle non trouvé avec l'ID : " + id);
    }

    @Test
    void getById_nullId() {
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> roleService.getById(null));
    }

    @Test
    void updateRole_success() {
        // GIVEN
        Integer id = 1;
        RoleDto dto = new RoleDto();
        dto.setCode("ADMIN");
        dto.setNom("Administrateur Modifié");
        dto.setDescription("Description modifiée");

        Role existingRole = new Role();
        existingRole.setId(id);
        existingRole.setCode("ADMIN");
        existingRole.setNom("Administrateur");
        existingRole.setDescription("Ancienne description");

        Role updatedRole = new Role();
        updatedRole.setId(id);
        updatedRole.setCode("ADMIN");
        updatedRole.setNom("Administrateur Modifié");
        updatedRole.setDescription("Description modifiée");

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));
        when(roleRepository.findByNom("Administrateur Modifié")).thenReturn(Optional.empty());
        when(roleRepository.findByCode("ADMIN")).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);
        when(roleMapper.toDto(updatedRole)).thenReturn(dto);

        // WHEN
        RoleDto result = roleService.update(id, dto);

        // THEN
        assertNotNull(result);
        verify(roleRepository).findById(id);
        verify(roleRepository).save(existingRole);
    }

    @Test
    void updateRole_changeNom_duplicate() {
        // GIVEN
        Integer id = 1;
        RoleDto dto = new RoleDto();
        dto.setNom("Nouveau Nom");

        Role existingRole = new Role();
        existingRole.setId(id);
        existingRole.setNom("Ancien Nom");

        Role duplicateRole = new Role();
        duplicateRole.setId(2);

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));
        when(roleRepository.findByNom("Nouveau Nom")).thenReturn(Optional.of(duplicateRole));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.update(id, dto),
                "Un rôle avec le nom 'Nouveau Nom' existe déjà");
    }

    @Test
    void updateRole_changeCode_duplicate() {
        // GIVEN
        Integer id = 1;
        RoleDto dto = new RoleDto();
        dto.setCode("NEWCODE");
        dto.setNom("Administrateur");

        Role existingRole = new Role();
        existingRole.setId(id);
        existingRole.setCode("OLDCODE");
        existingRole.setNom("Administrateur");

        Role duplicateRole = new Role();
        duplicateRole.setId(2);

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));
        when(roleRepository.findByCode("NEWCODE")).thenReturn(Optional.of(duplicateRole));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.update(id, dto),
                "Un rôle avec le code 'NEWCODE' existe déjà");
    }

    @Test
    void updateRole_notFound() {
        // GIVEN
        Integer id = 999;
        RoleDto dto = new RoleDto();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.update(id, dto),
                "Rôle non trouvé pour la mise à jour : " + id);
    }

    @Test
    void updateRole_nullId() {
        // GIVEN
        RoleDto dto = new RoleDto();

        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> roleService.update(null, dto));
    }

    @Test
    void updateRole_nullDto() {
        // GIVEN
        Integer id = 1;

        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> roleService.update(id, null));
    }

    @Test
    void deleteRole_success() {
        // GIVEN
        Integer id = 1;
        Role role = new Role();
        role.setId(id);
        role.setUtilisateurs(new HashSet<>()); // Aucun utilisateur associé

        when(roleRepository.existsById(id)).thenReturn(true);
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).deleteById(id);

        // WHEN
        roleService.delete(id);

        // THEN
        verify(roleRepository).existsById(id);
        verify(roleRepository).findById(id);
        verify(roleRepository).deleteById(id);
    }

    @Test
    void deleteRole_notFound() {
        // GIVEN
        Integer id = 999;
        when(roleRepository.existsById(id)).thenReturn(false);

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.delete(id),
                "Rôle inexistant : " + id);
        verify(roleRepository, never()).deleteById(id);
    }

    @Test
    void deleteRole_hasUsers() {
        // GIVEN
        Integer id = 1;
        Role role = new Role();
        role.setId(id);

        Utilisateur user1 = new Utilisateur();
        Utilisateur user2 = new Utilisateur();
        Set<Utilisateur> utilisateurs = new HashSet<>(Arrays.asList(user1, user2));
        role.setUtilisateurs(utilisateurs);

        when(roleRepository.existsById(id)).thenReturn(true);
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> roleService.delete(id),
                "Impossible de supprimer le rôle, il est utilisé par");
        verify(roleRepository, never()).deleteById(id);
    }

    @Test
    void deleteRole_nullId() {
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> roleService.delete(null));
    }
}

