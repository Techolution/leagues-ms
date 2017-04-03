package com.makeurpicks.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.OAuthClientDetails;
import com.makeurpicks.domain.Player;
import com.makeurpicks.exception.OAuthclientValidationException;
import com.makeurpicks.service.OAuthClientsService;
import com.makeurpicks.service.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {

	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private OAuthClientsService clientService;
	
	@Value("${config.oauth2.ui-uri}")
	private String ui;
	
    @Value("${config.oauth2.admin-uri}")
	private String admin;
	
	@RequestMapping(method=RequestMethod.POST, value="/")
	public @ResponseBody Player createPlayer(@RequestBody Player player) {

			return playerService.createPlayer(player);
	
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/")
	public String testOpen()
	{
		return ui;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/getallclientdetails")
	public List<OAuthClientDetails> getAllClientDetails()
	{
		
		List<OAuthClientDetails> oauthclientdetailslist=new ArrayList<OAuthClientDetails>();
		Iterable<OAuthClientDetails> allclients=clientService.findAllClients();
		if(allclients!=null){
			allclients.forEach(oAuthClientDetails -> oauthclientdetailslist.add(oAuthClientDetails));	
		}
		
		return oauthclientdetailslist;
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/")
	public @ResponseBody OAuthClientDetails createClients(@RequestBody OAuthClientDetails oAuthClientDetails) throws OAuthclientValidationException
	{
		
		OAuthClientDetails authClientDetails=clientService.createOAuthClientDetails(oAuthClientDetails);
		
		return authClientDetails;
	}
	
	
	
}
