package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Board;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends CrudRepository<Board, Long> {
}
