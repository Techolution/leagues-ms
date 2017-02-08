package com.makeurpicks;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.makeurpicks.domain.OAuthClientDetails;
import com.makeurpicks.domain.OAuthClientDetailsBuilder;
import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.PlayerBuilder;
import com.makeurpicks.service.OAuthClientsService;
import com.makeurpicks.service.PlayerService;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories
// @EnableResourceServer
public class AuthServerApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(AuthServerApplication.class);
	@Autowired
	private PlayerService playerServices;

	@Autowired
	private OAuthClientsService clientService;

	@Value("${config.oauth2.ui-uri}")
	private String ui;

	@Value("${config.oauth2.admin-uri}")
	private String admin;

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Configuration
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	protected static class SecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private PlayerService playerService;

		@Autowired
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(playerService).passwordEncoder(passwordEncoder());
		}

		@Bean
		public BCryptPasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		// @Override
		// @Autowired // <-- This is crucial otherwise Spring Boot creates its
		// own
		// protected void configure(AuthenticationManagerBuilder auth) throws
		// Exception {
		// log.info("Defining inMemoryAuthentication (2 users)");
		// auth
		// .inMemoryAuthentication()
		//
		// .withUser("user").password("password")
		// .roles("USER")
		//
		// .and()
		//
		// .withUser("admin").password("password")
		// .roles("USER", "ADMIN")
		// ;
		// }

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.formLogin()

					.and()

					.httpBasic().disable().anonymous().disable().authorizeRequests().anyRequest().authenticated();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(new String[] { "/players/", "/passwordutil/**" });
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

		@Value("${config.oauth2.privateKey}")
		private String privateKey;

		@Value("${config.oauth2.publicKey}")
		private String publicKey;

		@Autowired
		private AuthenticationManager authenticationManager;

		@Value("${config.oauth2.ui-uri}")
		private String ui;

		@Value("${config.oauth2.admin-uri}")
		private String admin;
		@Value("${config.oauth2.admin-ssluri}")
		private String adminssl;

		@Value("${config.oauth2.localadmin-uri:http://localhost:9000/admin/}")
		private String localadmin;
		@Autowired
		private OAuthClientsService clientService;
		@Autowired
		private DataSource dataSource;

		@Bean
		public JwtAccessTokenConverter tokenEnhancer() {
			log.info("Initializing JWT with public key:\n" + publicKey);
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			converter.setSigningKey(privateKey);
			converter.setVerifierKey(publicKey);
			return converter;
		}

		@Bean
		public JwtTokenStore tokenStore() {
			return new JwtTokenStore(tokenEnhancer());
		}

		/**
		 * Defines the security constraints on the token endpoints
		 * /oauth/token_key and /oauth/check_token Client credentials are
		 * required to access the endpoints
		 *
		 * @param oauthServer
		 * @throws Exception
		 */
		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.tokenKeyAccess("isAnonymous() || hasRole('ROLE_TRUSTED_CLIENT')") // permitAll()
					.checkTokenAccess("hasRole('TRUSTED_CLIENT')"); // isAuthenticated()
		}

		@Bean
		public BCryptPasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		/**
		 * Defines the authorization and token endpoints and the token services
		 *
		 * @param endpoints
		 * @throws Exception
		 */
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints

					// Which authenticationManager should be used for the
					// password grant
					// If not provided, ResourceOwnerPasswordTokenGranter is not
					// configured
					.authenticationManager(authenticationManager)

					// Use JwtTokenStore and our jwtAccessTokenConverter
					.tokenStore(tokenStore()).accessTokenConverter(tokenEnhancer());
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

			/*
			 * clients.inMemory() // Confidential client where client secret can
			 * be kept safe (e.g. server side)
			 * .withClient("confidential").secret("secret")
			 * .authorizedGrantTypes("client_credentials", "authorization_code",
			 * "refresh_token") .scopes("read", "write")
			 * .redirectUris(admin,localadmin,adminssl).autoApprove(true)
			 * 
			 * .and()
			 * 
			 * .withClient("ui").secret("secret")
			 * .authorizedGrantTypes("client_credentials", "authorization_code",
			 * "refresh_token") .scopes("read", "write")
			 * .redirectUris(ui).autoApprove(true)
			 * 
			 * .and()
			 * 
			 * // Public client where client secret is vulnerable (e.g. mobile
			 * apps, browsers) .withClient("public") // No secret!
			 * .authorizedGrantTypes("client_credentials", "implicit")
			 * .scopes("read")
			 * .redirectUris("http://localhost:9000/").autoApprove(true)
			 * 
			 * .and()
			 * 
			 * // Trusted client: similar to confidential client but also
			 * allowed to handle user password
			 * .withClient("trusted").secret("secret")
			 * .authorities("ROLE_TRUSTED_CLIENT")
			 * .authorizedGrantTypes("client_credentials", "password",
			 * "authorization_code", "refresh_token") .scopes("read", "write")
			 * .redirectUris("http://localhost:9000/").autoApprove(true) ;
			 */

			// Uses the DB for Clients rather than in memory
			clients.jdbc(dataSource).passwordEncoder(passwordEncoder()).clients(clientService);
		}

	}

	@Override
	public void run(String... arg0) throws Exception {
		PlayerBuilder playerBuilder = new PlayerBuilder("admin", "prithvish.mukherjee@techolution.com", "admin");
		playerBuilder.adAdmin();
		Player admin = playerBuilder.build();
		playerServices.createPlayer(admin);
		// add clients to db
		addClientsList();
	}

	/**
	 * add List of clients data to DB
	 */
	private void addClientsList() {
		List<OAuthClientDetails> clientList = new ArrayList<OAuthClientDetails>();
		clientList.add(new OAuthClientDetailsBuilder("confidential", "secret", "ROLE_ADMIN",
				"client_credentials,authorization_code,refresh_token", "read,write", admin + ",", "true").build());

		clientList.add(new OAuthClientDetailsBuilder("ui", "secret", "ROLE_USER",
				"client_credentials,authorization_code,refresh_token", "read,write", ui, "true").build());

		clientList.add(new OAuthClientDetailsBuilder("public", "", "ROLE_ADMIN", "client_credentials,implicit", "read",
				"http://localhost:9000/", "true").build());

		clientList.add(new OAuthClientDetailsBuilder("trusted", "secret", "ROLE_TRUSTED_CLIENT",
				"client_credentials,password,authorization_code,refresh_token", "read,write", "http://localhost:9000/",
				"true").build());

		clientService.createOAuthClientDetailsList(clientList);
	}
}