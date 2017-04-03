package com.makeurpicks.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.Team;
import com.makeurpicks.domain.TeamBuilder;
import com.makeurpicks.repository.TeamRepository;

@Component
public class TeamService {

	@Autowired
	private TeamRepository teamRepository;

	public List<Team> getTeams(String leagueType) {
		List<Team> teams = teamRepository.getTeamsByLeagueType(leagueType);
		if (teams == null || teams.isEmpty())
		{
			createAllTeams();
			return teamRepository.getTeamsByLeagueType(leagueType);
		}
		else
			return teams;
	}
	
	public Map<String, Team> getTeamMap() {
		Map<String, Team> teamMap = teamRepository.getTeamMap();
		if (teamMap == null || teamMap.isEmpty())
		{
			createAllTeams();
			teamMap = teamRepository.getTeamMap();
		}
		
		return teamMap;
		
		
		
	}

	public Team createTeam(Team team) {
		
		//String id = UUID.randomUUID().toString();
		team.setId(team.getShortName().toLowerCase());
		teamRepository.save(team);
		
		return team;
	}

	public Team getTeam(String id)
	{
		Team team = teamRepository.findOne(id);
		if (team == null)
		{
			createAllTeams();
			return teamRepository.findOne(id);
		}
		else
			return team;
		
	}
	
//	public Team getTeamByShortName(String leagueType, String sn)
//	{
//		List<Team> teams = getTeams(leagueType);
//		for (Team team:teams)
//		{ 
//			if (team.getShortName().equals(sn))
//				return team;
//		}
//		
//		throw new GameValidationException(sn, GameExceptions.TEAM_SHORT_TEAM_NOT_FOUND);
//	}
	
	protected void createAllTeams()
	{
		createTeams("pickem");
	}
	
	public List<Team> createTeams(String leagueType) {
		constructTeam("Arizona", "ARI", "Cardinals");
		constructTeam("Buffalo", "BUF", "Bills");
		constructTeam("Miami", "MIA", "Dolphins");
		constructTeam("New England", "NE", "Patriots");
		constructTeam("New York", "NYJ", "Jets");
		constructTeam("Baltimore", "BAL", "Ravins");
		constructTeam("Cincinnati", "CIN", "Bengals");
		constructTeam("Pittsburg", "PIT", "Steelers");
		constructTeam("Houston", "HOU", "Texans");
		constructTeam("Indianapolis", "IND", "Colts");
		constructTeam("Jacksinville", "JAC", "Jaguars");
		constructTeam("Tennessee", "TEN", "Titans");
		constructTeam("Denver", "DEN", "Broncos");
		constructTeam("Jacksinville", "JAC", "Jaguars");
		constructTeam("Tennessee", "TEN", "Titans");
		constructTeam("Denver", "DEN", "Broncos");
		constructTeam("Kansis City", "KC", "Chiefs");
		constructTeam("Oakland", "OAK", "Raiders");
		constructTeam("San Diego", "SD", "Raiders");
		constructTeam("Dallas", "DAL", "Cowboys");
		constructTeam("New York", "NYG", "Giants");
		constructTeam("Philladelpha", "PHI", "Eagles");
		constructTeam("Washington", "WAS", "Redskins");
		constructTeam("Chicago", "CHI", "Bears");
		constructTeam("Philladelpha", "PHI", "Eagles");
		constructTeam("Detroit", "DET", "Lions");
		constructTeam("Green Bay", "GB", "Packers");
		constructTeam("Minnesota", "MIN", "Vikings");
		constructTeam("Atlanta", "ATL", "Falcons");
		constructTeam("Tampa Bay", "TB", "Bucineers");
		constructTeam("New Orleans", "NO", "Saints");
		constructTeam("San Fransico", "SF", "49ers");
		constructTeam("Seattle", "SEA", "Seahawks");
		constructTeam("New Orleans", "NO", "Saints");
		constructTeam("Carolina", "CAR", "Panthers");
		constructTeam("St. Loius", "STL", "Rams");
		return getTeams(leagueType);
	}
	
	private void constructTeam(String cityName, String shortName, String teamName) {
		createTeam(new TeamBuilder().withCity(cityName).withShortName(shortName).withTeamName(teamName).build());
	}
}
