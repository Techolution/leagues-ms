package com.makeurpicks.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/seasons")
@RestController
public class SeasonController {

	// @Autowired
	// private SeasonService seasonService;
	//
	// @RequestMapping(method=RequestMethod.GET, value="/current")
	// public @ResponseBody List<Season> getCurrentSeasons()
	// {
	// return seasonService.getCurrentSeasons();
	// }
	//
	//
	// @RequestMapping(method=RequestMethod.POST, value="/")
	//// @PreAuthorize("#userName == authentication.name")
	// @PreAuthorize("hasRole('ADMIN')")
	// public @ResponseBody Season createSeason(@RequestBody Season season)
	// {
	// return seasonService.createSeason(season);
	// }
	//
	// @RequestMapping(method=RequestMethod.PUT, value="/")
	// @PreAuthorize("hasRole('ADMIN')")
	// public @ResponseBody Season updateSeason(@RequestBody Season season)
	// {
	// return seasonService.updateSeason(season);
	// }
	//
	// @RequestMapping(method=RequestMethod.GET, value="/leaguetype")
	// public @ResponseBody LeagueType[] getLeagueType()
	// {
	// return LeagueType.values();
	// }
	//
	// @RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	//// @PreAuthorize("#userName == authentication.name")
	// @PreAuthorize("hasRole('ADMIN')")
	// public @ResponseBody boolean deleteSeason(@PathVariable String id)
	// {
	// seasonService.deleteSeason(id);
	// return true;
	// }
}
