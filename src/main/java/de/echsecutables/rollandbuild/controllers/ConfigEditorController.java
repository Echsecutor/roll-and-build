package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.controllers.exceptions.NotFoundException;
import de.echsecutables.rollandbuild.models.BuildingType;
import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.persistence.ConfigRepositories;
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
    ConfigRepositories configRepositories;

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
        Optional<BuildingType> optionalBuilding = configRepositories.findBuildingType(buildingTypeId);
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
            saved = configRepositories.save(new BuildingType());
        } else {
            if (configRepositories.findBuildingType(buildingType.getId()).isEmpty()) {
                throw new NotFoundException("Building Type " + buildingType.getId() + " does not exist. " +
                        "Post an empty request body to create a new building type.");
            }
            saved = configRepositories.save(buildingType);
        }
        LOGGER.debug("Saved building type {}", saved);
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
        Optional<Dice> optionalDice = configRepositories.findDice(diceId);
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
            saved = configRepositories.save(new Dice());
        } else {
            if (configRepositories.findDice(dice.getId()).isEmpty()) {
                throw new NotFoundException("Dice id " + dice.getId() + " does not exist. " +
                        "Post an empty request to create a new dice.");
            }
            saved = configRepositories.save(dice);
        }
        LOGGER.debug("Saved dice {}", saved);
        return ResponseEntity.ok(saved);
    }

}
