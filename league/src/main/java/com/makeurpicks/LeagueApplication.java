package com.makeurpicks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueBuilder;
import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.SeasonBuilder;
import com.makeurpicks.message.channels.SqlChannels;
import com.makeurpicks.service.LeagueService;
import com.makeurpicks.service.SeasonService;

@EnableResourceServer
@IntegrationComponentScan
@EnableBinding(SqlChannels.class)
@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories
public class LeagueApplication implements CommandLineRunner {
	
	@Autowired
	private SeasonService seasonService;
	
	@Autowired
	private LeagueService leagueService;
	
	public static void main(String[] args) {
		SpringApplication.run(LeagueApplication.class, args);
	}

	
//	@Configuration
//    @EnableWebSecurity
//    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
            .antMatchers("/leagues/types", "/leagues/env", "/leagues/info", "/leagues/health").permitAll()
                    .anyRequest().authenticated()
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            ;
        }
    }
  
    @Override
	public void run(String... arg0) throws Exception {
//		PlayerBuilder playerBuilder = new PlayerBuilder("admin", "admin@techolution.com", "admin");
//		playerBuilder.adAdmin();
//		Player admin =playerBuilder.build();
//		playerServices.createPlayer(admin);
    	
    	
    	Season season = new SeasonBuilder().withStartYear(2017).withEndYear(2018).withLeagueType(LeagueType.pickem).build();
    	seasonService.createSeason(season);
    	
    	League league = new LeagueBuilder()
    			.withAdminId("admin")
    			.withName("Sample League")
    			.withPassword("password")
    			.withSeasonId(season.getId())
    			.build();
    	
    	league = leagueService.createLeague(league);
    	
    	leagueService.joinLeague(league, "player1");
    	leagueService.joinLeague(league, "player2");
    	leagueService.joinLeague(league, "player3");
    	leagueService.joinLeague(league, "player4");
    	leagueService.joinLeague(league, "player5");
		
	}

	/*@Autowired
	LeagueService leagueService;
	@Bean 
	public CommandLineRunner populateDb() {
		return args->{
			String player1Id = UUID.randomUUID().toString();
//			int leagueId = UUID.randomUUID().hashCode();
			String seasonId = UUID.randomUUID().toString();
			League league = new LeagueBuilder()
					.withAdminId(player1Id)
					.withName("pickem")
					.withPassword("football")
					.withSeasonId(seasonId).withNoSpreads()
					.build();
			leagueService.createLeague(league);
			PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId(),player1Id));
			playerLeague.setLeagueId(league.getId());
			playerLeague.setLeagueName(league.getLeagueName());
			playerLeague.setPassword(league.getPassword());
			playerLeague.setPlayerId(player1Id);
			leagueService.joinLeague(playerLeague);
			
		};
	};*/
	
}
