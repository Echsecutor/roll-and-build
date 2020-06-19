package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, String> {
}
