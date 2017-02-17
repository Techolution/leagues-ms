package com.makeurpicks.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeasonValidationException extends RuntimeException {
	public enum SeasonExceptions {
		SEASON_ID_IS_NULL, SEASON_START_YEAR_NULL, SEASON_END_YEAR_NULL, LEAGUE_TYPE_NULL, NULL_SEASON, NO_SUCH_SEASON_ID
	}

	private List<SeasonExceptions> exceptions;

	public SeasonValidationException(SeasonExceptions... seasonExceptions) {
		exceptions = new ArrayList<SeasonExceptions>(Arrays.asList(seasonExceptions));
	}

	public boolean hasException() {
		if (exceptions.isEmpty())
			return false;
		return true;
	}

	@Override
	public String getMessage() {
		return Arrays.toString(exceptions.toArray());
	}
}
