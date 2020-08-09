package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.GameRepository;
import de.echsecutables.rollandbuild.persistence.PlayerRepository;
import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    void getPlayerData() throws Exception {

        // smoke test: return player with some sessionId
        mockMvc.perform(get("/player")
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sessionId", Matchers.not(Matchers.blankOrNullString())))
                .andReturn();
    }

    @Test
    void setPlayerName() throws Exception {

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


}