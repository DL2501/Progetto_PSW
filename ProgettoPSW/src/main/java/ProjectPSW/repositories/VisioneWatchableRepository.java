package ProjectPSW.repositories;

import ProjectPSW.entities.StatoSerieTV;
import ProjectPSW.entities.StatoVisione;
import ProjectPSW.entities.Visione_Watchable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisioneWatchableRepository extends JpaRepository<Visione_Watchable,Integer> {

    List<Visione_Watchable> findByUtenteId(Integer utenteId);

    @Query("SELECT v " +
           "FROM Visione_Watchable v " +
           "JOIN v.watchable w " +
           "WHERE v.utente.id = :utenteId AND " +
           "      (TYPE(w) IN (Film,SerieTV)) AND " +
           "      (:titolo IS NULL OR LOWER(w.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
           "      (:genere IS NULL OR LOWER(w.genere) = LOWER(:genere)) AND " +
           "      (:anno IS NULL OR YEAR(w.dataUscita) = :anno) AND " +
           "      (:statoVisione IS NULL OR v.statoVisione = :statoVisione) AND " +
           "      (:valutazione IS NULL OR v.valutazione = :valutazione)")
    List<Visione_Watchable> ricercaDinamicaBaseVP(@Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("statoVisione") StatoVisione statoVisione, @Param("valutazione") Integer valutazione);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "JOIN TREAT(v.watchable AS Film) f " +
            "WHERE (v.utente.id = :utenteId) AND " +
            "      (:titolo IS NULL OR LOWER(f.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
            "      (:genere IS NULL OR LOWER(f.genere) = LOWER(:genere)) AND " +
            "      (:anno IS NULL OR YEAR(f.dataUscita) = :anno) AND " +
            "      (:durata IS NULL OR f.durata = :durata) AND " +
            "      (:regista IS NULL OR LOWER(f.regista) LIKE LOWER(CONCAT('%', :regista, '%'))) AND " +
            "      (:statoVisione IS NULL OR v.statoVisione = :statoVisione) AND " +
            "      (:valutazione IS NULL OR v.valutazione = :valutazione)")
    List<Visione_Watchable> ricercaDinamicaFilmVP(@Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("durata") Integer durata, @Param("regista") String regista, @Param("statoVisione") StatoVisione statoVisione, @Param("valutazione") Integer valutazione);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "JOIN TREAT(v.watchable AS SerieTV) s " +
            "WHERE (v.utente.id = :utenteId) AND " +
            "      (:titolo IS NULL OR LOWER(s.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
            "      (:genere IS NULL OR LOWER(s.genere) = LOWER(:genere)) AND " +
            "      (:anno IS NULL OR YEAR(s.dataUscita) = :anno) AND " +
            "      (:episodi IS NULL OR s.numeroEpisodi = :episodi) AND " +
            "      (:ideatore IS NULL OR LOWER(s.ideatore) LIKE LOWER(CONCAT('%', :ideatore, '%'))) AND " +
            "      (:annoConclusione IS NULL OR YEAR(s.dataFine) = :annoConclusione) AND " +
            "      (:stato IS NULL OR s.stato = :stato) AND " +
            "      (:statoVisione IS NULL OR v.statoVisione = :statoVisione) AND " +
            "      (:valutazione IS NULL OR v.valutazione = :valutazione)")
    List<Visione_Watchable> ricercaDinamicaSerieTVVP(@Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("episodi") Integer episodi, @Param("ideatore") String ideatore, @Param("annoConclusione") Integer annoConclusione, @Param("stato") StatoSerieTV stato, @Param("statoVisione") StatoVisione statoVisione, @Param("valutazione") Integer valutazione);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE Visione_Watchable v SET v.valutazione = " +
           "(SELECT AVG(v2.valutazione) " +
            "FROM Visione_Watchable v2 " +
            "JOIN TREAT(v2.watchable AS Episodio) e " +
            "WHERE (v2.utente.id = :utenteId) AND " +
            "      (e.serieTv.id = :serieTvId)) " +
            "WHERE v.utente.id = :utenteId AND v.watchable.id = :serieTvId")
    void aggiornaValutazioneByMediaSerieTV(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE Visione_Watchable v SET v.valutazione = :valutazione " +
           "WHERE v.utente.id = :utenteId AND v.watchable.id = :watchableId")
    void impostaValutazione(@Param("utenteId") Integer utenteId, @Param("watchableId") Integer watchableId, @Param("valutazione") Integer valutazione);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE Visione_Watchable v SET v.statoVisione = :statoVisione " +
            "WHERE v.utente.id = :utenteId AND v.watchable.id = :watchableId")
    void impostaStatoVisione(@Param("utenteId") Integer utenteId, @Param("watchableId") Integer watchableId, @Param("statoVisione") StatoVisione statoVisione);


    @Query("SELECT v " +
           "FROM Visione_Watchable v " +
           "JOIN TREAT(v.watchable AS Episodio) e " +
           "WHERE v.utente.id = :utenteId AND e.serieTv.id = :serieTvId " +
           "ORDER BY e.stagione ASC, e.numero ASC")
    List<Visione_Watchable> findAllEpisodiSerieTV(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "JOIN TREAT(v.watchable AS Episodio) e " +
            "WHERE v.utente.id = :utenteId AND e.serieTv.id = :serieTvId AND e.stagione = :stagione " +
            "ORDER BY e.numero ASC")
    List<Visione_Watchable> findEpisodiBySerieTVAndStagione(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId, @Param("stagione") Integer stagione);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "JOIN TREAT(v.watchable AS Episodio) e " +
            "WHERE (v.utente.id = :utenteId AND e.serieTv.id = :serieTvId) AND " +
            "      (e.stagione = :stagione AND e.numero = :numeroEpisodio)")
    List<Visione_Watchable> findEpisodioBySerieTVAndStagioneAndNumero(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId, @Param("stagione") Integer stagione, @Param("numeroEpisodio") Integer numeroEpisodio);


    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Visione_Watchable v " +
           "WHERE v.utente.id = :utenteId")
    void eliminaVideotecaUtente(@Param("utenteId") Integer utenteId);






























































}
