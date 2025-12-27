package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;

import java.util.List;

public interface TransactionInterface {

    TransactionDto create(TransactionDto transactionDto);

    TransactionDto getById(String idTransaction);

    List<TransactionDto> getAll();

    TransactionDto validerParCaissier(String idTransaction, String idCaissier);

    TransactionDto validerParSuperviseur(String idTransaction, String idSuperviseur);

    void rejeterTransaction(String idTransaction, String motifRejet);
}