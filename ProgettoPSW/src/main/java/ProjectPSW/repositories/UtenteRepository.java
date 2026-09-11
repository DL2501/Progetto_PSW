package ProjectPSW.repositories;

import ProjectPSW.entities.Utente;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente,Integer> {

    Optional<Utente> findByNomeUtente(String nomeUtente);

    boolean existsByNomeUtente(String nomeUtente);

    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u " +
           "FROM Utente u " +
           "WHERE u.id = :utenteId")
    Optional<Utente> findByIdForUpdate(@Param("utenteId") Integer utenteId);






















}
