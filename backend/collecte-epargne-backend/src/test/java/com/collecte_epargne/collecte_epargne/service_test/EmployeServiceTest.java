package com.collecte_epargne.collecte_epargne.service_test;

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
import com.collecte_epargne.collecte_epargne.services.implementations.EmployeService;
import com.collecte_epargne.collecte_epargne.utils.CodeGenerator;
import com.collecte_epargne.collecte_epargne.utils.TypeEmploye;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EmployeServiceTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveEmploye_success() {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("EMP001");
        dto.setDateEmbauche(LocalDate.now());
        dto.setTypeEmploye(TypeEmploye.COLLECTEUR);
        dto.setCommissionTaux(BigDecimal.valueOf(5.0));
        dto.setLoginUtilisateur("user1");
        dto.setIdAgence(1);

        Employe employe = new Employe();
        employe.setIdEmploye(1);
        employe.setMatricule("EMP001");
        employe.setTypeEmploye(TypeEmploye.COLLECTEUR);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("user1");
        AgenceZone agenceZone = new AgenceZone();
        agenceZone.setIdAgence(1);

        when(employeRepository.findByMatricule("EMP001")).thenReturn(Optional.empty());
        when(codeGenerator.generateMatricule(TypeEmploye.COLLECTEUR)).thenReturn("EMP001");
        when(employeMapper.toEntity(any(EmployeDto.class))).thenReturn(employe);
        when(utilisateurRepository.findById("user1")).thenReturn(Optional.of(utilisateur));
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.of(agenceZone));
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(dto);

        // WHEN
        EmployeDto result = employeService.save(dto);

        // THEN
        assertNotNull(result);
        verify(employeRepository).save(any(Employe.class));
        verify(employeMapper).toEntity(any(EmployeDto.class));
        verify(employeMapper).toDto(any(Employe.class));
    }

    @Test
    void saveEmploye_duplicateMatricule() {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("EMP001");
        dto.setTypeEmploye(TypeEmploye.COLLECTEUR);

        Employe existingEmploye = new Employe();
        when(employeRepository.findByMatricule("EMP001")).thenReturn(Optional.of(existingEmploye));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> employeService.save(dto),
                "Un employé avec ce matricule existe déjà.");
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    void saveEmploye_nullDto() {
        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> employeService.save(null));
    }

    @Test
    void saveEmploye_utilisateurNotFound() {
        // GIVEN
        EmployeDto dto = new EmployeDto();
        dto.setMatricule("EMP001");
        dto.setLoginUtilisateur("invalid");
        dto.setTypeEmploye(TypeEmploye.COLLECTEUR);

        Employe employe = new Employe();
        when(employeRepository.findByMatricule("EMP001")).thenReturn(Optional.empty());
        when(codeGenerator.generateMatricule(any(TypeEmploye.class))).thenReturn("EMP001");
        when(employeMapper.toEntity(any(EmployeDto.class))).thenReturn(employe);
        when(utilisateurRepository.findById("invalid")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> employeService.save(dto),
                "Utilisateur non trouvé");
    }

    @Test
    void getById_success() {
        // GIVEN
        String matricule = "EMP001";
        Employe employe = new Employe();
        employe.setMatricule(matricule);
        employe.setTypeEmploye(TypeEmploye.COLLECTEUR);

        EmployeDto dto = new EmployeDto();
        dto.setMatricule(matricule);

        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.of(employe));
        when(employeMapper.toDto(employe)).thenReturn(dto);

        // WHEN
        EmployeDto result = employeService.getById(matricule);

        // THEN
        assertNotNull(result);
        assertEquals(matricule, result.getMatricule());
        verify(employeRepository).findByMatricule(matricule);
        verify(employeMapper).toDto(employe);
    }

    @Test
    void getById_notFound() {
        // GIVEN
        String matricule = "INVALID";
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> employeService.getById(matricule),
                "Employé non trouvé");
    }

    @Test
    void updateEmploye_success() {
        // GIVEN
        String matricule = "EMP001";
        EmployeDto dto = new EmployeDto();
        dto.setTypeEmploye(TypeEmploye.SUPERVISEUR);
        dto.setCommissionTaux(BigDecimal.valueOf(10.0));
        dto.setLoginUtilisateur("user1");
        dto.setIdAgence(1);

        Employe existingEmploye = new Employe();
        existingEmploye.setIdEmploye(1);
        existingEmploye.setMatricule(matricule);
        existingEmploye.setTypeEmploye(TypeEmploye.COLLECTEUR);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("user1");
        AgenceZone agenceZone = new AgenceZone();
        agenceZone.setIdAgence(1);

        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.of(existingEmploye));
        when(utilisateurRepository.findById("user1")).thenReturn(Optional.of(utilisateur));
        when(agenceZoneRepository.findById(1)).thenReturn(Optional.of(agenceZone));
        when(employeRepository.save(any(Employe.class))).thenReturn(existingEmploye);
        when(employeMapper.toDto(any(Employe.class))).thenReturn(dto);

        // WHEN
        EmployeDto result = employeService.update(matricule, dto);

        // THEN
        assertNotNull(result);
        verify(employeRepository).save(existingEmploye);
    }

    @Test
    void updateEmploye_notFound() {
        // GIVEN
        String matricule = "INVALID";
        EmployeDto dto = new EmployeDto();
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> employeService.update(matricule, dto),
                "Employé non trouvé");
    }

    @Test
    void deleteEmploye_success() {
        // GIVEN
        String matricule = "EMP001";
        Employe employe = new Employe();
        employe.setIdEmploye(1);
        employe.setMatricule(matricule);

        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.of(employe));
        doNothing().when(employeRepository).deleteById(1);

        // WHEN
        employeService.delete(matricule);

        // THEN
        verify(employeRepository).findByMatricule(matricule);
        verify(employeRepository).deleteById(1);
    }

    @Test
    void deleteEmploye_notFound() {
        // GIVEN
        String matricule = "INVALID";
        when(employeRepository.findByMatricule(matricule)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> employeService.delete(matricule),
                "Employé inexistant");
    }

    @Test
    void getAll_success() {
        // GIVEN
        Employe employe1 = new Employe();
        employe1.setMatricule("EMP001");
        Employe employe2 = new Employe();
        employe2.setMatricule("EMP002");
        List<Employe> employes = Arrays.asList(employe1, employe2);

        EmployeDto dto1 = new EmployeDto();
        dto1.setMatricule("EMP001");
        EmployeDto dto2 = new EmployeDto();
        dto2.setMatricule("EMP002");

        when(employeRepository.findAll()).thenReturn(employes);
        when(employeMapper.toDto(employe1)).thenReturn(dto1);
        when(employeMapper.toDto(employe2)).thenReturn(dto2);

        // WHEN
        List<EmployeDto> result = employeService.getAll();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(employeRepository).findAll();
    }

    @Test
    void getSuperviseurs_success() {
        // GIVEN
        Employe superviseur = new Employe();
        superviseur.setMatricule("SUP001");
        superviseur.setTypeEmploye(TypeEmploye.SUPERVISEUR);
        List<Employe> superviseurs = Arrays.asList(superviseur);

        EmployeDto dto = new EmployeDto();
        dto.setMatricule("SUP001");
        dto.setTypeEmploye(TypeEmploye.SUPERVISEUR);

        when(employeRepository.findByTypeEmploye(TypeEmploye.SUPERVISEUR)).thenReturn(superviseurs);
        when(employeMapper.toDto(superviseur)).thenReturn(dto);

        // WHEN
        List<EmployeDto> result = employeService.getSuperviseurs();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository).findByTypeEmploye(TypeEmploye.SUPERVISEUR);
    }

    @Test
    void getCaissiers_success() {
        // GIVEN
        Employe caissier = new Employe();
        caissier.setMatricule("CAI001");
        caissier.setTypeEmploye(TypeEmploye.CAISSIER);
        List<Employe> caissiers = Arrays.asList(caissier);

        EmployeDto dto = new EmployeDto();
        dto.setMatricule("CAI001");
        dto.setTypeEmploye(TypeEmploye.CAISSIER);

        when(employeRepository.findByTypeEmploye(TypeEmploye.CAISSIER)).thenReturn(caissiers);
        when(employeMapper.toDto(caissier)).thenReturn(dto);

        // WHEN
        List<EmployeDto> result = employeService.getCaissiers();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository).findByTypeEmploye(TypeEmploye.CAISSIER);
    }

    @Test
    void getCollecteurs_success() {
        // GIVEN
        Employe collecteur = new Employe();
        collecteur.setMatricule("COL001");
        collecteur.setTypeEmploye(TypeEmploye.COLLECTEUR);
        List<Employe> collecteurs = Arrays.asList(collecteur);

        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");
        dto.setTypeEmploye(TypeEmploye.COLLECTEUR);

        when(employeRepository.findByTypeEmploye(TypeEmploye.COLLECTEUR)).thenReturn(collecteurs);
        when(employeMapper.toDto(collecteur)).thenReturn(dto);

        // WHEN
        List<EmployeDto> result = employeService.getCollecteurs();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository).findByTypeEmploye(TypeEmploye.COLLECTEUR);
    }

    @Test
    void getCollecteursBySuperviseur_success() {
        // GIVEN
        String idSuperviseur = "1";
        Integer idSuperviseurInt = 1;
        Employe superviseur = new Employe();
        superviseur.setIdEmploye(idSuperviseurInt);
        superviseur.setTypeEmploye(TypeEmploye.SUPERVISEUR);

        Employe collecteur = new Employe();
        collecteur.setMatricule("COL001");
        collecteur.setTypeEmploye(TypeEmploye.COLLECTEUR);
        List<Employe> collecteurs = Arrays.asList(collecteur);

        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");

        when(employeRepository.findById(idSuperviseurInt)).thenReturn(Optional.of(superviseur));
        when(employeRepository.findBySuperviseurIdEmploye(idSuperviseurInt)).thenReturn(collecteurs);
        when(employeMapper.toDto(collecteur)).thenReturn(dto);

        // WHEN
        List<EmployeDto> result = employeService.getCollecteursBySuperviseur(idSuperviseur);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository).findById(idSuperviseurInt);
        verify(employeRepository).findBySuperviseurIdEmploye(idSuperviseurInt);
    }

    @Test
    void getCollecteursBySuperviseur_superviseurNotFound() {
        // GIVEN
        String idSuperviseur = "1";
        Integer idSuperviseurInt = 1;
        when(employeRepository.findById(idSuperviseurInt)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> employeService.getCollecteursBySuperviseur(idSuperviseur),
                "Superviseur non trouvé");
    }

    @Test
    void getCollecteursBySuperviseur_notASuperviseur() {
        // GIVEN
        String idSuperviseur = "1";
        Integer idSuperviseurInt = 1;
        Employe employe = new Employe();
        employe.setIdEmploye(idSuperviseurInt);
        employe.setTypeEmploye(TypeEmploye.COLLECTEUR); // Pas un superviseur

        when(employeRepository.findById(idSuperviseurInt)).thenReturn(Optional.of(employe));

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> employeService.getCollecteursBySuperviseur(idSuperviseur),
                "n'est pas un superviseur");
    }

    @Test
    void getClientsByCollecteur_success() {
        // GIVEN
        String idCollecteur = "1";
        Integer idCollecteurInt = 1;
        Employe collecteur = new Employe();
        collecteur.setIdEmploye(idCollecteurInt);
        collecteur.setTypeEmploye(TypeEmploye.COLLECTEUR);

        Client client = new Client();
        client.setNumeroClient("1");
        List<Client> clients = Arrays.asList(client);

        ClientDto clientDto = new ClientDto();
        clientDto.setNumeroClient("1");

        when(employeRepository.findById(idCollecteurInt)).thenReturn(Optional.of(collecteur));
        when(clientRepository.findByCollecteurAssigneIdEmploye(idCollecteurInt)).thenReturn(clients);
        when(clientMapper.toDto(client)).thenReturn(clientDto);

        // WHEN
        List<ClientDto> result = employeService.getClientsByCollecteur(idCollecteur);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository).findById(idCollecteurInt);
        verify(clientRepository).findByCollecteurAssigneIdEmploye(idCollecteurInt);
    }

    @Test
    void getClientsByCollecteur_collecteurNotFound() {
        // GIVEN
        String idCollecteur = "1";
        Integer idCollecteurInt = 1;
        when(employeRepository.findById(idCollecteurInt)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> employeService.getClientsByCollecteur(idCollecteur),
                "Collecteur non trouvé");
    }

    @Test
    void getClientsByCollecteur_notACollecteur() {
        // GIVEN
        String idCollecteur = "1";
        Integer idCollecteurInt = 1;
        Employe employe = new Employe();
        employe.setIdEmploye(idCollecteurInt);
        employe.setTypeEmploye(TypeEmploye.SUPERVISEUR); // Pas un collecteur

        when(employeRepository.findById(idCollecteurInt)).thenReturn(Optional.of(employe));

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> employeService.getClientsByCollecteur(idCollecteur),
                "n'est pas un collecteur");
    }

    @Test
    void getCollecteursOrderedByClientCount_success() {
        // GIVEN
        Employe collecteur = new Employe();
        collecteur.setMatricule("COL001");
        List<Employe> collecteurs = Arrays.asList(collecteur);

        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");

        when(employeRepository.findCollecteursOrderByClientCountDesc()).thenReturn(collecteurs);
        when(employeMapper.toDto(collecteur)).thenReturn(dto);

        // WHEN
        List<EmployeDto> result = employeService.getCollecteursOrderedByClientCount();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository).findCollecteursOrderByClientCountDesc();
    }

    @Test
    void getCollecteursOrderedByTotalClientScore_success() {
        // GIVEN
        Employe collecteur = new Employe();
        collecteur.setMatricule("COL001");
        List<Employe> collecteurs = Arrays.asList(collecteur);

        EmployeDto dto = new EmployeDto();
        dto.setMatricule("COL001");

        when(employeRepository.findCollecteursOrderByTotalClientScoreDesc()).thenReturn(collecteurs);
        when(employeMapper.toDto(collecteur)).thenReturn(dto);

        // WHEN
        List<EmployeDto> result = employeService.getCollecteursOrderedByTotalClientScore();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeRepository).findCollecteursOrderByTotalClientScoreDesc();
    }
}

