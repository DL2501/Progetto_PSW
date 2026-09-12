package ProjectPSW.repositories;

import ProjectPSW.entities.StatoSerieTV;
import ProjectPSW.entities.StatoVisione;
import ProjectPSW.entities.Visione_Watchable;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisioneWatchableRepository extends JpaRepository<Visione_Watchable,Integer> {

    List<Visione_Watchable> findByUtenteIdAndInVideotecaTrue(Integer utenteId);

    List<Visione_Watchable> findByUtenteIdAndInVideotecaFalse(Integer utenteId);

    Optional<Visione_Watchable> findByUtenteIdAndWatchableId(Integer utenteId, Integer watchableId);

    Optional<Visione_Watchable> findByUtenteIdAndWatchableIdAndInVideotecaTrue(Integer utenteId, Integer watchableId);

    boolean existsByUtenteIdAndWatchableId(Integer utenteId, Integer watchableId);

    boolean existsByUtenteIdAndWatchableIdAndInVideotecaTrue(Integer utenteId, Integer watchableId);

    boolean existsByUtenteIdAndWatchableIdAndValutazioneIsNotNull(Integer utenteId, Integer watchableId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "WHERE v.id = :visioneWatchableId")
    Optional<Visione_Watchable> findByIdForUpdate(@Param("visioneWatchableId") Integer visioneWatchableId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "WHERE v.utente.id = :utenteId AND v.watchable.id = :watchableId")
    Optional<Visione_Watchable> findByUtenteIdAndWatchableIdForUpdate(@Param("utenteId") Integer utenteId, @Param("watchableId") Integer watchableId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "WHERE v.utente.id = :utenteId AND v.watchable.id = :watchableId AND v.inVideoteca = true")
    Optional<Visione_Watchable> findByUtenteIdAndWatchableIdAndInVideotecaTrueForUpdate(@Param("utenteId") Integer utenteId, @Param("watchableId") Integer watchableId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "WHERE v.utente.id = :utenteId AND v.watchable.id = :watchableId AND v.valutazione IS NOT NULL")
    Optional<Visione_Watchable> findByUtenteIdAndWatchableIdAndValutazioneNotNullForUpdate(@Param("utenteId") Integer utenteId, @Param("watchableId") Integer watchableId);


    @Query("SELECT v " +
           "FROM Visione_Watchable v " +
           "WHERE v.utente.id = :utenteId AND v.inVideoteca = true AND TYPE(v.watchable) IN (Film,SerieTV)")
    Page<Visione_Watchable> mostraSezionePrincipaleVideoteca(@Param("utenteId") Integer utenteId, Pageable pageable);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "WHERE v.utente.id = :utenteId AND v.inVideoteca = true AND TYPE(v.watchable) = Film")
    Page<Visione_Watchable> mostraSezioneFilmVideoteca(@Param("utenteId") Integer utenteId, Pageable pageable);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "WHERE v.utente.id = :utenteId AND v.inVideoteca = true AND TYPE(v.watchable) = SerieTV")
    Page<Visione_Watchable> mostraSezioneSerieTVVideoteca(@Param("utenteId") Integer utenteId, Pageable pageable);


    @Query("SELECT v " +
           "FROM Visione_Watchable v " +
           "JOIN v.watchable w " +
           "WHERE v.utente.id = :utenteId AND " +
           "      (TYPE(w) IN (Film,SerieTV)) AND " +
           "      (:titolo IS NULL OR LOWER(w.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
           "      (:genere IS NULL OR LOWER(w.genere) = LOWER(:genere)) AND " +
           "      (:anno IS NULL OR YEAR(w.dataUscita) = :anno) AND " +
           "      (:statoVisione IS NULL OR v.statoVisione = :statoVisione) AND " +
           "      (:valutazione IS NULL OR v.valutazione = :valutazione) AND " +
           "      (v.inVideoteca = true)")
    Page<Visione_Watchable> ricercaDinamicaBaseVP(@Param("utenteId") Integer utenteId, @Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("statoVisione") StatoVisione statoVisione, @Param("valutazione") Integer valutazione, Pageable pageable);


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
            "      (:valutazione IS NULL OR v.valutazione = :valutazione) AND " +
            "      (v.inVideoteca = true)")
    Page<Visione_Watchable> ricercaDinamicaFilmVP(@Param("utenteId") Integer utenteId, @Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("durata") Integer durata, @Param("regista") String regista, @Param("statoVisione") StatoVisione statoVisione, @Param("valutazione") Integer valutazione, Pageable pageable);


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
            "      (:valutazione IS NULL OR v.valutazione = :valutazione) AND " +
            "      (v.inVideoteca = true)")
    Page<Visione_Watchable> ricercaDinamicaSerieTVVP(@Param("utenteId") Integer utenteId, @Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("episodi") Integer episodi, @Param("ideatore") String ideatore, @Param("annoConclusione") Integer annoConclusione, @Param("stato") StatoSerieTV stato, @Param("statoVisione") StatoVisione statoVisione, @Param("valutazione") Integer valutazione, Pageable pageable);


    @Modifying
    @Query("UPDATE Visione_Watchable v SET v.valutazione = " +
           "(SELECT AVG(v2.valutazione) " +
            "FROM Visione_Watchable v2 " +
            "JOIN TREAT(v2.watchable AS Episodio) e " +
            "WHERE (v2.utente.id = :utenteId) AND " +
            "      (e.serieTv.id = :serieTvId)) " +
            "WHERE v.utente.id = :utenteId AND v.watchable.id = :serieTvId")
    void aggiornaValutazioneByMediaSerieTV(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId);



    @Query("SELECT v " +
           "FROM Visione_Watchable v " +
           "JOIN TREAT(v.watchable AS Episodio) e " +
           "WHERE v.utente.id = :utenteId AND e.serieTv.id = :serieTvId AND v.inVideoteca = true " +
           "ORDER BY e.stagione ASC, e.numero ASC")
    List<Visione_Watchable> findAllEpisodiSerieTV(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "JOIN TREAT(v.watchable AS Episodio) e " +
            "WHERE v.utente.id = :utenteId AND e.serieTv.id = :serieTvId AND e.stagione = :stagione AND v.inVideoteca = true " +
            "ORDER BY e.numero ASC")
    List<Visione_Watchable> findEpisodiBySerieTVAndStagione(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId, @Param("stagione") Integer stagione);


    @Query("SELECT v " +
            "FROM Visione_Watchable v " +
            "JOIN TREAT(v.watchable AS Episodio) e " +
            "WHERE (v.utente.id = :utenteId AND e.serieTv.id = :serieTvId) AND " +
            "      (e.stagione = :stagione AND e.numero = :numeroEpisodio) AND " +
            "      (v.inVideoteca = true)")
    Optional<Visione_Watchable> findEpisodioBySerieTVAndStagioneAndNumero(@Param("utenteId") Integer utenteId, @Param("serieTvId") Integer serieTvId, @Param("stagione") Integer stagione, @Param("numeroEpisodio") Integer numeroEpisodio);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v " +
           "FROM Visione_Watchable v " +
           "JOIN v.watchable w " +
           "WHERE v.utente.id = :utenteId AND v.valutazione IS NOT NULL AND TYPE(w) IN (Film,Episodio)")
    List<Visione_Watchable> raccogliValutazioniUtente(@Param("utenteId") Integer utenteId);


    @Modifying
    @Query("DELETE FROM Visione_Watchable v " +
           "WHERE v.utente.id = :utenteId")
    void eliminaVideotecaUtente(@Param("utenteId") Integer utenteId);


    @Modifying
    @Query("DELETE FROM Visione_Watchable v " +
            "WHERE v.watchable.id = :watchableId")
    void eliminaWatchableDalleVideoteche(@Param("watchableId") Integer watchableId);


    @Modifying
    @Query("DELETE FROM Visione_Watchable v " +
           "WHERE TREAT(v.watchable AS Episodio).serieTv.id = :serieTvId")
    void eliminaEpisodiSerieTVDalleVideoteche(@Param("serieTvId") Integer serieTvId);











}
