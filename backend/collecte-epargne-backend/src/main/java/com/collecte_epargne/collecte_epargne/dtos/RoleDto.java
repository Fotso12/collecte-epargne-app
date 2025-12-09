package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO pour Role
 */
@Value
public class RoleDto implements Serializable {
    Integer id;

    @Size(max = 20)
    @NotNull
    String code;

    @Size(max = 50)
    @NotNull
    String nom;

    @Size(max = 255)
    String description;

    // Utilisateurs omis
}