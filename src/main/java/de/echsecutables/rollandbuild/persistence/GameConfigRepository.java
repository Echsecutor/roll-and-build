package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.GameConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameConfigRepository extends CrudRepository<GameConfig, Long> {
}
