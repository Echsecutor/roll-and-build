package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.GameRepository;
import de.echsecutables.rollandbuild.persistence.PlayerRepository;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;


@ContextConfiguration
@WebAppConfiguration
@WebMvcTest(controllers = UserController.class)
@RunWith(SpringRunner.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private GameRepository gameRepository;

    private Map<String, Player> playerRepoMock = new HashMap<>();
    private Map<Long, Game> gameRepoMock = new HashMap<>();

    void setupMockRepos() {

        Mockito.when(playerRepository.findById(ArgumentMatchers.anyString())).thenAnswer(stub -> {
            String id = stub.getArgument(0);
            if (playerRepoMock.containsKey(id))
                return Optional.of(playerRepoMock.get(id));
            return Optional.empty();
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
    void getPlayerData() throws Exception {

        setupMockRepos();

        // smoke test: return player with some sessionId
        mockMvc.perform(get("/player")
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sessionId", Matchers.not(Matchers.blankOrNullString())))
                .andReturn();
    }

    @Test
    void setPlayerName() throws Exception {
        setupMockRepos();
        String name = "This is my name";

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .build();

        mockMvc.perform(post("/player/name")
                .content(name)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get("/player")
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
                .andReturn();
    }

    @Test
    void joinGame() throws Exception {
        setupMockRepos();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .build();

        // join non existing game: 404
        mockMvc.perform(post("/player/join")
                .content("42")
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .accept(MediaType.ALL))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();

        // Create Game
        mockMvc.perform(post("/player/join")
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get("/player")
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.games[0]").isNumber())
                .andReturn();


    }
}