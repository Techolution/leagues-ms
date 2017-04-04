package com.makeurpicks.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.makeurpicks.domain.Team;
import com.makeurpicks.domain.TeamBuilder;
import com.makeurpicks.repository.TeamRepository;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {
	private static final String TEAM_NAME = "Redskins";
	private static final String SHORT_NAME = "WAS";
	private static final String CITY_NAME = "Washington";
	private static final String LEAGUE_TYPE = "pickem";

	@Autowired
	@InjectMocks
	private TeamService teamServiceMock;

	@Mock
	private TeamRepository teamRepositoryMock;

	@Captor
	private ArgumentCaptor<Team> captor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetTeams() {
		final List<Team> dummyTeams = prepareDummyTeams();
		when(teamRepositoryMock.getTeamsByLeagueType(LEAGUE_TYPE)).thenReturn(null).thenReturn(dummyTeams);
		final List<Team> teams = teamServiceMock.getTeams(LEAGUE_TYPE);
		Assert.assertEquals(1, teams.size());
		verify(teamRepositoryMock, times(3)).getTeamsByLeagueType(LEAGUE_TYPE);
	}

	@Test
	public void getTeamMap() {
		final List<Team> baseTeams = prepareDummyTeams();
		when(teamRepositoryMock.getTeamsByLeagueType(LEAGUE_TYPE)).thenReturn(baseTeams);
		when(teamRepositoryMock.getTeamMap()).thenReturn(null).thenReturn(new HashMap<>(33));
		final Map<String, Team> teams = teamServiceMock.getTeamMap();
		assertNotNull(teams);
		verify(teamRepositoryMock, times(2)).getTeamMap();
		verify(teamRepositoryMock).getTeamsByLeagueType(LEAGUE_TYPE);

	}

	@Test
	public void testGetTeam() {
		final Team dummyTeam = constructTeam(CITY_NAME, SHORT_NAME, TEAM_NAME);
		when(teamRepositoryMock.findOne(LEAGUE_TYPE)).thenReturn(dummyTeam);
		Team team = teamServiceMock.getTeam(LEAGUE_TYPE);
		assertNotNull(team);
		verify(teamRepositoryMock).findOne(LEAGUE_TYPE);
	}

	@Test
	public void testGetTeam_teamNullFirstTime() {
		final Team dummyTeam = constructTeam(CITY_NAME, SHORT_NAME, TEAM_NAME);
		when(teamRepositoryMock.getTeamsByLeagueType(LEAGUE_TYPE)).thenReturn(prepareDummyTeams());
		when(teamRepositoryMock.findOne(LEAGUE_TYPE)).thenReturn(null).thenReturn(dummyTeam);
		final Team team = teamServiceMock.getTeam(LEAGUE_TYPE);
		assertNotNull(team);
		verify(teamRepositoryMock, times(2)).findOne(LEAGUE_TYPE);
		verify(teamRepositoryMock).getTeamsByLeagueType(LEAGUE_TYPE);
	}

	@Test
	public void testCreateTeam() {
		final Team team = constructTeam(CITY_NAME, SHORT_NAME, TEAM_NAME);
		/*
		 * Instruct mockito to do nothing when teamRepositoryMock.save will be
		 * called.
		 */
		doNothing().when(teamRepositoryMock).save(any(Team.class));
		teamServiceMock.createTeam(team);

		/*
		 * Verify that teamRepositoryMock.save was indeed called one time.
		 */
		verify(teamRepositoryMock, times(1)).save(captor.capture());
		/*
		 * Assert that teamRepositoryMock.save was called with a particular
		 * Team, assert details
		 */
		Assert.assertEquals(captor.getValue().getCity(), CITY_NAME);

	}

	private List<Team> prepareDummyTeams() {
		final List<Team> dummyTeams = new ArrayList<Team>();
		dummyTeams.add(constructTeam(CITY_NAME, SHORT_NAME, TEAM_NAME));
		return dummyTeams;
	}

	private Team constructTeam(final String cityName, String shortName, String teamName) {
		return (new TeamBuilder().withCity(cityName).withShortName(shortName).withTeamName(teamName).build());
	}

}
