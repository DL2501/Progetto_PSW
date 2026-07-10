package ProjectPSW.repositories;

import ProjectPSW.entities.Film;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends BaseCatalogoRepository<Film> {

    List<Film> findByDurata(Integer durata);

    List<Film> findByDurataGreaterThan(Integer durata);

    List<Film> findByDurataLessThan(Integer durata);

    List<Film> findByDurataBetween(Integer durataMin, Integer durataMax);

    List<Film> findByRegistaContainingIgnoreCase(String regista);












}
