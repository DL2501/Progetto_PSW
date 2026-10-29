import { Film } from './watchable.model';
import { Utente } from './utente.model';

export interface Prenotazione {
  prenotazioneId?: number;
  codiceBiglietto: string;
  dataPrenotazione: string;
  prezzoPagato: number;
  valida: boolean;
  utente: Utente;
  film: Film;
}
