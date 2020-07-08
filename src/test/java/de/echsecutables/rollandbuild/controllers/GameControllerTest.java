package de.echsecutables.rollandbuild.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.GameRepository;
import de.echsecutables.rollandbuild.persistence.PlayerRepository;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ContextConfiguration
@WebAppConfiguration
@WebMvcTest(controllers = GameController.class)
@RunWith(SpringRunner.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    PlayerRepository playerRepository;

    @MockBean
    GameRepository gameRepository;

    private final Map<String, Player> playerRepoMock = new HashMap<>();
    private final Map<Long, Game> gameRepoMock = new HashMap<>();

    @BeforeEach
    void setUp() {
        Mockito.when(playerRepository.findBySessionId(ArgumentMatchers.anyString())).thenAnswer(stub -> {
            String id = stub.getArgument(0);
            if (playerRepoMock.containsKey(id))
                return Lists.list(playerRepoMock.get(id));
            return Lists.emptyList();
        });
        Mockito.when(playerRepository.save(ArgumentMatchers.any(Player.class))).thenAnswer(stub -> {
            Player player = stub.getArgument(0);
            playerRepoMock.put(player.getSessionId(), player);
            return player;
        });

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong())).thenAnswer(stub -> {
            Long id = stub.getArgument(0);
            if (gameRepoMock.containsKey(id))
                return Optional.of(gameRepoMock.get(id));
            return Optional.empty();
        });
        Mockito.when(gameRepository.save(ArgumentMatchers.any(Game.class))).thenAnswer(stub -> {
            Game game = stub.getArgument(0);
            if (game.getId() == null)
                game.setId((long) gameRepoMock.size());
            gameRepoMock.put(game.getId(), game);
            return game;
        });

    }


    @Test
    void setGameConfig() {
    }

    @Test
    void joinGame() throws Exception {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .build();

        String reStr;
        GenericApiResponse re;
        ObjectMapper mapper = new ObjectMapper();

        // join non existing game: 404
        reStr = mockMvc.perform(post("/game/join/42")
                .accept(MediaType.ALL))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        // ensure answer is a generic API Response
        mapper.readValue(reStr, GenericApiResponse.class);

        // Create Game
        reStr = mockMvc.perform(post("/game/join/")
                .accept(MediaType.ALL))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        re = mapper.readValue(reStr, GenericApiResponse.class);
        Long gameId = Long.parseLong(re.getMessage());

        Assert.assertTrue(gameRepoMock.containsKey(gameId));
        Game game = gameRepoMock.get(gameId);

        Assert.assertEquals(gameId, game.getId());
        Assert.assertEquals(1, game.getPlayers().size());


    }
}