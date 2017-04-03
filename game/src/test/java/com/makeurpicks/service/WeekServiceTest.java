package com.makeurpicks.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import com.makeurpicks.GameApplication;
import com.makeurpicks.domain.LeagueView;
import com.makeurpicks.domain.Week;
import com.makeurpicks.domain.WeekBuilder;
import com.makeurpicks.exception.GameValidationException;
import com.makeurpicks.exception.GameValidationException.GameExceptions;
import com.makeurpicks.repository.WeekRepository;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = GameApplication.class)
public class WeekServiceTest {

	@Mock
	private WeekRepository weekRepositoryMock;

	@Mock
	private LeagueIntegrationService leagueIntServiceMock;

	@Autowired
	@InjectMocks
	private WeekService weekService;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetWeeksBySeason() {
		String s2015 = UUID.randomUUID().toString();
		String s2016 = UUID.randomUUID().toString();
		
		
		Week week1 = createWeek(s2015, 1);
		Week week2 = createWeek(s2015, 2);
		Week week3 = createWeek(s2015, 3);
		
		Week weeka = createWeek(s2016, 1);
		Week weekb = createWeek(s2016, 2);
		
		when(weekRepositoryMock.save(week1)).thenReturn(week1);
		when(weekRepositoryMock.save(week2)).thenReturn(week2);
		when(weekRepositoryMock.save(week3)).thenReturn(week3);
		when(weekRepositoryMock.save(weeka)).thenReturn(weeka);
		when(weekRepositoryMock.save(weekb)).thenReturn(weekb);
		
		List<Week> weeks2015 = new ArrayList<>();
		weeks2015.add(week1);
		weeks2015.add(week2);
		weeks2015.add(week3);
		
		List<Week> weeks2016 = new ArrayList<>();
		weeks2016.add(weeka);
		weeks2016.add(weekb);
		
		when(weekRepositoryMock.findBySeasonId(s2015)).thenReturn(weeks2015);
		when(weekRepositoryMock.findBySeasonId(s2016)).thenReturn(weeks2016);
		
		List<Week> weeks = weekService.getWeeksBySeason(s2015);
		assertTrue(weeks.contains(week1));
		assertTrue(weeks.contains(week2));
		assertTrue(weeks.contains(week3));
		
		assertFalse(weeks.contains(weeka));
		assertFalse(weeks.contains(weekb));
		
		weeks = weekService.getWeeksBySeason(s2016);
		assertFalse(weeks.contains(week1));
		assertFalse(weeks.contains(week2));
		assertFalse(weeks.contains(week3));
		
		assertTrue(weeks.contains(weeka));
		assertTrue(weeks.contains(weekb));
	}


	@Test
	public void testGetWeeksBySeasonWhichDoesNotExist() {

		String weekId22017 = UUID.randomUUID().toString();
		List<Week> weeks22017 = new ArrayList<>();

		// find a week which doesn't exist and it should return empty list
		when(weekRepositoryMock.findBySeasonId(weekId22017)).thenReturn(weeks22017);

		List<Week> weeks = weekService.getWeeksBySeason(weekId22017);
		assertTrue(weeks.isEmpty());
	}

	@Test
	public void getWeeksByLeagueWhichDoesNotExist() {
		// find a league which doesn't exist or not created in DB and it should
		// return empty list
		String leagueId = UUID.randomUUID().toString();

		LeagueView leagueView = new LeagueView();
		leagueView.setId(leagueId);
		leagueView.setSeasonId(UUID.randomUUID().toString());
		leagueView.setLeagueName("Pickem 2016");
		when(leagueIntServiceMock.getLeagueById(leagueId)).thenReturn(leagueView);

		List<Week> weeks = weekService.getWeeksByLeague(leagueId);

		assertTrue(weeks.isEmpty());
	}

	/*@Test
	public void getWeeksBySeason_seasonIdIsNull_throwsNullPointerException() {
		expectedEx.expect(NullPointerException.class);
		String seasonId = UUID.randomUUID().toString();
		when(weekRepositoryMock.findBySeasonId(seasonId)).thenReturn(null);
		weekService.getWeeksBySeason(seasonId);
	}*/

	@Test
	public void getWeeksBySeason_seasonIdIsNotNull_returnsListOfWeeks() {
		String seasonId = UUID.randomUUID().toString();
		Week week1 = new Week();
		week1.setId(UUID.randomUUID().toString());
		week1.setSeasonId(seasonId);
		week1.setWeekNumber((int) (Math.random() + 1));

		Week week2 = new Week();
		week2.setId(UUID.randomUUID().toString());
		week2.setSeasonId(seasonId);
		week2.setWeekNumber((int) (Math.random() + 1));
		
		Week week3 = new Week();
		week3.setId(UUID.randomUUID().toString());
		week3.setSeasonId(UUID.randomUUID().toString());
		week3.setWeekNumber((int) (Math.random() + 1));
		
		when(weekRepositoryMock.save(week1)).thenReturn(week1);
		when(weekRepositoryMock.save(week2)).thenReturn(week2);
		when(weekRepositoryMock.save(week3)).thenReturn(week3);

		List<Week> weeksPair1 = new ArrayList<>();
		weeksPair1.add(week1);
		weeksPair1.add(week2);
//		List<Week> weeksPair2 = new ArrayList<>();
//		weeksPair2.add(week3);

		when(weekRepositoryMock.findBySeasonId(seasonId)).thenReturn(weeksPair1);
		

		weekService.getWeeksBySeason(seasonId);
	}
	
	
	/*@Test
	public void getWeeksByLeague_leagueIdIsNull_throwsNullPointerException() {
		expectedEx.expect(NullPointerException.class);
		String leagueId = UUID.randomUUID().toString();
		LeagueView leagueView = new LeagueView();
		when(leagueIntServiceMock.getLeagueById(leagueId)).thenReturn(null);
		when(weekService.getWeeksBySeason(leagueView.getSeasonId())).thenReturn(null);
		weekService.getWeeksByLeague(leagueId);
	}*/
	
	@Test
	public void getWeeksByLeague_leagueIdIsNotNull_returnsListOfWeeks() {
		String leagueId = UUID.randomUUID().toString();
		String seasonId = UUID.randomUUID().toString();
		Week week1 = new Week();
		week1.setId(UUID.randomUUID().toString());
		week1.setSeasonId(seasonId);
		week1.setWeekNumber((int) (Math.random() + 1));

		Week week2 = new Week();
		week2.setId(UUID.randomUUID().toString());
		week2.setSeasonId(seasonId);
		week2.setWeekNumber((int) (Math.random() + 1));
		
		Week week3 = new Week();
		week3.setId(UUID.randomUUID().toString());
		week3.setSeasonId(UUID.randomUUID().toString());
		week3.setWeekNumber((int) (Math.random() + 1));
		
		when(weekRepositoryMock.save(week1)).thenReturn(week1);
		when(weekRepositoryMock.save(week2)).thenReturn(week2);
		when(weekRepositoryMock.save(week3)).thenReturn(week3);

		List<Week> weeksPair1 = new ArrayList<>();
		weeksPair1.add(week1);
		weeksPair1.add(week2);
		List<Week> weeksPair2 = new ArrayList<>();
		weeksPair2.add(week3);
		LeagueView leagueView =new LeagueView();
		leagueView.setId(UUID.randomUUID().toString());
		leagueView.setLeagueId(UUID.randomUUID().toString());
		leagueView.setSeasonId(seasonId);
		leagueView.setLeagueName("newLeague");
		when(leagueIntServiceMock.getLeagueById(leagueId)).thenReturn(leagueView);
		when(weekService.getWeeksBySeason(leagueView.getSeasonId())).thenReturn(weeksPair1); 
		weekService.getWeeksByLeague(leagueId);
	}
	
	@Test
	public void testcreateWeekWhichExist() {

		expectedEx.expect(GameValidationException.class);
		expectedEx.expectMessage(GameExceptions.WEEK_ALREADY_EXIST.toString());

		String s2015 = UUID.randomUUID().toString();
		Week week1 = createWeek(s2015, 1);
		List<Week> weeks = new ArrayList<>();
		weeks.add(week1);

		when(weekRepositoryMock.findBySeasonId(s2015)).thenReturn(weeks);
		weekService.createWeek(week1);
	}

	private Week createWeek(String seasonId, int weekNumber)
	{
		Week week = new WeekBuilder()
			.withSeasonId(seasonId)
			.withWeekNumber(weekNumber)
			.build();
		
		weekService.createWeek(week);
		
		return week;	
	}
}
