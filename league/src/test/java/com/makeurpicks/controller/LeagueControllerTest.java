package com.makeurpicks.controller;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure=true)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class LeagueControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    private final static String LEAGUE_JSON_STRING = "{\"id\":\"1\",\"leagueName\":\"league1\",\"paidFor\":0,\"money\":true,\"free\":false,\"active\":true,\"password\":\"abc\",\"spreads\":true,\"doubleEnabled\":true,\"entryFee\":10.0,\"weeklyFee\":100.0,\"firstPlacePercent\":50,\"secondPlacePercent\":40,\"thirdPlacePercent\":30,\"fourthPlacePercent\":20,\"fifthPlacePercent\":10,\"doubleType\":1,\"banker\":true,\"seasonId\":\"1\",\"adminId\":\"10\"}";
    private final static String LEAGUES_JSON_STRING = "[{\"id\":\"1\",\"leagueName\":\"league1\",\"paidFor\":0,\"money\":true,\"free\":false,\"active\":true,\"password\":\"abc\",\"spreads\":true,\"doubleEnabled\":true,\"entryFee\":10.0,\"weeklyFee\":100.0,\"firstPlacePercent\":50,\"secondPlacePercent\":40,\"thirdPlacePercent\":30,\"fourthPlacePercent\":20,\"fifthPlacePercent\":10,\"doubleType\":1,\"banker\":true,\"seasonId\":\"1\",\"adminId\":\"10\"}]";
    private final static String LEAGUES_NAME_JSON_STRING = "[{\"leagueName\":\"league1\",\"leagueId\":\"1\",\"seasonId\":\"1\"}]";
    private final static String LEAGUE_TYPES = "[\"pickem\",\"suicide\"]";
    private final static String EMPTY_LIST = "[]";
    private final static String LEAGUE_ID = "1";
    private static final String SEASON_ID = "1";
    private final static String LEAGUE_NAME = "league12";
    private final static String PASSWORD = "abc";
    private final static String PLAYER_ID = "1";
    private final static String ADMIN_ID = "test";

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void givenGetAllLeaguesURI_whenNoLeaguePresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenGetAllLeaguesURI_whenLeaguesPresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(LEAGUES_JSON_STRING)))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void givenGetAllLeagueTypesURI_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/types")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(LEAGUE_TYPES)))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void givenGetLeagueByIdURI_whenLeagueNotPresent_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(get("/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenGetLeagueByIdURI_whenLeaguePresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(LEAGUE_JSON_STRING)))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void givenGetLeaguesBySeasonIdURI_whenNoLeaguePresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/seasonid/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(EMPTY_LIST)))
                .andReturn();
        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenGetLeaguesBySeasonIdURI_whenLeaguePresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/seasonid/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(LEAGUES_JSON_STRING)))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void givenCreateLeagueURI_whenLeagueNameIsNull_thenVerifyResponse() throws Exception {
        League league = getValidLeague();
        league.setLeagueName(null);
        this.mockMvc.perform(post("/").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(league)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    public void givenCreateLeagueURI_whenSeasonIdIsNull_thenVerifyResponse() throws Exception {
        League league = getValidLeague();
        league.setSeasonId(null);
        this.mockMvc.perform(post("/").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(league)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    public void givenCreateLeagueURI_whenAdminIdIsNull_thenVerifyResponse() throws Exception {
        League league = getValidLeague();
        this.mockMvc.perform(post("/").principal(() -> "")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(league)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void givenCreateLeagueURI_whenValidInput_thenVerifyResponse() throws Exception {
        League league = getValidLeague();
        MvcResult mvcResult = this.mockMvc.perform(post("/").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(league)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.leagueName").value("league12"))
                .andReturn();
        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void givenUpdateLeagueURI_whenLeagueNotPresent_thenVerifyResponse() throws Exception {
        League league = getValidLeague();
        this.mockMvc.perform(put("/").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(league)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenUpdateLeagueURI_whenLeaguePresent_thenVerifyResponse() throws Exception {
        League league = getValidLeague();
        MvcResult mvcResult = this.mockMvc.perform(put("/").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(league)))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(objectMapper.writeValueAsString(league))))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void givenGeLeaguesByPlayerIdURI_whenNoLeaguePresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/player/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    @Transactional
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:createPlayerLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deletePlayerLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenGeLeaguesByPlayerIdURI_whenLeaguesPresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/player/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(LEAGUES_NAME_JSON_STRING)))
                .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void givenAddPlayerToLeagueURI_whenLeagueNameNull_thenVerifyResponse() throws Exception {
        PlayerLeague playerLeague = getValidPlayerLeague(getValidLeague());
        playerLeague.setLeagueId(null);
        playerLeague.setLeagueName(null);
        this.mockMvc.perform(post("/player").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(playerLeague)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenAddPlayerToLeagueURI_whenLeagueByNameNull_thenVerifyResponse() throws Exception {
        PlayerLeague playerLeague = getValidPlayerLeague(getValidLeague());
        playerLeague.setLeagueId(null);
        this.mockMvc.perform(post("/player").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(playerLeague)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    public void givenAddPlayerToLeagueURI_whenLeaguePresentButPasswordMismatch_thenVerifyResponse() throws Exception {
        PlayerLeague playerLeague = getValidPlayerLeague(getValidLeague());
        playerLeague.setPassword(null);
        this.mockMvc.perform(post("/player").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(playerLeague)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenAddPlayerToLeagueURI_whenValid_thenVerifyResponse() throws Exception {
        PlayerLeague playerLeague = getValidPlayerLeague(getValidLeague());
        this.mockMvc.perform(post("/player").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(playerLeague)))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenAddPlayerToLeagueURI_whenRoleAdmin_thenVerifyResponse() throws Exception {
        PlayerLeague playerLeague = getValidPlayerLeague(getValidLeague());
        this.mockMvc.perform(post("/player").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(playerLeague)))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    @Test
    public void givenGetLeagueByName_whenNoPresent_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(get("/name/league1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();

    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenGetLeagueByName_whenPresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/name/league1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(LEAGUE_JSON_STRING)))
                .andReturn();
        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());

    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:createPlayerLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deletePlayerLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenDeletePlayerURI_whenValidInput_thenVerifyResponse() throws Exception {
        PlayerLeague playerLeague = getValidPlayerLeague(getValidLeague());
        this.mockMvc.perform(delete("/player").principal(() -> "test")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(playerLeague)))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
    }

    public void givenGetPlayerInLeagueURI_whenNotPresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/player/leagueid/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")))
                .andReturn();
        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());

    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createPlayerLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deletePlayerLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenGetPlayerInLeagueURI_whenPresent_thenVerifyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/player/leagueid/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[\"1\"]")))
                .andReturn();
        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());

    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:createPlayerLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deletePlayerLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenDeleteLeagueURI_whenValidInput_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(delete("/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")))
                .andReturn();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    @SqlGroup({
            @Sql(scripts = "classpath:createLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:createPlayerLeagueDataTest.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(scripts = "classpath:deletePlayerLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD),
            @Sql(scripts = "classpath:deleteLeagueDataTest.sql", executionPhase = AFTER_TEST_METHOD)})
    public void givenDeletePlayerFromLeagueURI_whenValidInput_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(delete("/player/league/1/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")))
                .andReturn();
    }

    private League getValidLeague() {
        League league = new League();
        league.setId(LEAGUE_ID);
        league.setLeagueName(LEAGUE_NAME);
        league.setPassword(PASSWORD);
        league.setSeasonId(SEASON_ID);
        league.setAdminId(ADMIN_ID);
        return league;
    }

    private PlayerLeague getValidPlayerLeague(League league) {
        PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId(), PLAYER_ID));
        playerLeague.setLeagueId(league.getId());
        playerLeague.setLeagueName(league.getLeagueName());
        playerLeague.setPassword(league.getPassword());
        playerLeague.setPlayerId(PLAYER_ID);
        return playerLeague;
    }
}
