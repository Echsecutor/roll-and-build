package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.BuildingType;
import de.echsecutables.rollandbuild.models.Dice;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class ConfigRepositories {

    @Autowired
    private BuildingTypeRepository buildingTypeRepository;

    @Autowired
    private DiceRepository diceRepository;

    public Optional<BuildingType> findBuildingType(long id) {
        return buildingTypeRepository.findById(id);
    }

    public BuildingType save(BuildingType building) {
        return buildingTypeRepository.save(building);
    }

    public Optional<Dice> findDice(long id) {
        return diceRepository.findById(id);
    }

    public Dice save(Dice dice) {
        return diceRepository.save(dice);
    }
}
