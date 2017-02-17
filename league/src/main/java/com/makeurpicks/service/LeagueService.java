package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.LeagueValidationException.LeagueExceptions;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import com.makeurpicks.utils.HelperUtils;

@Component
public class LeagueService {

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private PlayerLeagueRepository playerLeagueRepository;

	@Autowired
	private SeasonService seasonService;

	public List<League> findAllLeagues() {
		return leagueRepository.findAll();
	}

	public List<String> getPlayersInLeague(String leagueid) throws LeagueValidationException {
		return playerLeagueRepository.findIdPlayerIdsByIdLeagueId(leagueid);
	}

	public League createLeague(League league) throws LeagueValidationException {
		validateLeague(league);
		if (null != leagueRepository.findByLeagueName(league.getLeagueName()))
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NAME_IN_USE);
		league = leagueRepository.save(league);
		attachPlayerToLeague(league, league.getAdminId());
		return league;
	}

	public List<League> createLeague(List<League> leagues) throws LeagueValidationException {
		for (League league : leagues) {
			validateLeague(league);
			if (null != leagueRepository.findByLeagueName(league.getLeagueName()))
				throw new LeagueValidationException(LeagueExceptions.LEAGUE_NAME_IN_USE);
		}
		leagues = leagueRepository.save(leagues);
		for (League league : leagues)
			attachPlayerToLeague(league, league.getAdminId());
		return leagues;
	}

	public League findById(String id) {
		if (null == id || null == leagueRepository.findOne(id))
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
		return leagueRepository.findOne(id);
	}

	public long getLeagueCount() {
		return leagueRepository.count();
	}

	public League updateLeague(League league) {
		if (null == league || null == league.getId() || null == leagueRepository.findOne(league.getId()))
			throw new LeagueValidationException(LeagueExceptions.INVALID_LEAGUE_ID);
		validateLeague(league);
		return leagueRepository.save(league);
	}

	public List<LeagueName> getLeaguesForPlayer(String playerId) throws LeagueValidationException {
		List<String> leagueIds = playerLeagueRepository.findIdLeagueIdsByIdPlayerId(playerId);
		if (leagueIds == null || leagueIds.size() == 0) {
			return new ArrayList<LeagueName>();
		}
		List<League> leagues = leagueRepository.findAll(leagueIds);
		return HelperUtils.getLeagueNameFromLeagues(leagues);
	}

	public void joinLeague(PlayerLeague playerLeague) {
		if (playerLeague.getLeagueId() == null) {
			if (playerLeague.getLeagueName() == null) {
				throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
			} else {
				League league = getLeagueByName(playerLeague.getLeagueName());
				if (league == null)
					throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
				playerLeague.setLeagueId(league.getId());
			}
		}
		joinLeagueByPlayerIdAndPassword(playerLeague.getLeagueId(), playerLeague.getPlayerId(),
				playerLeague.getPassword());
	}

	public void deleteLeague(String id) {
		if (null == id || null == leagueRepository.findOne(id))
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
		if (null != leagueRepository.findOne(id))
			leagueRepository.delete(id);
	}

	public League getLeagueByName(String leagueName) {
		if (null == leagueName || null == leagueRepository.findByLeagueName(leagueName))
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
		return leagueRepository.findByLeagueName(leagueName);
	}

	private void validateLeague(League league) throws LeagueValidationException {
		if (league.getLeagueName() == null || league.getLeagueName().trim().isEmpty())
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NAME_IS_NULL);
		if (league.getSeasonId() == null || league.getSeasonId().trim().isEmpty())
			throw new LeagueValidationException(LeagueExceptions.SEASON_ID_IS_NULL);
		if (null == seasonService.findById(league.getSeasonId()))
			throw new LeagueValidationException(LeagueExceptions.INVALID_SEASON_ID);
		if (league.getAdminId() == null || league.getAdminId().trim().isEmpty())
			throw new LeagueValidationException(LeagueExceptions.ADMIN_NOT_FOUND);
	}

	private void joinLeagueByPlayerIdAndPassword(String leagueId, String playerId, String password)
			throws LeagueValidationException {
		League league = leagueRepository.findOne(leagueId);
		if (league == null)
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
		if (league.getPassword() != null && !"".equals(league.getPassword()) && !league.getPassword().equals(password))
			throw new LeagueValidationException(LeagueExceptions.INVALID_LEAGUE_PASSWORD);
		attachPlayerToLeague(league, playerId);
	}

	private PlayerLeague attachPlayerToLeague(League league, String playerId) {
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId(), playerId));
		playerLeague.setLeagueId(league.getId());
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		playerLeague.setPlayerId(playerId);
		return playerLeagueRepository.save(playerLeague);
	}

}
