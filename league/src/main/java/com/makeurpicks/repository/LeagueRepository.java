package com.makeurpicks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeurpicks.domain.League;

public interface LeagueRepository extends JpaRepository<League, String> {
	// @Query(value = "select adminId from League where leagueName = ?")
	public List<League> findAdminIdsByLeagueName(String leagueName);

	// @Query(value = "select adminId from League where leagueName = ?")
	public List<String> findAdminIdsById(String id);

	public League findByLeagueName(String leagueName);
}
