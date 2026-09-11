package ProjectPSW.services;

import ProjectPSW.entities.*;
import ProjectPSW.repositories.EpisodioRepository;
import ProjectPSW.repositories.SerieTVRepository;
import ProjectPSW.repositories.UtenteRepository;
import ProjectPSW.repositories.VisioneWatchableRepository;
import ProjectPSW.support.Exceptions.*;
import ProjectPSW.support.Utility.ricercaDTO.FiltriRicercaSerieTVDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SerieTVService {

    @Autowired
    private SerieTVRepository serieTVRepository;

    @Autowired
    private EpisodioRepository episodioRepository;

    @Autowired
    private VisioneWatchableRepository visioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<SerieTV> ricercaSezioneSerieTV(FiltriRicercaSerieTVDTO filtri) {
        String titolo = filtri.getTitolo();
        String genere = filtri.getGenere();
        Integer annoUscita = filtri.getAnnoUscita();
        Integer valutazione = filtri.getValutazione();
        Integer episodi = filtri.getNumeroEpisodi();
        String ideatore = filtri.getIdeatore();
        Integer annoConclusione = filtri.getAnnoConclusione();
        StatoSerieTV stato = filtri.getStato();
        int numeroPagina = filtri.getNumeroPagina();
        int elementiPerPagina = filtri.getElementiPerPagina();
        Pageable richiestaPaginazione = PageRequest.of(numeroPagina,elementiPerPagina);
        return serieTVRepository.ricercaDinamicaSerieTV(titolo,genere,annoUscita,valutazione,episodi,ideatore,annoConclusione,stato,richiestaPaginazione);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void aggiungiEpisodio(@NonNull Episodio e, boolean nuovaStagione, boolean ultimoEpisodio) throws EpisodeAlreadyExistException, IllegalTVSeriesStateException, TVSeriesNotFoundException {
        String imdbId = e.getImdbId();
        if (episodioRepository.existsByImdbId(imdbId))
            throw new EpisodeAlreadyExistException("L'episodio che stai provando ad inserire è già presente nel catalogo.");
        Integer serieTvId = e.getSerieTv().getWatchableId();
        SerieTV serie = serieTVRepository.findByIdForUpdate(serieTvId).orElseThrow(() -> new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo."));
        serie.rilascioEpisodio(nuovaStagione,ultimoEpisodio);
        e.setSerieTv(serie);
        episodioRepository.save(e);
    }


    @Transactional(rollbackFor = Exception.class)
    public void aggiungiSerieTVInUscita(@NonNull SerieTV s) throws TVSeriesAlreadyExistException {
        if (s.getStato() != StatoSerieTV.IN_USCITA)
            throw new IllegalArgumentException("Impossibile inserire una serie TV già uscita senza inserire anche i suoi episodi.");
        String imdbId = s.getImdbId();
        if (serieTVRepository.existsByImdbId(imdbId))
            throw new TVSeriesAlreadyExistException("La serie TV che stai provando ad inserire è già presente nel catalogo.");
        serieTVRepository.save(s);
    }


    @Transactional(rollbackFor = Exception.class)
    public void aggiungiSerieTV(@NonNull SerieTV s, @NonNull List<Episodio> episodi) throws TVSeriesAlreadyExistException, EpisodeAlreadyExistException, InvalidEpisodeException {
        if (s.getStato() == StatoSerieTV.IN_USCITA)
            throw new IllegalArgumentException("Impossibile inserire gli episodi di una serie TV che non è ancora uscita.");
        String imdbId = s.getImdbId();
        if (serieTVRepository.existsByImdbId(imdbId))
            throw new TVSeriesAlreadyExistException("La serie TV che stai provando ad inserire è già presente nel catalogo.");
        SerieTV serieManaged = serieTVRepository.save(s);
        for (Episodio e : episodi) {
            if (!(e.getSerieTv().equals(s)))
                throw new InvalidEpisodeException("Impossibile associare episodi che non appartengono alla serie TV che si sta cercando di inserire.");
            String epImdbId = e.getImdbId();
            if (episodioRepository.existsByImdbId(epImdbId))
                throw new EpisodeAlreadyExistException("L'episodio che stai provando ad inserire è già presente nel catalogo.");
            e.setSerieTv(serieManaged);
            episodioRepository.save(e);
        }
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public SerieTV getSerieTV(@NonNull Integer serieTvId) throws TVSeriesNotFoundException {
        Optional<SerieTV> serieTvOpt = serieTVRepository.findById(serieTvId);
        if (serieTvOpt.isEmpty())
            throw new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo.");
        return serieTvOpt.get();
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Episodio getEpisodio(@NonNull Integer episodioId) throws EpisodeNotFoundException {
        Optional<Episodio> episodioOpt = episodioRepository.findById(episodioId);
        if (episodioOpt.isEmpty())
            throw new EpisodeNotFoundException("L'episodio non è presente all'interno del catalogo.");
        return episodioOpt.get();
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<SerieTV> sfogliaSezioneSerieTV(Integer numeroPagina) {
        int paginaVerificata = (numeroPagina != null && numeroPagina >= 0) ? numeroPagina : 0;
        Pageable richiestaPaginazione = PageRequest.of(paginaVerificata,20);
        return serieTVRepository.findAll(richiestaPaginazione);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Episodio> getAllEpisodi(@NonNull Integer serieTvId) throws TVSeriesNotFoundException, UnreleasedTVSeriesException {
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo");
        List<Episodio> episodiSerieTV = episodioRepository.findBySerieTvIdOrderByStagioneAscNumeroAsc(serieTvId);
        if (episodiSerieTV.isEmpty())
            throw new UnreleasedTVSeriesException("Impossibile trovare episodi: La serie TV non è ancora stata rilasciata al momento o potrebbero essere stati rimossi dal catalogo i suoi episodi.");
        return episodiSerieTV;
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Episodio> getEpisodiStagione(@NonNull Integer serieTvId, @NonNull Integer numeroStagione) throws TVSeriesNotFoundException, SeasonNotFoundException {
        if (numeroStagione <= 0)
            throw new IllegalArgumentException("Errore: il valore della stagione deve necessariamente essere maggiore di zero.");
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo");
        List<Episodio> episodiStagioneSerieTV = episodioRepository.findBySerieTvIdAndStagioneOrderByNumeroAsc(serieTvId,numeroStagione);
        if (episodiStagioneSerieTV.isEmpty())
            throw new SeasonNotFoundException("Impossibile trovare la stagione: la serie TV specificata non possiede la stagione " + numeroStagione + ". Questo può accadere sia per motivi regolari oppure perché serie TV ed episodi potrebbero essere stati rimossi dal catalogo.");
        return episodiStagioneSerieTV;
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Episodio getEpisodioByStagioneAndNumero(@NonNull Integer serieTvId, @NonNull Integer numeroStagione, @NonNull Integer numeroEpisodio) throws TVSeriesNotFoundException, EpisodeNotFoundException {
        if (numeroStagione <= 0 || numeroEpisodio <= 0)
            throw new IllegalArgumentException("Errore: il valore della stagione e del numero dell'episodio devono necessariamente essere maggiori di zero.");
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo");
        Optional<Episodio> episodioOpt = episodioRepository.findBySerieTvIdAndStagioneAndNumero(serieTvId,numeroStagione,numeroEpisodio);
        if (episodioOpt.isEmpty())
            throw new EpisodeNotFoundException("Episodio non trovato: nella serie TV specificata non esiste l'episodio " + numeroEpisodio + " della stagione " + numeroStagione + ". Questo può accadere sia per motivi regolari oppure perché serie TV ed episodi potrebbero essere stati rimossi dal catalogo.");
        return episodioOpt.get();
    }


    @Transactional(rollbackFor = Exception.class)
    public void valutaEpisodio(@NonNull Integer utenteId, @NonNull Integer episodioId, @NonNull Integer valutazione) throws EpisodeNotFoundException, WatchableAlreadyRatedException, IllegalRatingException, UserNotFoundException {
        if (valutazione < 0 || valutazione > 5)
            throw new IllegalRatingException("Errore: il voto deve essere necessariamente compreso tra 1.0 e 5.0 stelle.");
        Utente utente = utenteRepository.findByIdForUpdate(utenteId).orElseThrow(() -> new UserNotFoundException("Errore: utente non trovato."));
        Episodio episodio = episodioRepository.findById(episodioId).orElseThrow(() -> new EpisodeNotFoundException("L'episodio non è presente all'interno del catalogo."));
        Optional<Visione_Watchable> vwOpt = visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,episodioId);
        if (vwOpt.isPresent()) {
            Visione_Watchable vw = vwOpt.get();
            if (vw.getValutazione() != null)
                throw new WatchableAlreadyRatedException("Impossibile valutare: uno stesso utente non può valutare lo stesso contenuto più di una volta.");
            vw.impostaValutazione(valutazione);
        }
        else {
            Visione_Watchable nuovoVW = new Visione_Watchable(utente,episodio,valutazione);
            visioneRepository.save(nuovoVW);
        }
        int valutazioniModificate = episodioRepository.aggiungiVoto(utenteId,valutazione);
        if (valutazioniModificate == 0)
            throw new EpisodeNotFoundException("Impossibile valutare: l'episodio selezionato non è presente all'interno del catalogo.");
    }


    @Transactional(rollbackFor = Exception.class)
    public void eliminaEpisodio(@NonNull Integer episodioId) throws EpisodeNotFoundException {
        if (episodioRepository.findByIdForUpdate(episodioId).isEmpty())
            throw new EpisodeNotFoundException("Errore: non puoi eliminare un episodio che non è presente nel catalogo.");
        visioneRepository.eliminaWatchableDalleVideoteche(episodioId);
        episodioRepository.deleteById(episodioId);
    }


    @Transactional(rollbackFor = Exception.class)
    public void eliminaSerieTV(@NonNull Integer serieTvId) throws TVSeriesNotFoundException, EpisodeNotFoundException {
        if (serieTVRepository.findByIdForUpdate(serieTvId).isEmpty())
            throw new TVSeriesNotFoundException("Errore: non puoi eliminare una serie TV che non è presente nel catalogo.");
        visioneRepository.eliminaEpisodiSerieTVDalleVideoteche(serieTvId);
        visioneRepository.eliminaWatchableDalleVideoteche(serieTvId);
        episodioRepository.eliminaEpisodiSerieTV(serieTvId);
        serieTVRepository.deleteById(serieTvId);
    }


    @Transactional(rollbackFor = Exception.class)
    public void aggiornaValutazione(@NonNull Integer serieTvId) throws TVSeriesNotFoundException {
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("Errore: Impossibile aggiornare la valutazione di una serie TV che non è presente nel catalogo.");
        int valutazioniModificate = serieTVRepository.aggiornaValutazione(serieTvId);
        if (valutazioniModificate == 0)
            throw new TVSeriesNotFoundException("Errore: Impossibile aggiornare la valutazione di una serie TV che non è presente nel catalogo.");
    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggiornaValutazioneDiTutteLeSerieTV() {
        serieTVRepository.aggiornaValutazioneMediaDiTutteLeSerieTV();
    }



























































































}
