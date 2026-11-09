package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.ClientSavings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientSavingsRepository extends JpaRepository<ClientSavings, Long> {
    Optional<ClientSavings> findByPhone(String phone);
    Optional<ClientSavings> findByIdentityNumber(String identityNumber);
}

