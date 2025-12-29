package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import com.collecte_epargne.collecte_epargne.mappers.ClientMapper;
import com.collecte_epargne.collecte_epargne.mappers.EmployeMapper;
import com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.utils.CodeGenerator;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
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
class EmployeServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private EmployeMapper employeMapper;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private AgenceZoneRepository agenceZoneRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private CodeGenerator codeGenerator;

    @InjectMocks
    private EmployeService employeService;

    private EmployeDto employeDto;
    private Employe employe;
    private Utilisateur utilisateur;
    private AgenceZone agenceZone;
    private Employe superviseur;

    @BeforeEach
    void setUp() {
        // Setup Utilisateur
        utilisateur = new Utilisateur();
        utilisateur.setLogin("user123");

        // Setup AgenceZone
        agenceZone = new AgenceZone();
        agenceZone.setIdAgence(1);
        agenceZone.setNom("Agence Centrale");

        // Setup Superviseur
        superviseur = new Employe();
        superviseur.setIdEmploye(1);
        superviseur.setMatricule("SUP2025001");
        superviseur.setTypeEmploye(TypeEmploye.SUPERVISEUR);

        // Setup Employe
        employe = new Employe();
        employe.setIdEmploye(2);
        employe.setMatricule("COLL2025001");
        employe.setDateEmbauche(LocalDate.now());
        employe.setTypeEmploye(TypeEmploye.COLLECTEUR);
        employe.setCommissionTaux(new BigDecimal("5.00"));
        employe.setUtilisateur(utilisateur);
        employe.setAgenceZone(agenceZone);
        employe.setSuperviseur(superviseur);

        // Setup EmployeDto
        employeDto = new EmployeDto();
        employeDto.setIdEmploye(2);
        employeDto.setMatricule("COLL2025001");
        employeDto.setDateEmbauche(LocalDate.now());
        employeDto.setTypeEmploye(TypeEmploye.COLLECTEUR);
        employeDto.setCommissionTaux(new BigDecimal("5.00"));
        employeDto.setLoginUtilisateur("user123");
        employeDto.setIdAgence(1);
        employeDto.setIdSuperviseur("1");
    }

    @Test
    @SuppressWarnings("null")
    void testSave_Success() {
        // Given
        when(employeRepository.findByMatricule(anyString())).thenReturn(Optional.empty());
        when(codeGenerator.generateMatricule(any(TypeEmploye.class))).thenReturn("COLL2025001");
        when(employeMapper.toEntity(any(EmployeDto.class))).thenReturn(employe);
        when(utilisateurRepository.findById("user123")).thenReturn(Optional.of(utilisateur));
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.of(agenceZone));
        when(employeRepository.findById(1)).thenReturn(Optional.of(superviseur));
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        EmployeDto result = employeService.save(employeDto);

        // Then
        assertNotNull(result);
        assertEquals("COLL2025001", result.getMatricule());
        verify(employeRepository, times(1)).findByMatricule(anyString());
        verify(employeRepository, times(1)).save(any(Employe.class));
    }

    @Test
    void testSave_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> employeService.save(null));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithDuplicateMatricule_ThrowsException() {
        // Given
        when(employeRepository.findByMatricule("COLL2025001")).thenReturn(Optional.of(employe));

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.save(employeDto));
        verify(employeRepository, times(1)).findByMatricule("COLL2025001");
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithNonExistentUtilisateur_ThrowsException() {
        // Given
        when(employeRepository.findByMatricule(anyString())).thenReturn(Optional.empty());
        when(codeGenerator.generateMatricule(any(TypeEmploye.class))).thenReturn("COLL2025001");
        when(employeMapper.toEntity(any(EmployeDto.class))).thenReturn(employe);
        when(utilisateurRepository.findById("user123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.save(employeDto));
        verify(utilisateurRepository, times(1)).findById("user123");
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithNonExistentAgence_ThrowsException() {
        // Given
        when(employeRepository.findByMatricule(anyString())).thenReturn(Optional.empty());
        when(codeGenerator.generateMatricule(any(TypeEmploye.class))).thenReturn("COLL2025001");
        when(employeMapper.toEntity(any(EmployeDto.class))).thenReturn(employe);
        when(utilisateurRepository.findById("user123")).thenReturn(Optional.of(utilisateur));
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.save(employeDto));
        verify(agenceZoneRepository, times(1)).findById(1);
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    @SuppressWarnings("null")
    void testSave_WithInvalidSuperviseur_ThrowsException() {
        // Given
        Employe nonSuperviseur = new Employe();
        nonSuperviseur.setIdEmploye(3);
        nonSuperviseur.setTypeEmploye(TypeEmploye.COLLECTEUR);

        when(employeRepository.findByMatricule(anyString())).thenReturn(Optional.empty());
        when(codeGenerator.generateMatricule(any(TypeEmploye.class))).thenReturn("COLL2025001");
        when(employeMapper.toEntity(any(EmployeDto.class))).thenReturn(employe);
        when(utilisateurRepository.findById("user123")).thenReturn(Optional.of(utilisateur));
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.of(agenceZone));
        when(employeRepository.findById(3)).thenReturn(Optional.of(nonSuperviseur));

        employeDto.setIdSuperviseur("3");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> employeService.save(employeDto));
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    @SuppressWarnings("null")
    void testGetById_Success() {
        // Given
        String matricule = "COLL2025001";
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.of(employe));
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        EmployeDto result = employeService.getById(matricule);

        // Then
        assertNotNull(result);
        assertEquals("COLL2025001", result.getMatricule());
        verify(employeRepository, times(1)).findByMatricule(matricule);
    }

    @Test
    void testGetById_NotFound_ThrowsException() {
        // Given
        String matricule = "COLL9999999";
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.getById(matricule));
        verify(employeRepository, times(1)).findByMatricule(matricule);
    }

    @Test
    void testGetById_WithNullMatricule_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> employeService.getById(null));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() {
        // Given
        String matricule = "COLL2025001";
        employeDto.setCommissionTaux(new BigDecimal("7.50"));
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.of(employe));
        when(utilisateurRepository.findById("user123")).thenReturn(Optional.of(utilisateur));
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.of(agenceZone));
        when(employeRepository.findById(1)).thenReturn(Optional.of(superviseur));
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        EmployeDto result = employeService.update(matricule, employeDto);

        // Then
        assertNotNull(result);
        verify(employeRepository, times(1)).findByMatricule(matricule);
        verify(employeRepository, times(1)).save(any(Employe.class));
    }

    @Test
    void testUpdate_NotFound_ThrowsException() {
        // Given
        String matricule = "COLL9999999";
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.update(matricule, employeDto));
        verify(employeRepository, times(1)).findByMatricule(matricule);
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    void testUpdate_WithNullMatricule_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> employeService.update(null, employeDto));
    }

    @Test
    void testUpdate_WithNullDto_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> employeService.update("COLL2025001", null));
    }

    @Test
    @SuppressWarnings("null")
    void testDelete_Success() {
        // Given
        String matricule = "COLL2025001";
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.of(employe));

        // When
        employeService.delete(matricule);

        // Then
        verify(employeRepository, times(1)).findByMatricule(matricule);
        verify(employeRepository, times(1)).deleteById(2);
    }

    @Test
    void testDelete_NotFound_ThrowsException() {
        // Given
        String matricule = "COLL9999999";
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.delete(matricule));
        verify(employeRepository, times(1)).findByMatricule(matricule);
        verify(employeRepository, never()).deleteById(any(Integer.class));
    }

    @Test
    void testDelete_WithNullMatricule_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> employeService.delete(null));
    }

    @Test
    @SuppressWarnings("null")
    void testGetAll_Success() {
        // Given
        List<Employe> employes = Arrays.asList(employe);
        when(employeRepository.findAll()).thenReturn(employes);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        List<EmployeDto> result = employeService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void testGetSuperviseurs_Success() {
        // Given
        List<Employe> superviseurs = Arrays.asList(superviseur);
        when(employeRepository.findByTypeEmploye(TypeEmploye.SUPERVISEUR)).thenReturn(superviseurs);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        List<EmployeDto> result = employeService.getSuperviseurs();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findByTypeEmploye(TypeEmploye.SUPERVISEUR);
    }

    @Test
    @SuppressWarnings("null")
    void testGetCaissiers_Success() {
        // Given
        Employe caissier = new Employe();
        caissier.setTypeEmploye(TypeEmploye.CAISSIER);
        List<Employe> caissiers = Arrays.asList(caissier);
        when(employeRepository.findByTypeEmploye(TypeEmploye.CAISSIER)).thenReturn(caissiers);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        List<EmployeDto> result = employeService.getCaissiers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findByTypeEmploye(TypeEmploye.CAISSIER);
    }

    @Test
    @SuppressWarnings("null")
    void testGetCollecteurs_Success() {
        // Given
        List<Employe> collecteurs = Arrays.asList(employe);
        when(employeRepository.findByTypeEmploye(TypeEmploye.COLLECTEUR)).thenReturn(collecteurs);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        List<EmployeDto> result = employeService.getCollecteurs();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findByTypeEmploye(TypeEmploye.COLLECTEUR);
    }

    @Test
    @SuppressWarnings("null")
    void testGetCollecteursOrderedByClientCount_Success() {
        // Given
        List<Employe> collecteurs = Arrays.asList(employe);
        when(employeRepository.findCollecteursOrderByClientCountDesc()).thenReturn(collecteurs);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        List<EmployeDto> result = employeService.getCollecteursOrderedByClientCount();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findCollecteursOrderByClientCountDesc();
    }

    @Test
    @SuppressWarnings("null")
    void testGetCollecteursOrderedByTotalClientScore_Success() {
        // Given
        List<Employe> collecteurs = Arrays.asList(employe);
        when(employeRepository.findCollecteursOrderByTotalClientScoreDesc()).thenReturn(collecteurs);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        List<EmployeDto> result = employeService.getCollecteursOrderedByTotalClientScore();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findCollecteursOrderByTotalClientScoreDesc();
    }

    @Test
    @SuppressWarnings("null")
    void testGetCollecteursBySuperviseur_Success() {
        // Given
        String idSuperviseur = "1";
        List<Employe> collecteurs = Arrays.asList(employe);
        when(employeRepository.findById(1)).thenReturn(Optional.of(superviseur));
        when(employeRepository.findBySuperviseurIdEmploye(1)).thenReturn(collecteurs);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(employeDto);

        // When
        List<EmployeDto> result = employeService.getCollecteursBySuperviseur(idSuperviseur);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findById(1);
        verify(employeRepository, times(1)).findBySuperviseurIdEmploye(1);
    }

    @Test
    void testGetCollecteursBySuperviseur_NotFound_ThrowsException() {
        // Given
        String idSuperviseur = "999";
        when(employeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.getCollecteursBySuperviseur(idSuperviseur));
        verify(employeRepository, times(1)).findById(999);
    }

    @Test
    void testGetCollecteursBySuperviseur_NotSuperviseur_ThrowsException() {
        // Given
        String idSuperviseur = "2";
        when(employeRepository.findById(2)).thenReturn(Optional.of(employe));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> employeService.getCollecteursBySuperviseur(idSuperviseur));
        verify(employeRepository, times(1)).findById(2);
    }

    @Test
    void testGetCollecteursBySuperviseur_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> employeService.getCollecteursBySuperviseur(null));
    }

    @Test
    @SuppressWarnings("null")
    void testGetClientsByCollecteur_Success() {
        // Given
        String idCollecteur = "2";
        Client client = new Client();
        client.setNumeroClient(1L);
        List<Client> clients = Arrays.asList(client);
        ClientDto clientDto = new ClientDto();
        when(employeRepository.findById(2)).thenReturn(Optional.of(employe));
        when(clientRepository.findByCollecteurAssigneIdEmploye(2)).thenReturn(clients);
        when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

        // When
        List<ClientDto> result = employeService.getClientsByCollecteur(idCollecteur);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository, times(1)).findById(2);
        verify(clientRepository, times(1)).findByCollecteurAssigneIdEmploye(2);
    }

    @Test
    void testGetClientsByCollecteur_NotFound_ThrowsException() {
        // Given
        String idCollecteur = "999";
        when(employeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> employeService.getClientsByCollecteur(idCollecteur));
        verify(employeRepository, times(1)).findById(999);
    }

    @Test
    void testGetClientsByCollecteur_NotCollecteur_ThrowsException() {
        // Given
        String idCollecteur = "1";
        when(employeRepository.findById(1)).thenReturn(Optional.of(superviseur));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> employeService.getClientsByCollecteur(idCollecteur));
        verify(employeRepository, times(1)).findById(1);
    }

    @Test
    void testGetClientsByCollecteur_WithNullId_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> employeService.getClientsByCollecteur(null));
    }
}

