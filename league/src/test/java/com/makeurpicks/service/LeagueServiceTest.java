package com.makeurpicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.SeasonBuilder;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.SeasonValidationException;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@Transactional
@Rollback
// @ContextConfiguration(classes = { LeagueApplication.class })
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LeagueServiceTest {

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private TestEntityManager testEntityManager;

	private League l1, l2;
	private Season s1, s2;

	@Before
	public void setUp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int currentYear = calendar.get(Calendar.YEAR);
		s1 = testEntityManager.persist(new SeasonBuilder().withEndYear(currentYear - 1).withEndYear(currentYear + 2)
				.withLeagueType(LeagueType.pickem).build());
		s2 = testEntityManager.persist(new SeasonBuilder().withEndYear(currentYear - 3).withEndYear(currentYear + 2)
				.withLeagueType(LeagueType.suicide).build());
		l1 = testEntityManager.persist(new LeagueBuilder().withAdminId("admin1").withName("league1")
				.withPassword("pass1").withSeasonId(s1.getId()).build());
		l2 = testEntityManager.persist(new LeagueBuilder().withAdminId("admin2").withName("league2")
				.withPassword("pass2").withSeasonId(s2.getId()).build());
	}

	@After
	public void tearDown() {
		testEntityManager.remove(l1);
		testEntityManager.remove(l2);
		testEntityManager.remove(s1);
		testEntityManager.remove(s2);
		testEntityManager.clear();
	}

	@Test
	public void getAllLeagues_goodData_shouldGetAllLeagues() {
		List<League> leagues = leagueService.findAllLeagues();
		assertTrue(leagues.size() > 0);
		assertTrue(leagues.contains(l1));
		assertTrue(leagues.contains(l2));
		assertEquals(leagues.size(), 2);
	}

	@Test
	public void getLeagueCount_goodData_shouldGetCorrectLeagueCount() {
		long count = leagueService.getLeagueCount();
		assertEquals(count, 2L);
	}

	@Test
	public void deleteLeague_goodData_shouldDeleteLeague() {
		Season s5 = testEntityManager.persist(
				new SeasonBuilder().withStartYear(2000).withEndYear(2010).withLeagueType(LeagueType.pickem).build());
		League l5 = testEntityManager.persist(new LeagueBuilder().withAdminId("admin5").withPassword("pass5")
				.withName("league5").withSeasonId(s5.getId()).build());
		assertEquals(leagueService.getLeagueCount(), 3);
		leagueService.deleteLeague(l5.getId());
		assertFalse(leagueService.findAllLeagues().contains(l5));
		assertEquals(leagueService.getLeagueCount(), 2);
	}

	@Test(expected = LeagueValidationException.class)
	public void deleteLeague_noSuchLeagueId_shouldThrowLeagueValidationException() {
		leagueService.deleteLeague(UUID.randomUUID().toString());
	}

	@Test
	public void updateLeague_goodData_shouldUpdateSeason() {
		l1.setAdminId("admin5");
		l1.setPassword("pass5");
		l1.setLeagueName("league1");
		l1 = leagueService.updateLeague(l1);
		League thisLeague = leagueService.findById(l1.getId());
		assertNotNull(thisLeague);
		assertThat(thisLeague).hasFieldOrPropertyWithValue("adminId", "admin5");
		assertThat(thisLeague).hasFieldOrPropertyWithValue("password", "pass5");
	}

	@Test(expected = LeagueValidationException.class)
	public void updateLeague_noSuchSeasonId_shouldThrowLeagueValidationException() {
		l1.setId(UUID.randomUUID().toString());
		leagueService.updateLeague(l1);
	}

	@Test
	public void findById_goodData_shouldFindLeague() {
		League thisLeague = leagueService.findById(l1.getId());
		assertNotNull(thisLeague);
		assertEquals(thisLeague, l1);
	}

	@Test(expected = LeagueValidationException.class)
	public void findById_noSuchId_shouldThrowLeagueValidationException() {
		leagueService.findById(UUID.randomUUID().toString());
	}

	@Test
	public void getLeagueByName_goodData_shouldFindLeague() {
		League thisLeague = leagueService.getLeagueByName(l1.getLeagueName());
		assertNotNull(thisLeague);
		assertEquals(thisLeague, l1);
	}

	@Test(expected = LeagueValidationException.class)
	public void getByLeagueName_noSuchLeagueName_shouldThrowLeagueValidationException() {
		leagueService.getLeagueByName("asdjkavdvbakcbask");
	}

	@Test
	public void createLeague_goodData_shouldCreateLeague() {
		Season s3 = testEntityManager.persist(
				new SeasonBuilder().withLeagueType(LeagueType.pickem).withStartYear(2000).withEndYear(2010).build());
		League l3 = leagueService.createLeague(new LeagueBuilder().withAdminId("admin3").withPassword("pass3")
				.withName("league3").withSeasonId(s3.getId()).build());

		assertEquals(leagueService.getLeagueCount(), 3);
		assertNotNull(l3);
		assertThat(l3).hasFieldOrPropertyWithValue("leagueName", "league3");
	}

	@Test(expected = SeasonValidationException.class)
	public void createLeague_noSuchSeasonId_shouldThrowSeasonValidationException() {
		leagueService.createLeague(new LeagueBuilder().withAdminId("admin3").withPassword("pass3").withName("league3")
				.withSeasonId(UUID.randomUUID().toString()).build());
	}

	@Test
	public void createLeague_goodListData_shouldCreateListOfLeagues() {
		Season s3 = testEntityManager.persist(
				new SeasonBuilder().withLeagueType(LeagueType.pickem).withStartYear(2000).withEndYear(2010).build());
		Season s4 = testEntityManager.persist(
				new SeasonBuilder().withLeagueType(LeagueType.suicide).withStartYear(2000).withEndYear(2010).build());
		League l3 = new LeagueBuilder().withAdminId("admin3").withPassword("pass3").withName("league3")
				.withSeasonId(s3.getId()).build();
		League l4 = new LeagueBuilder().withAdminId("admin4").withPassword("pass4").withName("league4")
				.withSeasonId(s4.getId()).build();
		List<League> thisLeagues = new ArrayList<League>();
		thisLeagues.add(l3);
		thisLeagues.add(l4);
		thisLeagues = leagueService.createLeague(thisLeagues);
		assertNotNull(leagueService.getLeagueByName(l3.getLeagueName()));
		assertNotNull(leagueService.getLeagueByName(l4.getLeagueName()));
		assertEquals(leagueService.getLeagueCount(), 4);
	}

	@Test(expected = SeasonValidationException.class)
	public void createLeague_noSushSeasonIdListData_shouldThrowSeasonValidationException() {
		League l3 = new LeagueBuilder().withAdminId("admin3").withPassword("pass3").withName("league3")
				.withSeasonId(UUID.randomUUID().toString()).build();
		League l4 = new LeagueBuilder().withAdminId("admin4").withPassword("pass4").withName("league4")
				.withSeasonId(UUID.randomUUID().toString()).build();
		List<League> thisLeagues = new ArrayList<League>();
		thisLeagues.add(l3);
		thisLeagues.add(l4);
		thisLeagues = leagueService.createLeague(thisLeagues);
	}

}
