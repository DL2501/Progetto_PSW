package ProjectPSW.repositories;

import ProjectPSW.entities.Film;
import ProjectPSW.entities.StatoFilm;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends WatchableRepository<Film> {

    @Query("SELECT f " +
            "FROM Film f " +
            "WHERE (:titolo IS NULL OR LOWER(f.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
            "      (:genere IS NULL OR LOWER(f.genere) = LOWER(:genere)) AND " +
            "      (:anno IS NULL OR YEAR(f.dataUscita) = :anno) AND " +
            "      (:valutazione IS NULL OR f.valutazione = :valutazione) AND " +
            "      (:durata IS NULL OR f.durata = :durata) AND " +
            "      (:regista IS NULL OR LOWER(f.regista) LIKE LOWER(CONCAT('%', :regista, '%'))) AND " +
            "      (:stato IS NULL OR f.stato = :stato) AND " +
            "      (:soloFilmPrenotabili = false OR f.postiAnteprimaDisponibili > 0)")
    Page<Film> ricercaDinamicaFilm(@Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("valutazione") Integer valutazione, @Param("durata") Integer durata, @Param("regista") String regista, @Param("stato") StatoFilm stato, @Param("soloFilmPrenotabili") boolean soloFilmPrenotabili, Pageable pageable);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f " +
           "FROM Film f " +
           "WHERE f.id = :filmId")
    Optional<Film> findByIdForUpdate(@Param("filmId") Integer filmId);


    @Modifying
    @Query("UPDATE Film f SET " +
            "f.valutazione = ((COALESCE(f.valutazione, 0.0) * f.numeroVoti) + :nuovoVoto) / (f.numeroVoti + 1), " +
            "f.numeroVoti = f.numeroVoti + 1 " +
            "WHERE f.id = :filmId AND f.stato = StatoFilm.RILASCIATO")
    int aggiungiVoto(@Param("filmId") Integer filmId, @Param("nuovoVoto") Integer nuovoVoto);


    @Modifying
    @Query("UPDATE Film f SET " +
            "f.valutazione = CASE WHEN (f.numeroVoti = 1) THEN NULL " +
            "                     ELSE ((f.valutazione * f.numeroVoti) - :votoDaRimuovere) / (f.numeroVoti - 1) END, " +
            "f.numeroVoti = f.numeroVoti - 1 " +
            "WHERE f.id = :filmId AND f.numeroVoti > 0")
    int rimuoviVoto(@Param("filmId") Integer filmId, @Param("votoDaRimuovere") Integer votoDaRimuovere);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f " +
           "FROM Film f " +
           "WHERE (f.stato = StatoFilm.IN_USCITA AND CURRENT_DATE >= f.dataAperturaPrenotazioni) OR " +
           "      (f.stato = StatoFilm.PRENOTAZIONI_APERTE AND CURRENT_DATE >= f.dataRilascioAnteprima) OR " +
           "      (f.stato = StatoFilm.PRENOTAZIONI_CHIUSE AND CURRENT_DATE >= f.dataUscita)")
    List<Film> trovaFilmDaAggiornare();



    List<Film> findByStato(StatoFilm stato);

    List<Film> findByDurataGreaterThan(Integer durata);

    List<Film> findByDurataLessThan(Integer durata);

    List<Film> findByDurataBetween(Integer durataMin, Integer durataMax);

    //Da aggiungere: varie combinazioni con gli attributi di classe e superclasse










}
