package ProjectPSW.repositories;

import ProjectPSW.entities.ContenutoCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseCatalogoRepository<T extends ContenutoCatalogo> extends JpaRepository<T,Integer> {

    List<T> findByTitoloContainingIgnoreCase(String titolo);

    List<T> findByGenereContainingIgnoreCase(String genere);

    List<T> findByTitoloAndGenereAllIgnoreCase(String titolo, String genere);




}
