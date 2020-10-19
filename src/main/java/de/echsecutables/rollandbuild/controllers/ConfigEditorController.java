package de.echsecutables.rollandbuild.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.controllers.exceptions.BadRequestException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @GetMapping(value = "/Config/BuildingType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns all BuildingTypes", response = BuildingType.class, responseContainer = "List")
            }
    )
    @ApiOperation(value = "Get all Building Types")
    public ResponseEntity<List<BuildingType>> getBuildingTypes(
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        List<BuildingType> buildings = StreamSupport
                .stream(buildingTypeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        LOGGER.debug("Building Types {}", buildings);
        return ResponseEntity.ok(buildings);
    }

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

    @PostMapping(value = "/Config/BuildingType/setDiceId/{buildingTypeId}", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Set the Dice of a building type by Id."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the saved/created BuildingType.", response = BuildingType.class)
            }
    )
    public ResponseEntity<BuildingType> saveBuildingTypeDiceId(
            @PathVariable("buildingTypeId") Long buildingTypeId,
            @ApiParam(value = "ID of an existing Dice",
                    required = true,
                    example = "1"
            )
            @RequestBody Long diceId,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Optional<BuildingType> optionalBuilding = buildingTypeRepository.findById(buildingTypeId);
        if (optionalBuilding.isEmpty()) {
            throw new NotFoundException("Building ID not found");
        }
        Optional<Dice> optionalDice = diceRepository.findById(diceId);
        if (optionalDice.isEmpty()) {
            throw new NotFoundException("Dice ID not found");
        }
        LOGGER.debug("Found buildingType {} and Dice {}", optionalBuilding.get(), optionalDice.get());

        BuildingType buildingType = optionalBuilding.get();
        buildingType.setDice(optionalDice.get());

        return ResponseEntity.ok(buildingTypeRepository.save(buildingType));
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
            @ApiParam(value = "Leave empty to create a new BuildingType. Otherwise the Id must exist to update an existing type.",
                    required = false,
                    allowEmptyValue = true,
                    example = "",
                    type = "BuildingType"
            )
            @RequestBody(required = false) String buildingType,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        BuildingType saved = Utils.findAndChangeOrCreateNew(buildingType, buildingTypeRepository, BuildingType.class);
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

    @GetMapping(value = "/Config/DiceFace", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns all DiceFaces",
                            response = DiceFace.class,
                            responseContainer = "List"
                    )
            }
    )
    @ApiOperation(value = "Get all Dice Face")
    public ResponseEntity<List<DiceFace>> getDiceFaces(
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        List<DiceFace> diceFaces = StreamSupport.stream(diceFaceRepository.findAll().spliterator(), false).collect(Collectors.toList());
        LOGGER.debug("Dice Faces {}", diceFaces);
        return ResponseEntity.ok(diceFaces);
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
            @ApiParam(value = "Leave empty to create a new Dice Face. Otherwise the Id must exist to update an existing type.",
                    type = "DiceFace",
                    example = ""
            )
            @RequestBody(required = false) String diceFace,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        DiceFace saved = Utils.findAndChangeOrCreateNew(diceFace, diceFaceRepository, DiceFace.class);
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
            @ApiParam(value = "Leave empty to create a new GameConfig. Otherwise the Id must exist to update an existing type.",
            type = "GameConfig")
            @RequestBody(required = false) String gameConfig,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        GameConfig saved = Utils.findAndChangeOrCreateNew(gameConfig, gameConfigRepository, GameConfig.class);
        LOGGER.debug("Saved {}", saved);
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
            @ApiParam(value = "Leave empty to create a new Dice. Otherwise the Id must exist to update an existing Dice.",
                    type = "Dice",
                    example = "")
            @RequestBody(required = false) String dice,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Dice saved = Utils.findAndChangeOrCreateNew(dice, diceRepository, Dice.class);

        LOGGER.debug("Saved dice {}", saved);
        return ResponseEntity.ok(saved);
    }


    @PostMapping(value = "/Config/Dice/{diceId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Set Dice Faces by Ids."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the saved/created Dice.", response = BuildingType.class)
            }
    )
    public ResponseEntity<Dice> saveDiceByFaceIds(
            @PathVariable("diceId") Long diceId,
            @ApiParam(value = "List of Dice Face Ids",
                    type = "Long",
                    collectionFormat = "List",
                    example = "[1,2]",
                    required = true
            )
            @RequestBody String diceFaceIds,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Dice dice = diceRepository.findById(diceId)
                .orElseThrow(() -> new BadRequestException("Dice Id not found: " + diceId.toString()));

        List<DiceFace> diceFaces = new ArrayList<>();

        TypeReference<List<Long>> listTypeReference = new TypeReference<List<Long>>() {
        };
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Long> diceFaceLongIds = objectMapper.readValue(diceFaceIds, listTypeReference);
            for (Long id : diceFaceLongIds) {
                Optional<DiceFace> optionalDiceFace = diceFaceRepository.findById(id);
                if (optionalDiceFace.isEmpty()) {
                    throw new BadRequestException("Dice Face Id " + id.toString() + " does not exist.");
                }
                diceFaces.add(optionalDiceFace.get());
            }
        } catch (JsonProcessingException e) {
            LOGGER.debug(e.toString());
            throw new BadRequestException("Invalid ID List " + diceFaceIds);
        }

        dice.setDiceFaces(diceFaces);
        dice = diceRepository.save(dice);

        LOGGER.debug("Saved dice {}", dice);
        return ResponseEntity.ok(dice);
    }
}
