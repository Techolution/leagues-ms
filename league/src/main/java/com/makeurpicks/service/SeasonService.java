package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Season;
import com.makeurpicks.exception.SeasonValidationException;
import com.makeurpicks.exception.SeasonValidationException.SeasonExceptions;
import com.makeurpicks.repository.SeasonRepository;

@Service
public class SeasonService {

	@Autowired
	private SeasonRepository seasonRepository;

	public List<Season> getCurrentSeasons() {
		List<Season> seasons = new ArrayList<Season>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int currentYear = calendar.get(Calendar.YEAR);
		seasons = seasonRepository.getSeasonsByCurrentYear(currentYear, currentYear);
		return seasons;
	}

	public Season findById(String seasonId) {
		if (null == seasonId)
			throw new SeasonValidationException(SeasonExceptions.SEASON_ID_IS_NULL);
		if (null == seasonRepository.findOne(seasonId))
			throw new SeasonValidationException(SeasonExceptions.NO_SUCH_SEASON_ID);
		return seasonRepository.findOne(seasonId);
	}

	public List<Season> findAllSeasons() {
		return seasonRepository.findAll();
	}

	public Season createSeason(Season season) {
		validateSeason(season);
		String id = UUID.randomUUID().toString();
		season.setId(id);
		return seasonRepository.save(season);
	}

	public Season updateSeason(Season season) {
		if (null == season || null == season.getId() || null == seasonRepository.findOne(season.getId()))
			throw new SeasonValidationException(SeasonExceptions.NO_SUCH_SEASON_ID);
		return seasonRepository.save(season);
	}

	public void deleteSeason(String seasonId) {
		if (null == seasonId || null == seasonRepository.findOne(seasonId))
			throw new SeasonValidationException(SeasonExceptions.NO_SUCH_SEASON_ID);
		if (null != seasonRepository.findOne(seasonId))
			seasonRepository.delete(seasonId);
	}

	private void validateSeason(Season season) {
		if (null == season)
			throw new SeasonValidationException(SeasonExceptions.NULL_SEASON);
		if (null == season.getLeagueType())
			throw new SeasonValidationException(SeasonExceptions.LEAGUE_TYPE_NULL);
	}
}
