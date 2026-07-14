package ProjectPSW.repositories;

import ProjectPSW.entities.ContenutoCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ContenutoCatalogoRepository extends WatchableRepository<ContenutoCatalogo> {

    @Query("SELECT c " +
            "FROM ContenutoCatalogo c " +
            "WHERE (:titolo IS NULL OR LOWER(c.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND " +
            "      (:genere IS NULL OR LOWER(c.genere) = LOWER(:genere)) AND " +
            "      (:anno IS NULL OR YEAR(c.dataUscita) = :anno) AND " +
            "      (:valutazione IS NULL OR c.valutazione = :valutazione)")
    List<ContenutoCatalogo> ricercaDinamicaBase(@Param("titolo") String titolo, @Param("genere") String genere, @Param("anno") Integer anno, @Param("valutazione") Double valutazione);


    List<ContenutoCatalogo> findByDataUscitaBetween(LocalDate dataInizio, LocalDate dataFine);

    //Metodi da aggiungere: ricercaDinamica con intervallo date e valutazioni maggiori di un certo valore







}
