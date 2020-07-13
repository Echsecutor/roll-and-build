package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.BuildingType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingTypeRepository extends CrudRepository<BuildingType, Long> {
}
