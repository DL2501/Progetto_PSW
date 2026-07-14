package ProjectPSW.repositories;

import ProjectPSW.entities.Watchable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchableRepository<T extends Watchable> extends JpaRepository<T,Integer> {

    Optional<T> findByImdbId(String imdbId);

    boolean existByImdbId(String imdbId);













}
