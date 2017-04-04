package com.makeurpicks.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.makeurpicks.domain.League;
import com.makeurpicks.repository.LeagueRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeagueControllerTest {

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mockMvc;

	private Principal principal;

	String testAdminUserName = "DUMMY_ADMIN_FOR_TEST";
	String leagueNameOne = "League first for Integration test";
	String leagueNameTwo = "League second for Integration test";
	String seasonId = "b8928d9f-a1ff-4cf9-9cf9-27af8c3c685d";
	String slash = "/";
	String seasonPath = "/seasonid";
	String playerPath = "/player";

	private static boolean setUpfinished = false;

	@Before
	public void init() {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();

		principal = new Principal() {
			@Override
			public String getName() {
				return testAdminUserName;
			}
		};

		if (setUpfinished) {
			return;
		} else {
			setUpfinished = true;
			// create dummy league to proceed for test
			leagueRepository.save(createLeagueObject(leagueNameOne, principal.getName(), seasonId));
		}
	}

	@Test
	public void testGetAllLeague() throws Exception {
		mockMvc.perform(get(slash).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testGetLeagueById() throws Exception {

		String jsonResponse = mockMvc.perform(get(slash).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse()
				.getContentAsString();

		League[] leagues = new Gson().fromJson(jsonResponse, League[].class);

		mockMvc.perform(get(slash + leagues[0].getId())).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.id", equalTo(leagues[0].getId())))
				.andExpect(jsonPath("$.leagueName", equalTo(leagues[0].getLeagueName())))
				.andExpect(jsonPath("$.seasonId", equalTo(seasonId))).andReturn();

	}

	@Test
	public void testGetLeagueBySeasonId() throws Exception {
		String jsonResponse = mockMvc.perform(get(slash).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse()
				.getContentAsString();

		League[] leagues = new Gson().fromJson(jsonResponse, League[].class);

		String servletPath = seasonPath + slash + leagues[0].getSeasonId();

		mockMvc.perform(get(servletPath)).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$[0].id", equalTo(leagues[0].getId())))
				.andExpect(jsonPath("$[0].leagueName", equalTo(leagues[0].getLeagueName())))
				.andExpect(jsonPath("$[0].seasonId", equalTo(leagues[0].getSeasonId()))).andReturn();

	}

	@Test
	public void testCreateLeague() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(createLeagueObject(leagueNameTwo, principal.getName(), seasonId));

		mockMvc.perform(post(slash).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.accept(MediaType.APPLICATION_JSON).content(jsonString).principal(principal))
				.andExpect(status().isOk());

	}

	@Test
	public void testUpdateLeague() throws Exception {

		String updatedName = "New Updated via PUT";

		String jsonResponse = mockMvc.perform(get(slash).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse()
				.getContentAsString();

		League[] leagues = new Gson().fromJson(jsonResponse, League[].class);

		League league = leagues[0];
		String leagueIdToBeUpdated = league.getId();
		league.setLeagueName(updatedName);

		ObjectMapper mapper = new ObjectMapper();
		String leagueJson = mapper.writeValueAsString(league);

		mockMvc.perform(put(slash).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).accept(MediaType.APPLICATION_JSON)
				.content(leagueJson).principal(principal)).andExpect(status().isOk());

		String updatedJsonResponse = mockMvc.perform(get(slash).accept(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse().getContentAsString();

		League[] updatedLeagues = new Gson().fromJson(updatedJsonResponse, League[].class);

		boolean foundUpdatedDataInDb = false;
		for (int i = 0; i < updatedLeagues.length; i++) {
			if (updatedLeagues[i].getId().equals(leagueIdToBeUpdated)) {
				foundUpdatedDataInDb = true;
			}
		}

		assertTrue(foundUpdatedDataInDb);
	}

	@Test
	public void testDeleteLeague() throws Exception {

		String leagueJsonResponse = mockMvc.perform(get(slash).accept(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse().getContentAsString();

		League[] leagues = new Gson().fromJson(leagueJsonResponse, League[].class);

		String leagueIdToDelete = leagues[0].getId();

		mockMvc.perform(delete(slash + leagueIdToDelete).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.accept(MediaType.APPLICATION_JSON).principal(principal)).andExpect(status().isOk());

		String updatedLeagueJsonResponse = mockMvc.perform(get(slash).accept(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse().getContentAsString();

		League[] updatedLeagues = new Gson().fromJson(updatedLeagueJsonResponse, League[].class);

		boolean leagueDeleted = true;
		for (int i = 0; i < updatedLeagues.length; i++) {
			if (updatedLeagues[i].getId().equals(leagueIdToDelete)) {
				leagueDeleted = false;
			}
		}
		assertTrue(leagueDeleted);
	}

	private League createLeagueObject(String leagueName, String adminId, String seasonId) {
		String leagueId = UUID.randomUUID().toString();
		League league = new League();
		league.setId(leagueId);
		league.setLeagueName(leagueName);
		league.setActive(true);
		league.setSeasonId(seasonId);
		league.setAdminId(adminId);
		league.setPassword(leagueName);
		return league;
	}

}
