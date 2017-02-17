package com.makeurpicks.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LeagueValidationException extends RuntimeException {

	public enum LeagueExceptions {
		LEAGUE_NAME_IS_NULL, LEAGUE_NAME_IN_USE, SEASON_ID_IS_NULL, ADMIN_NOT_FOUND, PLAYER_NOT_FOUND, LEAGUE_NOT_FOUND, INVALID_LEAGUE_PASSWORD, INVALID_LEAGUE_ID, INVALID_SEASON_ID
	}

	private List<LeagueExceptions> exceptions;

	public LeagueValidationException(LeagueExceptions... leagueExceptions) {
		exceptions = new ArrayList<LeagueExceptions>(Arrays.asList(leagueExceptions));
	}

	public boolean hasException() {
		if (exceptions.isEmpty())
			return false;
		return true;
	}

	@Override
	public String getMessage() {
		return exceptions.toString();
	}

}
