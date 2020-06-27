package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
    List<Player> findBySessionId(String sessionId);
}
