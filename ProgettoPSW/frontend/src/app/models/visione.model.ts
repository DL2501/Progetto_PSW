import { StatoVisione } from './enums.model';
import { Utente } from './utente.model';
import {Watchable} from './watchable.model';

export interface VisioneWatchable {
  visioneWatchableId?: number;
  statoVisione?: StatoVisione;
  valutazione?: number;
  inVideoteca: boolean;
  utente: Utente;
  watchable: Watchable;
}
