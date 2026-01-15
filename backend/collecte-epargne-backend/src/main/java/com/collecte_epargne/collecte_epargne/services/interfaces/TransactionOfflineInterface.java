package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.TransactionOfflineDto;
import com.collecte_epargne.collecte_epargne.utils.StatutSynchroOffline;

import java.util.List;

public interface TransactionOfflineInterface {

    TransactionOfflineDto save(TransactionOfflineDto dto);

    TransactionOfflineDto getById(String idOffline);

    List<TransactionOfflineDto> getAll();

    List<TransactionOfflineDto> getByStatutSynchro(StatutSynchroOffline statut);

    List<TransactionOfflineDto> getByEmploye(Integer idEmploye);

    List<TransactionOfflineDto> getByEmployeToday(Integer idEmploye);

    List<TransactionOfflineDto> getByCaissier(Integer idCaissier);

    TransactionOfflineDto valider(String idOffline, Integer idCaissier);

    void rejeter(String idOffline, String motif);

    TransactionOfflineDto markAsSynced(
            String idOffline,
            String idTransactionFinale
    );
}