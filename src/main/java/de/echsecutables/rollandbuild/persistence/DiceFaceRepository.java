package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.DiceFace;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiceFaceRepository extends CrudRepository<DiceFace, Long> {
}
