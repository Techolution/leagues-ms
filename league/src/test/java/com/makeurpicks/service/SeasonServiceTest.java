package com.makeurpicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.SeasonBuilder;
import com.makeurpicks.exception.SeasonValidationException;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@Transactional
@Rollback
// @ContextConfiguration(classes = { LeagueApplication.class })
public class SeasonServiceTest {

	@Autowired
	private SeasonService seasonService;

	@Autowired
	private TestEntityManager testEntityManager;

	private Season s1, s2, s3, s4;

	@Before
	public void setUp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int currentYear = calendar.get(Calendar.YEAR);
		s1 = testEntityManager.persist(new SeasonBuilder().withEndYear(currentYear - 1).withEndYear(currentYear + 2)
				.withLeagueType(LeagueType.pickem).build());
		s2 = testEntityManager.persist(new SeasonBuilder().withEndYear(currentYear - 3).withEndYear(currentYear + 2)
				.withLeagueType(LeagueType.suicide).build());
		s3 = testEntityManager.persist(
				new SeasonBuilder().withEndYear(2002).withEndYear(2004).withLeagueType(LeagueType.pickem).build());
		s4 = testEntityManager.persist(
				new SeasonBuilder().withEndYear(2008).withEndYear(2009).withLeagueType(LeagueType.suicide).build());
	}

	@After
	public void tearDown() {
		testEntityManager.remove(s1);
		testEntityManager.remove(s2);
		testEntityManager.remove(s3);
		testEntityManager.remove(s4);
		testEntityManager.clear();
	}

	@Test
	public void getCurrentSeasons_goodData_shouldFindCurrentSeasons() {
		List<Season> seasons = seasonService.getCurrentSeasons();
		assertEquals(seasons.size(), 2);
		assertTrue(seasons.contains(s1));
		assertTrue(seasons.contains(s2));
		assertFalse(seasons.contains(s3));
		assertFalse(seasons.contains(s4));
	}

	@Test
	public void findAllSeasons_goodData_shouldFindAllSeasons() {
		List<Season> seasons = seasonService.findAllSeasons();
		assertEquals(seasons.size(), 4);
		assertTrue(seasons.contains(s1));
		assertTrue(seasons.contains(s2));
		assertTrue(seasons.contains(s3));
		assertTrue(seasons.contains(s4));
	}

	@Test
	public void createSeason_goodData_shouldCreateSeason() {
		Season s = new SeasonBuilder().withStartYear(2016).withEndYear(2020).withLeagueType(LeagueType.pickem).build();
		s = seasonService.createSeason(s);
		assertNotNull(s);
		assertNotNull(s.getId());
		assertEquals(seasonService.findAllSeasons().size(), 5);
	}

	@Test(expected = SeasonValidationException.class)
	public void createSeason_nullSeason_shouldThrowSeasonValidationException() {
		seasonService.createSeason(null);
	}

	@Test(expected = SeasonValidationException.class)
	public void createSeason_nullLeagueType__shouldThrowSeasonValidationException() {
		seasonService.createSeason(new SeasonBuilder().withEndYear(2020).build());
	}

	@Test
	public void deleteSeason_goodData_shouldDeleteSeason() {
		seasonService.deleteSeason(s1.getId());
		assertFalse(seasonService.findAllSeasons().contains(s1));
		assertEquals(seasonService.findAllSeasons().size(), 3);
	}

	@Test(expected = SeasonValidationException.class)
	public void deleteSeason_noSuchSeasonId_shouldThrowSeasonValidationException() {
		seasonService.deleteSeason(UUID.randomUUID().toString());
	}

	@Test
	public void updateSeason_goodData_shouldUpdateSeason() {
		s1.setLeagueType(LeagueType.suicide.toString());
		s1.setEndYear(2022);
		s1 = seasonService.updateSeason(s1);
		Season thisSeason = seasonService.findById(s1.getId());
		assertNotNull(thisSeason);
		assertThat(thisSeason).hasFieldOrPropertyWithValue("leagueType", LeagueType.suicide.toString());
		assertThat(thisSeason).hasFieldOrPropertyWithValue("endYear", 2022);
	}

	@Test(expected = SeasonValidationException.class)
	public void updateSeason_noSuchSeasonId_shouldThrowSeasonValidationException() {
		s1.setId(UUID.randomUUID().toString());
		seasonService.updateSeason(s1);
	}

	@Test(expected = SeasonValidationException.class)
	public void findById_noSuchSeasonId_shouldThrowSeasonValidationException() {
		s1.setId(UUID.randomUUID().toString());
		seasonService.findById(s1.getId());
	}

	@Test
	public void findById_goodData_shouldFindTheSeason() {
		Season thisSeason = seasonService.findById(s1.getId());
		assertNotNull(thisSeason);
		assertEquals(thisSeason, s1);
	}

}
