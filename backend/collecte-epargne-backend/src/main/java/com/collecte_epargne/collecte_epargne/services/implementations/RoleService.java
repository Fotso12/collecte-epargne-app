package com.collecte_epargne.collecte_epargne.services.implementations;

import com.collecte_epargne.collecte_epargne.dtos.RoleDto;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.mappers.RoleMapper;
import com.collecte_epargne.collecte_epargne.repositories.RoleRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.RoleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class RoleService implements RoleInterface {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @SuppressWarnings("null")
    public RoleDto save(RoleDto roleDto) {
        log.info("Sauvegarde de rôle avec code: {}", roleDto.getCode());
        Objects.requireNonNull(roleDto, "roleDto ne doit pas être null");
        if (roleDto.getNom() == null || roleDto.getNom().isEmpty() || roleDto.getCode() == null || roleDto.getCode().isEmpty()) {
            throw new IllegalArgumentException("Le nom et le code du rôle sont obligatoires.");
        }

        // 1. Vérification de l'unicité du nom
        if (roleRepository.findByNom(roleDto.getNom()).isPresent()) {
            throw new RuntimeException("Un rôle avec le nom '" + roleDto.getNom() + "' existe déjà.");
        }

        // 2. Vérification de l'unicité du code
        if (roleRepository.findByCode(roleDto.getCode()).isPresent()) {
            throw new RuntimeException("Un rôle avec le code '" + roleDto.getCode() + "' existe déjà.");
        }

        Role roleToSave = roleMapper.toEntity(roleDto);
        Role savedRole = roleRepository.save(roleToSave);
        log.info("Rôle sauvegardé avec succès avec code: {}", savedRole.getCode());
        return roleMapper.toDto(savedRole);
    }

    @Override
    public List<RoleDto> getAll() {
        log.info("Récupération de tous les rôles");
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto getById(Integer id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        log.info("Récupération de rôle avec id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé avec l'ID : " + id));
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDto update(Integer id, RoleDto roleDto) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        Objects.requireNonNull(roleDto, "roleDto ne doit pas être null");
        log.info("Mise à jour de rôle avec id: {}", id);
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé pour la mise à jour : " + id));

        // 1. Vérification de l'unicité du nom (si le nom a changé)
        if (!existingRole.getNom().equals(roleDto.getNom())) {
            if (roleRepository.findByNom(roleDto.getNom()).isPresent()) {
                throw new RuntimeException("Impossible de renommer : Un rôle avec le nom '" + roleDto.getNom() + "' existe déjà.");
            }
            existingRole.setNom(roleDto.getNom());
        }

        // 2. Vérification de l'unicité du code (si le code a changé)
        if (!existingRole.getCode().equals(roleDto.getCode())) {
            if (roleRepository.findByCode(roleDto.getCode()).isPresent()) {
                throw new RuntimeException("Impossible de changer le code : Un rôle avec le code '" + roleDto.getCode() + "' existe déjà.");
            }
            existingRole.setCode(roleDto.getCode());
        }

        existingRole.setDescription(roleDto.getDescription());

        Role updatedRole = roleRepository.save(existingRole);
        log.info("Rôle mis à jour avec succès avec id: {}", updatedRole.getId());
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public void delete(Integer id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        log.info("Suppression de rôle avec id: {}", id);
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Rôle inexistant : " + id);
        }

        // Ajoutez une vérification pour les utilisateurs associés (BONNE PRATIQUE)
        // L'entité Role a Set<Utilisateur> utilisateurs;
        Role role = roleRepository.findById(id).get();
        if (role.getUtilisateurs() != null && !role.getUtilisateurs().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le rôle, il est utilisé par " + role.getUtilisateurs().size() + " utilisateur(s).");
        }

        roleRepository.deleteById(id);
        log.info("Rôle supprimé avec id: {}", id);
    }
}