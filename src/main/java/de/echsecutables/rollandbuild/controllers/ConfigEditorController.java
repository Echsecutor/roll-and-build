package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.controllers.exceptions.NotFoundException;
import de.echsecutables.rollandbuild.models.BuildingType;
import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.models.DiceFace;
import de.echsecutables.rollandbuild.models.GameConfig;
import de.echsecutables.rollandbuild.persistence.BuildingTypeRepository;
import de.echsecutables.rollandbuild.persistence.DiceFaceRepository;
import de.echsecutables.rollandbuild.persistence.DiceRepository;
import de.echsecutables.rollandbuild.persistence.GameConfigRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@Api
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request. (Most likely problem: No ID or mal formatted ID given.)", response = GenericApiResponse.class),
        @ApiResponse(code = 404, message = "Trying to edit non existing Config/Building/....", response = GenericApiResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Check the error message for details.", response = GenericApiResponse.class)
})
public class ConfigEditorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigEditorController.class);

    @Autowired
    BuildingTypeRepository buildingTypeRepository;

    @Autowired
    DiceRepository diceRepository;

    @Autowired
    DiceFaceRepository diceFaceRepository;

    @Autowired
    GameConfigRepository gameConfigRepository;

    @GetMapping(value = "/Config/BuildingType/{buildingTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the requested BuildingType", response = BuildingType.class)
            }
    )
    @ApiOperation(value = "Get a Building Type by Id.")
    public ResponseEntity<BuildingType> getBuildingType(
            @PathVariable("buildingTypeId") Long buildingTypeId,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Optional<BuildingType> optionalBuilding = buildingTypeRepository.findById(buildingTypeId);
        if (optionalBuilding.isEmpty()) {
            throw new NotFoundException("Building ID not found");
        }
        LOGGER.debug("Found buildingType {}", optionalBuilding.get());
        return ResponseEntity.ok(optionalBuilding.get());
    }

    @PostMapping(value = "/Config/BuildingType", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Create a new Building Type if the body is empty, otherwise saves the body.",
            notes = "CAUTION: Posting a new BuildingType configuration for an existing building type ID will replace the old one!"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the saved/created BuildingType.", response = BuildingType.class)
            }
    )
    public ResponseEntity<BuildingType> saveBuildingType(
            @ApiParam(value = "Leave empty to create a new BuildingType. Otherwise the Id must exist to update an existing type.")
            @RequestBody(required = false) BuildingType buildingType,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        BuildingType saved;

        if (buildingType == null) {
            saved = buildingTypeRepository.save(new BuildingType());
        } else {
            if (buildingTypeRepository.findById(buildingType.getId()).isEmpty()) {
                throw new NotFoundException("Building Type " + buildingType.getId() + " does not exist. " +
                        "Post an empty request body to create a new building type.");
            }
            saved = buildingTypeRepository.save(buildingType);
        }
        LOGGER.debug("Saved building type {}", saved);
        return ResponseEntity.ok(saved);
    }


    @GetMapping(value = "/Config/DiceFace/{Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the requested DiceFace", response = DiceFace.class)
            }
    )
    @ApiOperation(value = "Get a Dice Face by Id.")
    public ResponseEntity<DiceFace> getDiceFace(
            @PathVariable("Id") Long Id,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Optional<DiceFace> optionalDiceFace = diceFaceRepository.findById(Id);
        if (optionalDiceFace.isEmpty()) {
            throw new NotFoundException("Dice Face ID not found");
        }
        LOGGER.debug("Dice Face {}", optionalDiceFace.get());
        return ResponseEntity.ok(optionalDiceFace.get());
    }

    @PostMapping(value = "/Config/DiceFace", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Create a new Dice Face if the body is empty, otherwise saves the body.",
            notes = "CAUTION: Posting a new Dice Face configuration for an existing Dice Face ID will replace the old one!"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the saved/created Dice Face.", response = DiceFace.class)
            }
    )
    public ResponseEntity<DiceFace> saveDiceFace(
            @ApiParam(value = "Leave empty to create a new Dice Face. Otherwise the Id must exist to update an existing type.")
            @RequestBody(required = false) DiceFace diceFace,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        DiceFace saved;

        if (diceFace == null) {
            saved = diceFaceRepository.save(new DiceFace());
        } else {
            if (diceFaceRepository.findById(diceFace.getId()).isEmpty()) {
                throw new NotFoundException("diceFace" + diceFace.getId() + " does not exist. " +
                        "Post an empty request body to create a new one.");
            }
            saved = diceFaceRepository.save(diceFace);
        }
        LOGGER.debug("Saved diceFace {}", saved);
        return ResponseEntity.ok(saved);
    }


    @GetMapping(value = "/Config/GameConfig/{Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the requested GameConfig", response = GameConfig.class)
            }
    )
    @ApiOperation(value = "Get a GameConfig by Id.")
    public ResponseEntity<GameConfig> getGameConfig(
            @PathVariable("Id") Long Id,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Optional<GameConfig> optional = gameConfigRepository.findById(Id);
        if (optional.isEmpty()) {
            throw new NotFoundException("Dice GameConfig ID not found");
        }
        LOGGER.debug("Dice Face {}", optional.get());
        return ResponseEntity.ok(optional.get());
    }

    @PostMapping(value = "/Config/GameConfig", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Create a new GameConfig if the body is empty, otherwise saves the body.",
            notes = "CAUTION: Posting a new GameConfig configuration for an existing  ID will replace the old one!"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the saved/created GameConfig.", response = GameConfig.class)
            }
    )
    public ResponseEntity<GameConfig> saveGameConfig(
            @ApiParam(value = "Leave empty to create a new GameConfig. Otherwise the Id must exist to update an existing type.")
            @RequestBody(required = false) GameConfig gameConfig,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        GameConfig saved;

        if (gameConfig == null) {
            saved = gameConfigRepository.save(new GameConfig());
        } else {
            if (gameConfigRepository.findById(gameConfig.getId()).isEmpty()) {
                throw new NotFoundException("gameConfig" + gameConfig.getId() + " does not exist. " +
                        "Post an empty request body to create a new one.");
            }
            saved = gameConfigRepository.save(gameConfig);
        }
        LOGGER.debug("Saved gameConfig {}", saved);
        return ResponseEntity.ok(saved);
    }


    @GetMapping(value = "/Config/Dice/{diceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the requested Dice", response = Dice.class)
            }
    )
    @ApiOperation(value = "Get a Dice by Id.")
    public ResponseEntity<Dice> getDice(
            @PathVariable("diceId") Long diceId,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Optional<Dice> optionalDice = diceRepository.findById(diceId);
        if (optionalDice.isEmpty()) {
            throw new NotFoundException("Dice ID not found");
        }
        LOGGER.debug("Found Dice {}", optionalDice.get());
        return ResponseEntity.ok(optionalDice.get());
    }

    @PostMapping(value = "/Config/Dice", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Create a new Dice if the body is empty, otherwise saves the body.",
            notes = "CAUTION: Posting a new configuration for an existing Dice ID will replace the old one!"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the saved/created Dice.", response = BuildingType.class)
            }
    )
    public ResponseEntity<Dice> saveDice(
            @ApiParam(value = "Leave empty to create a new Dice. Otherwise the Id must exist to update an existing Dice.")
            @RequestBody(required = false) Dice dice,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Dice saved;

        if (dice == null) {
            saved = diceRepository.save(new Dice());
        } else {
            if (diceRepository.findById(dice.getId()).isEmpty()) {
                throw new NotFoundException("Dice id " + dice.getId() + " does not exist. " +
                        "Post an empty request to create a new dice.");
            }
            saved = diceRepository.save(dice);
        }
        LOGGER.debug("Saved dice {}", saved);
        return ResponseEntity.ok(saved);
    }

}
