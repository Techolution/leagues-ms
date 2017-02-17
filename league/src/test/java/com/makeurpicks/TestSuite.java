package com.makeurpicks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.makeurpicks.repository.LeagueRepositoryTest;
import com.makeurpicks.repository.SeasonRepositoryTest;
import com.makeurpicks.service.LeagueServiceTest;
import com.makeurpicks.service.SeasonServiceTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SuiteClasses(value = { LeagueRepositoryTest.class, SeasonRepositoryTest.class, SeasonServiceTest.class,
		LeagueServiceTest.class })
public class TestSuite {

	@Test
	public void test() {
		assertEquals("A", "A");
	}
}
