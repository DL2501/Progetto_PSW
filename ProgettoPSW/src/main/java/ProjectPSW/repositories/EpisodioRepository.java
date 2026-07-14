package ProjectPSW.repositories;

import ProjectPSW.entities.Episodio;
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

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Episodio e SET " +
            "e.valutazione = ((COALESCE(e.valutazione, 0.0) * e.numeroVoti) + :nuovoVoto) / (e.numeroVoti + 1), " +
            "e.numeroVoti = e.numeroVoti + 1 " +
            "WHERE e.id = :episodioId AND e.dataUscita <= CURRENT_DATE")
    void aggiungiVoto(@Param("episodioId") Integer episodioId, @Param("nuovoVoto") Double nuovoVoto);



}
