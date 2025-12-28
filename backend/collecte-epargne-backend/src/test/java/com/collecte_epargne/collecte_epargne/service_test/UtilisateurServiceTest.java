package com.collecte_epargne.collecte_epargne.service_test;

import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.UtilisateurMapper;
import com.collecte_epargne.collecte_epargne.repositories.RoleRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.UtilisateurService;
import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurMapper utilisateurMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private UtilisateurDto utilisateurDto;
    private Utilisateur utilisateur;
    private Role role;

    @BeforeEach
    void setUp() {
        utilisateurDto = new UtilisateurDto();
        utilisateurDto.setLogin("testuser");
        utilisateurDto.setIdRole(1);
        utilisateurDto.setNom("Test");
        utilisateurDto.setPrenom("User");
        utilisateurDto.setTelephone("0123456789");
        utilisateurDto.setEmail("test@example.com");
        utilisateurDto.setStatut(StatutGenerique.ACTIF);
        utilisateurDto.setDateCreation(Instant.now());

        utilisateur = new Utilisateur();
        utilisateur.setLogin("testuser");
        utilisateur.setNom("Test");
        utilisateur.setPrenom("User");
        utilisateur.setTelephone("0123456789");
        utilisateur.setEmail("test@example.com");
        utilisateur.setStatut(StatutGenerique.ACTIF);
        utilisateur.setDateCreation(Instant.now());

        role = new Role();
        role.setId(1);
        role.setNom("Admin");
    }

    @Test
    void testSave_Success() {
        when(utilisateurRepository.existsById(anyString())).thenReturn(false);
        when(utilisateurMapper.toEntity(any(UtilisateurDto.class))).thenReturn(utilisateur);
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        when(utilisateurMapper.toDto(any(Utilisateur.class))).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.save(utilisateurDto, "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    void testSave_WithNullDto_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> utilisateurService.save(null, "password123"));
        assertEquals("utilisateurDto ne doit pas être null", exception.getMessage());
    }

    @Test
    void testSave_WithNullLogin_ThrowsIllegalArgumentException() {
        utilisateurDto.setLogin(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> utilisateurService.save(utilisateurDto, "password123"));
        assertEquals("Le login et le mot de passe sont obligatoires.", exception.getMessage());
    }

    @Test
    void testSave_WithExistingLogin_ThrowsRuntimeException() {
        when(utilisateurRepository.existsById("testuser")).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> utilisateurService.save(utilisateurDto, "password123"));
        assertEquals("Un utilisateur avec ce login existe déjà.", exception.getMessage());
    }

    @Test
    void testGetAll_Success() {
        List<Utilisateur> utilisateurs = Arrays.asList(utilisateur);
        when(utilisateurRepository.findAll()).thenReturn(utilisateurs);
        when(utilisateurMapper.toDto(any(Utilisateur.class))).thenReturn(utilisateurDto);

        List<UtilisateurDto> result = utilisateurService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetByLogin_Success() {
        when(utilisateurRepository.findById("testuser")).thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.getByLogin("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
    }

    @Test
    void testGetByLogin_WithNullLogin_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> utilisateurService.getByLogin(null));
        assertEquals("login ne doit pas être null", exception.getMessage());
    }

    @Test
    void testGetByEmail_Success() {
        when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toDto(utilisateur)).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.getByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetByEmail_WithNullEmail_ThrowsNullPointerException() {
        // Changé de IllegalArgumentException vers NullPointerException (Option B)
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> utilisateurService.getByEmail(null));
        assertEquals("email ne doit pas être null", exception.getMessage());
    }

    @Test
    void testUpdate_Success() {
        utilisateurDto.setNom("Updated Test");
        when(utilisateurRepository.findById("testuser")).thenReturn(Optional.of(utilisateur));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        when(utilisateurMapper.toDto(any(Utilisateur.class))).thenReturn(utilisateurDto);

        UtilisateurDto result = utilisateurService.update("testuser", utilisateurDto);

        assertNotNull(result);
        assertEquals("Updated Test", result.getNom());
    }

    @Test
    void testUpdate_WithNullLogin_ThrowsNullPointerException() {
        // Changé de IllegalArgumentException vers NullPointerException (Option B)
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> utilisateurService.update(null, utilisateurDto));
        assertEquals("login ne doit pas être null", exception.getMessage());
    }

    @Test
    void testUpdate_WithNullDto_ThrowsNullPointerException() {
        // Changé de IllegalArgumentException vers NullPointerException (Option B)
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> utilisateurService.update("testuser", null));
        assertEquals("utilisateurDto ne doit pas être null", exception.getMessage());
    }

    @Test
    void testUpdatePassword_Success() {
        when(utilisateurRepository.findById("testuser")).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        utilisateurService.updatePassword("testuser", "newpassword123");

        verify(utilisateurRepository, times(1)).save(utilisateur);
        assertEquals("newpassword123", utilisateur.getPassword());
    }

    @Test
    void testUpdatePassword_WithNullLogin_ThrowsNullPointerException() {
        // Changé de IllegalArgumentException vers NullPointerException (Option B)
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> utilisateurService.updatePassword(null, "newpassword123"));
        assertEquals("login ne doit pas être null", exception.getMessage());
    }

    @Test
    void testUpdatePassword_WithInvalidPassword_ThrowsIllegalArgumentException() {
        // On mock l'utilisateur pour qu'il existe et qu'on arrive à la validation du MDP
        when(utilisateurRepository.findById("testuser")).thenReturn(Optional.of(utilisateur));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> utilisateurService.updatePassword("testuser", null));
        assertEquals("Le nouveau mot de passe est invalide.", exception.getMessage());
    }

    @Test
    void testDelete_Success() {
        when(utilisateurRepository.existsById("testuser")).thenReturn(true);
        doNothing().when(utilisateurRepository).deleteById("testuser");

        utilisateurService.delete("testuser");

        verify(utilisateurRepository, times(1)).deleteById("testuser");
    }

    @Test
    void testDelete_WithNullLogin_ThrowsNullPointerException() {
        // Changé de IllegalArgumentException vers NullPointerException (Option B)
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> utilisateurService.delete(null));
        assertEquals("login ne doit pas être null", exception.getMessage());
    }

    @Test
    void testDelete_UserNotFound_ThrowsRuntimeException() {
        when(utilisateurRepository.existsById("nonexistent")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> utilisateurService.delete("nonexistent"));
        assertEquals("Utilisateur inexistant : nonexistent", exception.getMessage());
    }
}