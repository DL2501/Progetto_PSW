import { StatoFilm, StatoSerieTV, StatoVisione } from './enums.model';

export interface FiltriRicercaCatalogo {
  titolo?: string | null;
  genere?: string | null;
  annoUscita?: number | null;
  valutazione?: number | null;
}

export interface FiltriRicercaFilm extends FiltriRicercaCatalogo {
  durata?: number | null;
  regista?: string | null;
  stato?: StatoFilm | null;
  soloFilmPrenotabili?: boolean;
}

export interface FiltriRicercaSerieTV extends FiltriRicercaCatalogo {
  numeroEpisodi?: number | null;
  ideatore?: string | null;
  annoConclusione?: number | null;
  stato?: StatoSerieTV | null;
}

export interface FiltriRicercaCatalogoVideoteca extends FiltriRicercaCatalogo {
  statoVisione?: StatoVisione | null;
}

export interface FiltriRicercaFilmVideoteca extends FiltriRicercaCatalogoVideoteca {
  durata?: number | null;
  regista?: string | null;
}

export interface FiltriRicercaSerieTVVideoteca extends FiltriRicercaCatalogoVideoteca {
  numeroEpisodi?: number | null;
  ideatore?: string | null;
  annoConclusione?: number | null;
  stato?: StatoSerieTV | null;
}
