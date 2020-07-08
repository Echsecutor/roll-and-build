package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.controllers.exceptions.*;
import de.echsecutables.rollandbuild.models.*;
import de.echsecutables.rollandbuild.persistence.RepositoryWrapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Random;

@RestController
@Api
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request. (Most likely problem: No ID or mal formatted ID given.)", response = GenericApiResponse.class),
        @ApiResponse(code = 404, message = "Game unknown.", response = GenericApiResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Check the error message for details.", response = GenericApiResponse.class)
})
public class GameController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @Autowired
    RepositoryWrapper repositories;


    // Load/Create Player and Load game. Throw if game does not exist. (Caught + Transformed to GenericApiResponse)
    private Pair<Game, Player> getGameAndPlayer(Long gameId, HttpServletRequest request) {
        Utils.logRequest(LOGGER, request);
        Player player = repositories.getOrCreatePlayer(request.getSession().getId());
        Optional<Game> optionalGame = repositories.loadGame(gameId);
        if (optionalGame.isEmpty()) {
            LOGGER.debug("Game not found {}", gameId);
            throw new GameNotFoundException("Game " + gameId + " not found");
        }
        return Pair.of(optionalGame.get(), player);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game Data attached", response = Game.class)
    })
    @PostMapping(value = "/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> getGame(
            @PathVariable("gameId") Long gameId,
            HttpServletRequest request
    ) {
        Pair<Game, Player> gameAndPlayer = getGameAndPlayer(gameId, request);
        return ResponseEntity.ok(gameAndPlayer.getFirst());
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Config Accepted, game phase advanced.", response = GenericApiResponse.class),
            @ApiResponse(code = 403, message = "Player not allowed to set config for this game now.", response = GenericApiResponse.class)
    })
    @PostMapping(value = "/game/{gameId}/setConfig", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> setGameConfig(
            @PathVariable("gameId") Long gameId,
            @RequestBody GameConfig gameConfig,
            HttpServletRequest request
    ) {
        LOGGER.debug("Config {} ", gameConfig);

        Pair<Game, Player> gameAndPlayer = getGameAndPlayer(gameId, request);

        Game game = gameAndPlayer.getFirst();
        if (game.getPhase() != Phase.NOT_READY) {
            return GenericApiResponse.buildResponse(HttpStatus.FORBIDDEN, "Game " + game.getId() + " is already in phase " + game.getPhase(), request.getRequestURI());
        }

        Player player = gameAndPlayer.getSecond();
        if (!game.getPlayers().contains(player)) {
            return GenericApiResponse.buildResponse(HttpStatus.FORBIDDEN, "Player " + player.getId() + " not in game " + game.getId(), request.getRequestURI());
        }

        LOGGER.debug("Setting Config {} for game {}", gameConfig, game);

        game.setGameConfig(gameConfig);
        game.setPhase(Phase.READY);
        game.getPlayersReady().clear();
        game = repositories.save(game);
        LOGGER.info("New game config set: {}", game);

        return GenericApiResponse.buildResponse(HttpStatus.OK, "Config accepted, Phase advanced to " + game.getPhase(), request.getRequestURI());
    }

    @PostMapping(value = "/game/join", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new game game.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "New game created and joined. Message = gameId", response = GenericApiResponse.class)
            }
    )
    public ResponseEntity<GenericApiResponse> createGame(HttpServletRequest request) {
        return joinGame(null, request);
    }

    @PostMapping(value = "/game/join/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create or join game.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Existing game joined.", response = GenericApiResponse.class),
                    @ApiResponse(code = 208, message = "Player is already in game.", response = GenericApiResponse.class)
            }
    )
    public ResponseEntity<GenericApiResponse> joinGame(@ApiParam(value = "Game ID must be a positive Integer")
                                                       @PathVariable(name = "gameId", value = "gameId") Long gameId,
                                                       HttpServletRequest request) {
        Utils.logRequest(LOGGER, request);
        Game game;
        HttpStatus re = HttpStatus.OK;
        if (gameId == null) {
            game = repositories.save(new Game());
            LOGGER.info("Created new Game {}", game);
            re = HttpStatus.CREATED;
        } else {
            Optional<Game> optionalGame = repositories.loadGame(gameId);
            if (optionalGame.isEmpty()) {
                LOGGER.warn("Game not found: {}", gameId);
                return GenericApiResponse.buildResponse(HttpStatus.NOT_FOUND, "Game ID '" + gameId + "' not found.", request.getRequestURI());
            }
            game = optionalGame.get();
        }

        Player player = repositories.getOrCreatePlayer(request.getSession().getId());
        if (player.getGames().contains(game)) {
            LOGGER.debug("Player {} already in Game {}", player, game);

            if (!game.getPlayers().contains(player)) {
                LOGGER.error("FIXME! Inconsistent state! Player {} not listed in game {}", player, game);
                game.join(player);
                game = repositories.save(game);
                LOGGER.debug("Fixed game {}", game);
            }
            return GenericApiResponse.buildResponse(HttpStatus.ALREADY_REPORTED, "Already playing in Game ID '" + gameId + "'.", request.getRequestURI());
        }
        player.getGames().add(game);
        player = repositories.save(player);

        game.join(player);
        game = repositories.save(game);

        LOGGER.info("Player {} joined Game {}", player, game);
        return GenericApiResponse.buildResponse(re, game.getId().toString(), request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return Board of this player in given game.", response = Board.class),
            @ApiResponse(code = 404, message = "No board for this player in this game.", response = GenericApiResponse.class)
    })
    @GetMapping(value = "/game/{gameId}/board", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get this players Board for the given game.")
    public ResponseEntity<Board> getBoard(
            @PathVariable("gameId") Long gameId,
            HttpServletRequest request
    ) {
        Pair<Game, Player> gameAndPlayer = getGameAndPlayer(gameId, request);
        Optional<Board> optionalBoard = gameAndPlayer.getFirst().getBoards().stream()
                .filter(x -> x.getOwner() == gameAndPlayer.getSecond())
                .findAny();
        if (optionalBoard.isEmpty()) {
            throw new PlayerNotInGameException("Player " + gameAndPlayer.getSecond().getId() + " not playing in Game " + gameId);
        }
        return ResponseEntity.ok(optionalBoard.get());
    }

    // actuall rolling
    private DiceFace roll(Dice dice) {
        if (dice == null)
            return null;

        int totalNumFaces = 0;
        for (Pair<Integer, DiceFace> facePair : dice.getNumberOfSidesWithFaces()) {
            totalNumFaces += facePair.getFirst();
        }
        Random r = new Random();
        int rolled = r.nextInt(totalNumFaces);
        for (Pair<Integer, DiceFace> facePair : dice.getNumberOfSidesWithFaces()) {
            rolled -= facePair.getFirst();
            if (rolled <= 0) {
                return facePair.getSecond();
            }
        }
        throw new BugFoundException("Error in rolling, this must be unreachable.");
    }

    // advance to rolling phase - do initial rolls
    private void startRolling(Game game) {
        assert game.getPhase() == Phase.SETUP || game.getPhase() == Phase.END_OF_TURN;

        game.setPhase(Phase.ROLLING);
        for (Board board : game.getBoards()) {
            for (Building building : board.getPlacedBuildings()) {
                building.setLastRolled(roll(building.getBuildingType().getDice()));
            }
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Added player to the ready list.", response = GenericApiResponse.class),
            @ApiResponse(code = 208, message = "Player already set to ready", response = GenericApiResponse.class),
            @ApiResponse(code = 403, message = "Player is not ready! (See message.)", response = GenericApiResponse.class)
    })
    @PostMapping(value = "/game/{gameId}/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Call to indicate that you are ready to advance to the next game phase."
    )
    public ResponseEntity<GenericApiResponse> ready(
            @PathVariable("gameId") Long gameId,
            HttpServletRequest request
    ) {
        Pair<Game, Player> gameAndPlayer = getGameAndPlayer(gameId, request);
        Game game = gameAndPlayer.getFirst();
        Player player = gameAndPlayer.getSecond();
        if (game.getPlayersReady().contains(player.getId())) {
            LOGGER.warn("Player is already ready. This is likely an error in front or backend. Game: {}", game);
            return GenericApiResponse.buildResponse(HttpStatus.ALREADY_REPORTED, "Already set to ready.", request.getRequestURI());
        }

        // if there are available buildings left -> not ready!
        if (game.getBoards().stream()
                .filter(x -> x.getOwner().equals(player)).findAny()
                .orElseThrow(() -> new BugFoundException("Players Board not in game."))
                .getAvailableBuildings().size() > 0
        ) {
            LOGGER.debug("Player {} has not placed all his available buildings in game {}", player.getId(), game);
            return GenericApiResponse.buildResponse(HttpStatus.FORBIDDEN, "You need to place all available buildings first.", request.getRequestURI());
        }

        LOGGER.debug("Player {} readied in game {}", player.getId(), game);
        game.getPlayersReady().add(player.getId());

        if (game.getPlayersReady().size() == game.getPlayers().size()) {
            LOGGER.debug("All players ready in {}. Advance!", game);
            switch (game.getPhase()) {
                case READY -> game.setPhase(Phase.SETUP);
                case SETUP -> startRolling(game);
                default -> LOGGER.error("All players ready in a phase which does not support waiting for all players! {}", game);
            }
            game.getPlayersReady().clear();
        }

        game = repositories.save(game);
        LOGGER.debug("Saved new state {}", game);

        return GenericApiResponse.buildResponse(HttpStatus.OK, "Player ready", request.getRequestURI());
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Building placed.", response = GenericApiResponse.class),
            @ApiResponse(code = 403, message = "Placing that building there is not allowed.", response = GenericApiResponse.class)
    })
    @ApiOperation(
            value = "Place a building from the available Buildings list.",
            notes = "Only allowed during SETUP or BUILDING phase."
    )
    @PostMapping(
            value = "/game/{gameId}/placeBuilding",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericApiResponse> placeBuilding(
            @PathVariable("gameId") Long gameId,
            @RequestBody PlaceBuildingCommand placeBuildingCommand,
            HttpServletRequest request
    ) {
        Pair<Game, Player> gameAndPlayer = getGameAndPlayer(gameId, request);
        if (gameAndPlayer.getFirst().getPhase() != Phase.SETUP && gameAndPlayer.getFirst().getPhase() != Phase.BUILDING) {
            throw new AgainstTheRulesException("Placing buildings is not allowed in Phase " + gameAndPlayer.getFirst().getPhase());
        }
        Board board = getBoard(gameId, request).getBody();
        assert board != null;
        Building building = board.getAvailableBuildings().stream()
                .filter(x -> x.getBuildingType().getId().equals(placeBuildingCommand.getBuildingTypeId()))
                .findAny()
                .orElseThrow(() -> new BadRequestException("There is no available building of type " + placeBuildingCommand.getBuildingTypeId()));

        building.setPosition(placeBuildingCommand.getPoint());
        building.setOrientation(placeBuildingCommand.getOrientation());
        boolean placed = board.placeBuilding(building);
        if (!placed) {
            return GenericApiResponse.buildResponse(HttpStatus.FORBIDDEN, "Can not place this building there!", request.getRequestURI());
        }
        Game game = repositories.save(gameAndPlayer.getFirst());
        return GenericApiResponse.buildResponse(HttpStatus.OK, "Building placed", request.getRequestURI());
    }
}
