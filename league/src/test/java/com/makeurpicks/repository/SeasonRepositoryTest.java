package com.makeurpicks.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

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

import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.SeasonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@Transactional
@Rollback
public class SeasonRepositoryTest {

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	private Season season;

	@Before
	public void setUp() {
		season = testEntityManager.persist(
				new SeasonBuilder().withStartYear(2007).withEndYear(2008).withLeagueType(LeagueType.pickem).build());
	}

	@After
	public void tearDown() {
		testEntityManager.remove(season);
		testEntityManager.clear();
	}

	@Test
	public void saveSeason_goodData_shouldSaveSeason() {
		List<Season> seasons = seasonRepository.findAll();
		assertEquals(seasons.size(), 1);
		seasonRepository.save(
				new SeasonBuilder().withStartYear(2008).withEndYear(2009).withLeagueType(LeagueType.suicide).build());
		seasons = seasonRepository.findAll();
		assertEquals(seasons.size(), 2);

		for (Season s : seasons) {
			assertThat(s).hasFieldOrProperty("id");
			assertNotNull(s.getId());
			assertThat(s).hasFieldOrProperty("startYear");
			assertThat(s).hasFieldOrProperty("endYear");
			assertThat(s).hasFieldOrProperty("leagueType");
		}
	}

	@Test
	public void findSeasonByLeagueType_goodData_shouldFindSeasons() {
		// test If the season exists
		List<Season> seasons = seasonRepository.getSeasonsByLeagueType(LeagueType.pickem.toString());
		assertEquals(seasons.size(), 1);
		Season thisSeason = seasons.get(0);
		assertThat(thisSeason).hasFieldOrPropertyWithValue("startYear", 2007);
		assertThat(thisSeason).hasFieldOrPropertyWithValue("endYear", 2008);
		assertThat(thisSeason).hasFieldOrPropertyWithValue("id", thisSeason.getId());
		assertEquals(thisSeason, season);
	}

	@Test
	public void findSeasonByLeagueType_noSuchLeagueType_shouldNotFindSeasons() {
		// test If the season leagueType exists
		List<Season> seasons = seasonRepository.getSeasonsByLeagueType(LeagueType.suicide.toString());
		assertEquals(seasons.size(), 0);
	}

	@Test
	public void findSeasonById_goodData_shouldFindSeason() {
		Season thisSeason = seasonRepository.findOne(season.getId());
		assertNotNull(thisSeason);
		assertEquals(thisSeason, season);
	}

	@Test
	public void findSeasonByStartYear_noSuchStartYear_shouldNotFindSeasons() {
		// test If the season leagueType exists
		List<Season> seasons = seasonRepository.getSeasonsByStartYear(2017);
		assertEquals(seasons.size(), 0);
	}

	@Test
	public void findSeasonByStartYear_goodData_shouldFindSeason() {
		List<Season> seasons = seasonRepository.getSeasonsByStartYear(season.getStartYear());
		assertNotNull(seasons);
		assertEquals(seasons.size(), 1);
	}

	@Test
	public void findSeasonByEndYear_noSuchStartYear_shouldNotFindSeasons() {
		// test If the season leagueType exists
		List<Season> seasons = seasonRepository.getSeasonsByEndYear(2017);
		assertEquals(seasons.size(), 0);
	}

	@Test
	public void findSeasonByEndYear_goodData_shouldFindSeason() {
		List<Season> seasons = seasonRepository.getSeasonsByEndYear(season.getEndYear());
		assertNotNull(seasons);
		assertEquals(seasons.size(), 1);
	}

	@Test
	public void getSeasonsByStartYearGreaterThan_goodData_shouldFindSeasons() {
		List<Season> seasons = seasonRepository.getSeasonsByCurrentYear(season.getStartYear(), season.getStartYear());
		assertNotNull(seasons);
		assertEquals(seasons.size(), 1);
	}

	@Test
	public void getSeasonsByStartYearGreaterThan_noSuchStartYearGreaterThanCurrentYear_shouldFindSeasons() {
		List<Season> seasons = seasonRepository.getSeasonsByCurrentYear(2020, 2020);
		assertNotNull(seasons);
		assertEquals(seasons.size(), 0);
	}

}
