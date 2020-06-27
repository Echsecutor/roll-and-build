package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Building;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends CrudRepository<Building, Long> {
}
