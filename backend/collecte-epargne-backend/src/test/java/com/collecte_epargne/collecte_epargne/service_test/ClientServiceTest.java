package com.collecte_epargne.collecte_epargne.service_test;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.mappers.ClientMapper;
import com.collecte_epargne.collecte_epargne.repositories.ClientRepository;
import com.collecte_epargne.collecte_epargne.repositories.EmployeRepository;
import com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository;
import com.collecte_epargne.collecte_epargne.services.implementations.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientServiceTest {


        @Mock
        private ClientRepository clientRepository;

        @Mock
        private ClientMapper clientMapper;

        @Mock
        private UtilisateurRepository utilisateurRepository;

        @Mock
        private EmployeRepository employeRepository;

        @InjectMocks
        private ClientService clientService;

        private Client client;
        private ClientDto clientDto;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            client = new Client();
            client.setNumeroClient(1001L);

            clientDto = new ClientDto();
            clientDto.setNumeroClient(1001L);
        }

        @Test
        void save_shouldSaveClient_whenDataIsValid() {
            when(clientRepository.findByNumeroClient(1001L)).thenReturn(Optional.empty());
            when(clientMapper.toEntity(clientDto)).thenReturn(client);
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toDto(client)).thenReturn(clientDto);

            ClientDto result = clientService.save(clientDto);

            assertNotNull(result);
            verify(clientRepository).save(client);
        }

        @Test
        void save_shouldThrowException_whenNumeroClientExists() {
            when(clientRepository.findByNumeroClient(1001L)).thenReturn(Optional.of(client));

            assertThrows(RuntimeException.class, () -> clientService.save(clientDto));
        }

        @Test
        void getAll_shouldReturnListOfClients() {
            when(clientRepository.findAll()).thenReturn(List.of(client));
            when(clientMapper.toDto(client)).thenReturn(clientDto);

            List<ClientDto> result = clientService.getAll();

            assertEquals(1, result.size());
        }

        @Test
        void getById_shouldReturnClient_whenExists() {
            when(clientRepository.findById(1001L)).thenReturn(Optional.of(client));
            when(clientMapper.toDto(client)).thenReturn(clientDto);

            ClientDto result = clientService.getById(1001L);

            assertNotNull(result);
        }

        @Test
        void getById_shouldThrowException_whenNotFound() {
            when(clientRepository.findById(1001L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> clientService.getById(1001L));
        }

        @Test
        void update_shouldUpdateClient() {
            when(clientRepository.findById(1001L)).thenReturn(Optional.of(client));
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toDto(client)).thenReturn(clientDto);

            ClientDto result = clientService.update(1001L, clientDto);

            assertNotNull(result);
        }

        @Test
        void delete_shouldDeleteClient_whenExists() {
            when(clientRepository.findById(1001L)).thenReturn(Optional.of(client));

            clientService.delete(1001L);

            verify(clientRepository).deleteById(1001L);
        }

        @Test
        void delete_shouldThrowException_whenNotExists() {
            when(clientRepository.findById(1001L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> clientService.delete(1001L));
        }

        @Test
        void updateByCodeClient_shouldUpdateClient() {
            when(clientRepository.findByCodeClient("C001")).thenReturn(Optional.of(client));
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toDto(client)).thenReturn(clientDto);

            ClientDto result = clientService.updateByCodeClient("C001", clientDto);

            assertNotNull(result);
        }

        @Test
        void deleteByCodeClient_shouldDeleteClient() {
            when(clientRepository.findByCodeClient("C001")).thenReturn(Optional.of(client));
            when(clientRepository.findById(1001L)).thenReturn(Optional.of(client));

            clientService.deleteByCodeClient("C001");

            verify(clientRepository).deleteById(1001L);
        }
}
