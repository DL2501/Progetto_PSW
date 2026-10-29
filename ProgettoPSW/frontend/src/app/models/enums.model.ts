
export type StatoFilm =
  | 'IN_USCITA'
  | 'PRENOTAZIONI_APERTE'
  | 'PRENOTAZIONI_CHIUSE'
  | 'RILASCIATO';

export type StatoSerieTV =
  | 'IN_USCITA'
  | 'IN_CORSO'
  | 'TERMINATO';

export type StatoVisione =
  | 'VISTO'
  | 'DA_VEDERE'
  | 'IN_VISIONE';

export type TipoContenuto =
  | 'FILM'
  | 'SERIE-TV'
  | 'EPISODIO';
