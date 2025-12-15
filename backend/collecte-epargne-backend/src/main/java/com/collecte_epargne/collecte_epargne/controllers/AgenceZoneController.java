    package com.collecte_epargne.collecte_epargne.controllers;


    import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;
    import com.collecte_epargne.collecte_epargne.services.implementations.AgenceZoneService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.server.ResponseStatusException;

    import java.util.List;

    @RestController
    @RequestMapping("api/AgenceZone")
    public class AgenceZoneController {

        private AgenceZoneService agenceZoneService;

        public AgenceZoneController(AgenceZoneService agenceZoneService) {
            this.agenceZoneService = agenceZoneService;
        }

        @PostMapping
        public ResponseEntity<?> save(@RequestBody AgenceZoneDto agenceZoneDto) {

            try{
                return new ResponseEntity<>(agenceZoneService.save(agenceZoneDto), HttpStatus.CREATED);
            }catch(Exception e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        @GetMapping()
        public ResponseEntity<List<AgenceZoneDto>> getAll() {

            try{
                return new  ResponseEntity<>(agenceZoneService.getAll(), HttpStatus.OK);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        @PutMapping("/{idAgence}")
        public ResponseEntity<?> update(@PathVariable Integer idAgence, @RequestBody AgenceZoneDto agenceZoneDto) {
            try {
                return  new ResponseEntity<>(agenceZoneService.update(idAgence, agenceZoneDto), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        @DeleteMapping("/{idAgence}")
        public ResponseEntity<?> delete(@PathVariable Integer idAgence) {
            try {
                agenceZoneService.delete(idAgence);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        @GetMapping("/{idAgence}")
        public ResponseEntity<?> Show(@PathVariable Integer idAgence) {
            try{
                return new ResponseEntity<>(agenceZoneService.getById(idAgence), HttpStatus.OK);
            }catch(Exception e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

        }


    }
