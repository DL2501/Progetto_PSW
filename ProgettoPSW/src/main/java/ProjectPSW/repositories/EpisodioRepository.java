package ProjectPSW.repositories;

import ProjectPSW.entities.Episodio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodioRepository extends WatchableRepository<Episodio> {

    List<Episodio> findBySerieTvIdOrderByStagioneAscNumeroAsc(Integer serieTvId);

    List<Episodio> findBySerieTvIdAndStagioneOrderByNumeroAsc(Integer serieTvId, Integer stagione);

    Optional<Episodio> findBySerieTvIdAndStagioneAndNumero(Integer serieTvId, Integer stagione, Integer numero);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e " +
           "FROM Episodio e " +
           "WHERE e.id = :episodioId")
    Optional<Episodio> findByIdForUpdate(@Param("episodioId") Integer episodioId);


    @Modifying
    @Query("UPDATE Episodio e SET " +
            "e.valutazione = ((COALESCE(e.valutazione, 0.0) * e.numeroVoti) + :nuovoVoto) / (e.numeroVoti + 1), " +
            "e.numeroVoti = e.numeroVoti + 1 " +
            "WHERE e.id = :episodioId")
    int aggiungiVoto(@Param("episodioId") Integer episodioId, @Param("nuovoVoto") Integer nuovoVoto);


    @Modifying
    @Query("UPDATE Episodio e SET " +
            "e.valutazione = CASE WHEN (e.numeroVoti = 1) THEN NULL " +
            "                     ELSE ((e.valutazione * e.numeroVoti) - :votoDaRimuovere) / (e.numeroVoti - 1) END, " +
            "e.numeroVoti = e.numeroVoti - 1 " +
            "WHERE e.id = :episodioId AND e.numeroVoti > 0")
    int rimuoviVoto(@Param("episodioId") Integer episodioId, @Param("votoDaRimuovere") Integer votoDaRimuovere);


    @Modifying
    @Query("DELETE FROM Episodio e " +
            "WHERE e.serieTv.id = :serieTvId")
    void eliminaEpisodiSerieTV(@Param("serieTvId") Integer serieTvId);







}
