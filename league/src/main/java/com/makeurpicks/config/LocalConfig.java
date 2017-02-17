package com.makeurpicks.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class LocalConfig {
	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create().url("jdbc:mysql://localhost:3303/league").username("dbuser")
				.password("password").build();
	}
}
