package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.RecuDto;
import com.collecte_epargne.collecte_epargne.services.implementations.RecuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recu")
@CrossOrigin
public class RecuController {

    private final RecuService recuService;

    public RecuController(RecuService recuService) {
        this.recuService = recuService;
    }

//    @PostMapping
//    public RecuDto save(@RequestBody RecuDto dto) {
//        return recuService.create(dto);
//    }

//    @GetMapping("/{id}")
//    public RecuDto findById(@PathVariable String id) {
//        return recuService.getById(id);
//    }

    @GetMapping
    public List<RecuDto> getAll() {
        return recuService.getAll();
    }

//    @PutMapping("/{id}")
//    public RecuDto update(@PathVariable String id, @RequestBody RecuDto dto) {
//        return recuService.update(id, dto);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable String id) {
//        recuService.delete(id);
//    }

//    @GetMapping("/transaction/{idTransaction}")
//    public RecuDto getByTransaction_IdTransaction(@PathVariable String idTransaction) {
//        return recuService.findByTransaction_IdTransaction(idTransaction);
//    }
}
