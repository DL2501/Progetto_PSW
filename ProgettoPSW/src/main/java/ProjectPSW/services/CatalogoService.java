package ProjectPSW.services;

import ProjectPSW.entities.ContenutoCatalogo;
import ProjectPSW.repositories.ContenutoCatalogoRepository;
import ProjectPSW.support.Exceptions.CatalogContentNotFoundException;
import ProjectPSW.support.Utility.ricercaDTO.FiltriRicercaCatalogoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CatalogoService {

    @Autowired
    private ContenutoCatalogoRepository catalogoRepository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<ContenutoCatalogo> ricercaCatalogoGenerale(FiltriRicercaCatalogoDTO filtri) {
        String titolo = filtri.getTitolo();
        String genere = filtri.getGenere();
        Integer annoUscita = filtri.getAnnoUscita();
        Integer valutazione = filtri.getValutazione();
        int numeroPagina = filtri.getNumeroPagina();
        int elementiPerPagina = filtri.getElementiPerPagina();
        Pageable richiestaPaginazione = PageRequest.of(numeroPagina,elementiPerPagina);
        return catalogoRepository.ricercaDinamicaBase(titolo,genere,annoUscita,valutazione,richiestaPaginazione);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public ContenutoCatalogo getDettaglioContenuto(Integer contenutoCatalogoId) throws CatalogContentNotFoundException {
        Optional<ContenutoCatalogo> contenutoOpt = catalogoRepository.findById(contenutoCatalogoId);
        if (contenutoOpt.isEmpty())
            throw new CatalogContentNotFoundException("Il contenuto richiesto non è presente nel catalogo");
        return contenutoOpt.get();
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<ContenutoCatalogo> sfogliaCatalogo(Integer numeroPagina) {
        int paginaVerificata = (numeroPagina != null && numeroPagina >= 0) ? numeroPagina : 0;
        Pageable richiestaPaginazione = PageRequest.of(paginaVerificata,20);
        return catalogoRepository.findAll(richiestaPaginazione);
    }

























}
