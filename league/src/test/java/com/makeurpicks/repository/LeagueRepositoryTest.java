package com.makeurpicks.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.SeasonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@Transactional
@Rollback
public class LeagueRepositoryTest {

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	private Season season;
	private League league;

	@Before
	public void setUp() {
		season = testEntityManager.persist(
				new SeasonBuilder().withStartYear(2007).withEndYear(2008).withLeagueType(LeagueType.pickem).build());
		league = testEntityManager.persist(new LeagueBuilder().withAdminId("prithvish").withName("leagueA")
				.withPassword("secret").withSeasonId(season.getId()).build());
	}

	@After
	public void tearDown() {
		testEntityManager.remove(league);
		testEntityManager.remove(season);
		testEntityManager.clear();
	}

	@Test
	public void testSaveLeague_goodData_shouldSaveLeague() {
		League savedLeague = leagueRepository.save(new LeagueBuilder().withAdminId("prithvish").withName("leagueB")
				.withPassword("secret").withSeasonId(season.getId()).build());
		League thisLeague = leagueRepository.findByLeagueName("leagueB");
		assertThat(thisLeague).hasFieldOrPropertyWithValue("leagueName", "leagueB");
		assertThat(thisLeague).hasFieldOrPropertyWithValue("adminId", "prithvish");
		assertThat(thisLeague).hasFieldOrPropertyWithValue("seasonId", season.getId());
		assertEquals(thisLeague, savedLeague);
	}

	@Test
	public void testSaveLeague_noSuchLeagueName_shouldNotSaveLeague() {
		// try to save a League with a non-existent Season Id
		try {
			leagueRepository.save(new LeagueBuilder().withAdminId("prithvish").withName("leagueB")
					.withPassword("secret").withSeasonId(UUID.randomUUID().toString()).build());
		} catch (Exception e) {
			assertEquals(e.getClass(), EntityNotFoundException.class);
		}
	}

	@Test
	public void findByLeagueName_goodData_shouldFindLeague_shouldFindSeasons() {
		// test If the season exists
		List<Season> seasons = seasonRepository.getSeasonsByLeagueType(LeagueType.pickem.toString());
		assertEquals(seasons.size(), 1);
		Season thisSeason = seasons.get(0);
		assertThat(thisSeason).hasFieldOrPropertyWithValue("startYear", 2007);
		assertThat(thisSeason).hasFieldOrPropertyWithValue("endYear", 2008);
		assertThat(thisSeason).hasFieldOrPropertyWithValue("id", thisSeason.getId());
		assertEquals(thisSeason, season);

		// Test If league exists
		League thisLeague = leagueRepository.findByLeagueName("leagueA");
		assertThat(thisLeague).hasFieldOrPropertyWithValue("leagueName", "leagueA");
		assertThat(thisLeague).hasFieldOrPropertyWithValue("adminId", "prithvish");
		assertThat(thisLeague).hasFieldOrPropertyWithValue("seasonId", thisSeason.getId());
		assertEquals(thisLeague, league);

	}

	@Test
	public void findByLeagueName_noSuchLeagueName_shouldNotFindLeague() {
		League thisLeague = leagueRepository.findByLeagueName("leagueX");
		assertNull(thisLeague);
	}

	@Test(expected = NullPointerException.class)
	public void findByLeagueName_noSuchLeagueName_shouldThrowException() {
		League thisLeague = leagueRepository.findByLeagueName("leagueX");
		assertEquals(thisLeague.getLeagueName(), "leagueX");
	}

	@Test
	public void findAdminIdsById_goodData_shouldFindAdmins() {
		List<String> adminIds = leagueRepository.findAdminIdsById(league.getId());
		assertEquals(adminIds.size(), 1);
		assertNotNull(adminIds.get(0));
		assertEquals(adminIds.get(0), league);
	}

	@Test
	public void findAdminIdsById_noSuchLeagueId_adminNotFound() {
		List<String> adminIds = leagueRepository.findAdminIdsById(UUID.randomUUID().toString());
		assertEquals(adminIds.size(), 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void findAdminIdsById_noSuchLeagueId_shouldThrowException() {
		List<String> adminIds = leagueRepository.findAdminIdsById(UUID.randomUUID().toString());
		assertNull(adminIds.get(0));
	}

	@Test
	public void findAdminIdsByLeagueName_goodData_shouldFindAdmins() {
		List<League> adminIds = leagueRepository.findAdminIdsByLeagueName(league.getLeagueName());
		assertNotNull(adminIds);
		assertTrue(adminIds.size() > 0);
		assertNotNull(adminIds.get(0));
		assertEquals(adminIds.get(0).getAdminId(), league.getAdminId());
	}

	@Test
	public void findAdminIdsByLeagueName_noSuchLeagueName_shouldNotFindAdmins() {
		List<League> adminIds = leagueRepository.findAdminIdsByLeagueName("leagueX");
		assertNotNull(adminIds);
		assertTrue(adminIds.size() == 0);
	}

}
