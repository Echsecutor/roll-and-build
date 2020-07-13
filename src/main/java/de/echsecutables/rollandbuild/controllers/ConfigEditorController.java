package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.controllers.exceptions.NotFoundException;
import de.echsecutables.rollandbuild.models.BuildingType;
import de.echsecutables.rollandbuild.persistence.RepositoryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    RepositoryWrapper repositories;

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
        Optional<BuildingType> optionalBuilding = repositories.loadBuilding(buildingTypeId);
        if (optionalBuilding.isEmpty()) {
            throw new NotFoundException("Building ID not found");
        }
        return ResponseEntity.ok(optionalBuilding.get());
    }
}
