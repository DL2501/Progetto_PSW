import { StatoFilm, StatoSerieTV, TipoContenuto } from './enums.model';

export interface Watchable {
  watchableId?: number;
  imdbId: string;
  titolo: string;
  genere: string;
  sinossi?: string;
  copertinaURL?: string;
  dataUscita: string;
  valutazione?: number;
  tipoContenuto: TipoContenuto;
}

export interface ContenutoCatalogo extends Watchable {}

export interface Film extends ContenutoCatalogo {
  tipoContenuto: 'FILM';
  durata?: number;
  regista: string;
  dataAperturaPrenotazioni?: string;
  dataRilascioAnteprima?: string;
  capacitaMassima?: number;
  postiAnteprimaDisponibili?: number;
  stato: StatoFilm;
}

export interface SerieTV extends ContenutoCatalogo {
  tipoContenuto: 'SERIE-TV';
  stagioni?: number;
  numeroEpisodi?: number;
  ideatore: string;
  dataFine?: string;
  stato: StatoSerieTV;
}

export interface Episodio extends Watchable {
  tipoContenuto: 'EPISODIO';
  stagione: number;
  numero: number;
  durata: number;
  regista: string;
  serieTv: SerieTV;
}

export interface SerieTVRequest {
  serie: SerieTV;
  episodi?: Episodio[];
}
