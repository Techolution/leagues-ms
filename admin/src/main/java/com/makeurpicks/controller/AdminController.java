package com.makeurpicks.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.season.SeasonView;
import com.makeurpicks.service.AdminService;
import com.makeurpicks.service.Dummy;
import com.makeurpicks.service.GameRandonizor;

@RestController
public class AdminController {

//	@Autowired
//    private OAuth2RestOperations restTemplate;
//
	@Value("${security.oauth2.client.logout-uri}")
	public String authServerLogout;
	
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private GameRandonizor randomizer;
	
	Log log = LogFactory.getLog(AdminController.class);
	
	
	@RequestMapping("/user")
    public HashMap<String,String> home(Principal principal) {
		HashMap<String,String> userContent=new HashMap<String,String>();
		userContent.put("name", principal.getName());
		userContent.put("authserverlogout", authServerLogout);
		return userContent;
    }
	

	@RequestMapping(value="/dummy", method = RequestMethod.POST)
	public @ResponseBody Dummy createDummyData()
	{
		return adminService.createDummyWeeks();
	}
	
	@RequestMapping(value="/random", method = RequestMethod.POST)
	public void randomizer()
	{
		randomizer.createRandomLeague(17);
	}
	
	@RequestMapping(value="/seasons", method = RequestMethod.GET)
	public List<SeasonView> getSeasons()
	{
		return adminService.getSeasons();
	}
	
	@RequestMapping(value="/seasons/current", method = RequestMethod.GET)
	public List<SeasonView> getCurrentSeason()
	{
		log.debug("----- Start - in getCurrentSeasons");
		return adminService.getCurrentSeasons();
	}
	
	
}

