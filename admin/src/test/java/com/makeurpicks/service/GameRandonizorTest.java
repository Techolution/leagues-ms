package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.makeurpicks.season.SeasonIntegrationService;
import com.makeurpicks.season.SeasonView;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GameRandonizorTest {

	@Autowired 
	@InjectMocks
	private GameRandonizor gameRandomizer;
	
	@Mock
	private SeasonIntegrationService seasonIntegrationService;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test() {
		List<SeasonView> seasons = new ArrayList<>();
		Mockito.when(seasonIntegrationService.getCurrentSeasons()).thenReturn(seasons);
		expectedEx.expect(RuntimeException.class);
		gameRandomizer.createRandomLeague(17);
	}

}
