package ProjectPSW.repositories;

import ProjectPSW.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente,Integer> {

    Optional<Utente> findByNomeUtente(String nomeUtente);

    boolean existByNomeUtente(String nomeUtente);

    Optional<Utente> findByEmail(String email);

    boolean existByEmail(String email);



















}
