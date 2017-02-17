package com.makeurpicks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.makeurpicks.domain.Season;

public interface SeasonRepository extends JpaRepository<Season, String> {// CrudRepository<Season,
	// String>{
	public List<Season> getSeasonsByLeagueType(String leaueType);

	public List<Season> getSeasonsByStartYear(int startYear);

	public List<Season> getSeasonsByEndYear(int endYear);

	@Query("select s from Season s where s.startYear <= ? and s.endYear >= ? ")
	public List<Season> getSeasonsByCurrentYear(int currentYear1, int currentYear2);

}
