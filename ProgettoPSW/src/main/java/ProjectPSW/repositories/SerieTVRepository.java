package ProjectPSW.repositories;

import ProjectPSW.entities.SerieTV;
import ProjectPSW.entities.StatoSerieTV;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerieTVRepository extends WatchableRepository<SerieTV> {

    @Query("SELECT s " +
            "FROM SerieTV s " +
            "WHERE (:titolo IS NULL OR LOWER(s.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
            "      (:genere IS NULL OR LOWER(s.genere) = LOWER(:genere)) AND " +
            "      (:anno IS NULL OR YEAR(s.dataUscita) = :anno) AND " +
            "      (:valutazione IS NULL OR s.valutazione = :valutazione) AND " +
            "      (:episodi IS NULL OR s.numeroEpisodi = :episodi) AND " +
            "      (:ideatore IS NULL OR LOWER(s.ideatore) LIKE LOWER(CONCAT('%', :ideatore, '%'))) AND " +
            "      (:annoConclusione IS NULL OR YEAR(s.dataFine) = :annoConclusione) AND " +
            "      (:stato IS NULL OR s.stato = :stato)")
    List<SerieTV> ricercaDinamicaSerieTV(@Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("valutazione") Double valutazione, @Param("episodi") Integer episodi, @Param("ideatore") String ideatore, @Param("annoConclusione") Integer annoConclusione, @Param("stato") StatoSerieTV stato);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE SerieTV s SET s.valutazione = " +
           "(SELECT AVG(e.valutazione) " +
            "FROM Episodio e " +
            "WHERE e.serieTv.id = s.id) " +
            "WHERE s.id = :serieTvId")
    void aggiornaValutazione(@Param("serieTvId") Integer serieTvId);












}
