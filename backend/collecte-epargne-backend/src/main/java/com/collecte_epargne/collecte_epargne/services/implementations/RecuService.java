package com.collecte_epargne.collecte_epargne.services.implementations;


import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.entities.Recu;
import com.collecte_epargne.collecte_epargne.dtos.RecuDto;
import com.collecte_epargne.collecte_epargne.mappers.RecuMapper;
import com.collecte_epargne.collecte_epargne.repositories.RecuRepository;
import com.collecte_epargne.collecte_epargne.services.interfaces.RecuInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class RecuService implements RecuInterface{

    private final RecuRepository recuRepository;
    private final RecuMapper recuMapper;

    private static final Logger log = LoggerFactory.getLogger(RecuService.class);

    public RecuService(RecuRepository repository, RecuMapper mapper) {
        this.recuRepository = repository;
        this.recuMapper = mapper;
    }
    @Override
    public RecuDto generateRecu(String idtransaction){
        return new RecuDto();
    }
    @Override
    public RecuDto reportRecu(String idRecu){
        return new RecuDto();
    }


//@Override
//    public RecuDto create(RecuDto recuDto) {
//        Recu recu = recuMapper.toEntity(recuDto);
//        recu.setDateGeneration(Instant.now());
//        Recu savedRecu = recuRepository.save(recu);
//        return recuMapper.toDto(savedRecu);
//    }
//
    @Override
    public RecuDto getById(String id) {
        return recuRepository.findById(id)
                .map(recuMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Pas de reference : " + id));
    }

    @Override
    public List<RecuDto> getAll() {
        return recuRepository.findAll()
                .stream()
                .map(recuMapper::toDto)
                .collect(Collectors.toList());
    }
//
//    @Override
//    public RecuDto update(String id, RecuDto dto) {
////        Recu recu = recuRepository.findById(id)
////                .orElseThrow(() -> new RuntimeException("Reçu non trouvé"));
////
////        recu.setFormat(dto.getFormat());
////        recu.setContenu(dto.getContenu());
////        recu.setFichierPath(dto.getFichierPath());
//
//        return dto;//recuMapper.toDto(recuRepository.save(recu));
//    }
//
//    @Override
//    public void delete(String id) {
//        recuRepository.deleteById(id);
//        recuRepository.deleteById(id);
//
//    }

//    @Override
//    public RecuDto getByTransaction(String idTransaction) {
//        Recu recu = recuRepository.findByTransaction_IdTransaction(idTransaction) .orElseThrow(() ->
//                new RuntimeException("Aucun reçu trouvé pour la transaction : " + idTransaction)
//        );
//        return recuMapper.toDto(recu);
//    }

}
