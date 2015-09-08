package com.makeurpicks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.GameValidationException;
import com.makeurpicks.GameValidationException.GameExceptions;
import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.Team;
import com.makeurpicks.domain.Week;
import com.makeurpicks.repository.GameRepository;
import com.makeurpicks.repository.TeamRepository;
import com.makeurpicks.repository.WeekRepository;

@Component
public class GameService {

	@Autowired
	private GameRepository gameRepository;
	
	@Autowired
	private WeekRepository weekRepository;
	
	@Autowired
	private TeamRepository teamRepository;
	
	
	public Iterable<Week> getWeeksBySeason(String seasonId)
	{
		return weekRepository.getWeeksBySeason(seasonId);
	}
	
	public Week createWeek(Week week)
	{
		return weekRepository.createUpdateWeek(week);
	}
	
	public Iterable<Team> getTeams(String leagueType)
	{
		return teamRepository.getTeamsByLeagueType(leagueType);
	}
	
	public Game getGameById(String gameId)
	{
		return gameRepository.getGameById(gameId);
	}
	
	public Game createGame(Game game)
	{
		validateGame(game);
		
		return gameRepository.createUpdateGame(game);
	}
	
	public Game updateGame(Game game)
	{
		validateGame(game);
		
		return gameRepository.createUpdateGame(game);
	}
	
	public Iterable<Game> getGamesByWeek(String weekId)
	{
		return gameRepository.getGamesByWeek(weekId);
	}
	
	private void validateGame(Game game)
	{
		if (game==null)
			throw new GameValidationException(GameExceptions.GAME_IS_NULL);
		if ("".equals(game.getFavId()))
			throw new GameValidationException(GameExceptions.FAVORITE_IS_NULL);
		if ("".equals(game.getDogId()))
			throw new GameValidationException(GameExceptions.DOG_IS_NULL);
		if ("".equals(game.getWeekId()))
			throw new GameValidationException(GameExceptions.WEEK_IS_NULL);
		if (game.getGameStart() == 0)
			throw new GameValidationException(GameExceptions.GAMESTART_IS_NULL);
		
		if (game.getFavId().equals(game.getDogId()))
			throw new GameValidationException(GameExceptions.TEAM_CANNOT_PLAY_ITSELF);
		
		
	}
}
