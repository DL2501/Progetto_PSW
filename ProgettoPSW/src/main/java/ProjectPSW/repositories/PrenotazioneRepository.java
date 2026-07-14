package ProjectPSW.repositories;

import ProjectPSW.entities.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione,Integer> {

    Optional<Prenotazione> findByCodiceBiglietto(String codiceBiglietto);

    boolean existByCodiceBiglietto(String codiceBiglietto);

    boolean existByCodiceBigliettoAndValidaTrue(String codiceBiglietto);

    List<Prenotazione> findByUtenteIdOrderByDataPrenotazioneDesc(Integer utenteId);

    List<Prenotazione> findByUtenteIdAndValidaTrueOrderByDataPrenotazioneDesc(Integer utenteId);

    List<Prenotazione> findByUtenteIdAndValidaFalseOrderByDataPrenotazioneDesc(Integer utenteId);

    boolean existByUtenteIdAndFilmIdAndValidaTrue(Integer utenteId, Integer filmId);

    int countByFilmIdAndValidaTrue(Integer filmId);
















}
