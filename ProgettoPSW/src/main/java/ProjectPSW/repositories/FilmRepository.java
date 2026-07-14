package ProjectPSW.repositories;

import ProjectPSW.entities.Film;
import jakarta.persistence.LockModeType;
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
            "      (:inUscita IS NULL OR f.inUscita = :inUscita) AND " +
            "      (:prenotabile = false OR f.postiAnteprimaDisponibili > 0)")
    List<Film> ricercaDinamicaFilm(@Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("valutazione") Double valutazione, @Param("durata") Integer durata, @Param("regista") String regista, @Param("inUscita") Boolean inUscita, @Param("prenotabile") boolean prenotabile);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f " +
           "FROM Film f " +
           "WHERE f.imdbId = :imdbId")
    Optional<Film> findByImdbIdForUpdate(@Param("imdbId") String imdbId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Film f SET " +
            "f.valutazione = ((COALESCE(f.valutazione, 0.0) * f.numeroVoti) + :nuovoVoto) / (f.numeroVoti + 1), " +
            "f.numeroVoti = f.numeroVoti + 1 " +
            "WHERE f.id = :filmId AND f.dataUscita <= CURRENT_DATE")
    void aggiungiVoto(@Param("filmId") Integer filmId, @Param("nuovoVoto") Double nuovoVoto);


    List<Film> findByInUscita(Boolean inUscita);

    List<Film> findByDurataGreaterThan(Integer durata);

    List<Film> findByDurataLessThan(Integer durata);

    List<Film> findByDurataBetween(Integer durataMin, Integer durataMax);

    //Da aggiungere: varie combinazioni con gli attributi di classe e superclasse










}
