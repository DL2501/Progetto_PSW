package ProjectPSW.controllers;

import ProjectPSW.entities.ContenutoCatalogo;
import ProjectPSW.services.CatalogoService;
import ProjectPSW.support.exceptions.CatalogContentNotFoundException;
import ProjectPSW.support.utility.DTO.ricerca.FiltriRicercaCatalogo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalogo")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;


    @PostMapping("/ricerca")
    public ResponseEntity<?> ricercaCatalogo(@RequestBody FiltriRicercaCatalogo filtri) {
        try {
            Page<ContenutoCatalogo> risultatiRicerca = catalogoService.ricercaCatalogoGenerale(filtri);
            return new ResponseEntity<>(risultatiRicerca, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la ricerca.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{contenutoId}")
    public ResponseEntity<?> getDettaglioContenuto(@PathVariable Integer contenutoCatalogoId) {
        try {
            ContenutoCatalogo contenuto = catalogoService.getDettaglioContenuto(contenutoCatalogoId);
            return new ResponseEntity<>(contenuto, HttpStatus.OK);
        } catch (CatalogContentNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore nella visualizzazione del contenuto.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<?> sfogliaCatalogo(@RequestParam(defaultValue = "0") Integer numeroPagina) {
        try {
            Page<ContenutoCatalogo> paginaCatalogo = catalogoService.sfogliaCatalogo(numeroPagina);
            return new ResponseEntity<>(paginaCatalogo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore deurante la visualizzazione del catalogo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






}
