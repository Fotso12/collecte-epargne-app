package com.collecte_epargne.collecte_epargne.services.interfaces;

import com.collecte_epargne.collecte_epargne.dtos.RecuDto;
import com.collecte_epargne.collecte_epargne.dtos.RoleDto;

import java.util.List;


public interface RecuInterface {

    RecuDto generateRecu(String idtransaction);
    RecuDto reportRecu(String idRecu);

    List<RecuDto> getAll();

    RecuDto getById(String id);


}
