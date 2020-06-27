package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Dice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiceRepository extends CrudRepository<Dice, Long> {
}
