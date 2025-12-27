package com.collecte_epargne.collecte_epargne.repositories;

import com.collecte_epargne.collecte_epargne.entities.Recu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecuRepository extends JpaRepository<Recu, String> {

    // Trouver un reçu par ID
    Optional<Recu> findByIdRecu(String idRecu);

    // Trouver un reçu par ID de transaction
    Optional<Recu> findByTransaction_IdTransaction(String idTransaction);
}
