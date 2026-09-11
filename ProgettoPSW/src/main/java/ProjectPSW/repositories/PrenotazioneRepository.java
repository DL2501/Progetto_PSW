package ProjectPSW.repositories;

import ProjectPSW.entities.Prenotazione;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione,Integer> {

    Optional<Prenotazione> findByCodiceBiglietto(String codiceBiglietto);

    boolean existsByCodiceBiglietto(String codiceBiglietto);

    boolean existsByCodiceBigliettoAndValidaTrue(String codiceBiglietto);

    List<Prenotazione> findByUtenteIdOrderByDataPrenotazioneDesc(Integer utenteId);

    List<Prenotazione> findByUtenteIdAndValidaTrueOrderByDataPrenotazioneDesc(Integer utenteId);

    List<Prenotazione> findByUtenteIdAndValidaFalseOrderByDataPrenotazioneDesc(Integer utenteId);

    boolean existsByUtenteIdAndFilmIdAndValidaTrue(Integer utenteId, Integer filmId);

    int countByFilmIdAndValidaTrue(Integer filmId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p " +
           "FROM Prenotazione p " +
           "WHERE p.utente.id = :utenteId")
    List<Prenotazione> findByUtenteIdForUpdate(@Param("utenteId") Integer utenteId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p " +
            "FROM Prenotazione p " +
            "WHERE p.film.id = :filmId")
    List<Prenotazione> findByFilmIdForUpdate(@Param("filmId") Integer filmId);


    @Modifying
    @Query("DELETE FROM Prenotazione p " +
           "WHERE p.utente.id = :utenteId")
    void eliminaPrenotazioniUtente(@Param("utenteId") Integer utenteId);


    @Modifying
    @Query("DELETE FROM Prenotazione p " +
            "WHERE p.film.id = :filmId")
    void eliminaPrenotazioniFilm(@Param("filmId") Integer filmId);























}
