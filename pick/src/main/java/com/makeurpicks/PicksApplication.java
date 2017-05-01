package com.makeurpicks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableResourceServer
public class PicksApplication {
	
	
	@LoadBalanced
	@Bean(name={"loadBalancedRestTemplate"})
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Autowired
	AuthorizationCodeResourceDetails oAuth2ProtectedResourceDetails;
	@Autowired
	OAuth2ClientContext oAuth2ClientContext;
	@LoadBalanced
	@Bean
	public OAuth2RestOperations securerestTemplate() {
		return new OAuth2RestTemplate(oAuth2ProtectedResourceDetails, oAuth2ClientContext);
	}

    public static void main(String[] args) {
        SpringApplication.run(PicksApplication.class, args);
    }

    
    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            ;
        }
    }

}
