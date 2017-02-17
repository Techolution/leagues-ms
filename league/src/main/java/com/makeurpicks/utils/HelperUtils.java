package com.makeurpicks.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;

public class HelperUtils {

	public static List<LeagueName> getLeagueNameFromLeagues(Collection<League> leagues) {
		List<LeagueName> leagueNames = new ArrayList<LeagueName>();
		leagues.forEach(e -> {
			leagueNames.add(new LeagueName(e));
		});
		return leagueNames;
	}
}
