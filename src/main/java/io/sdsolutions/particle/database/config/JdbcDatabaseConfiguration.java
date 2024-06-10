package io.sdsolutions.particle.database.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

/**
 * If you choose not to leverage jOOQ, this jdbc configuration must be extended, which will provide you access to both
 * jdbcTemplate (for parameterized queries) and simpleJdbcCall (for calling stored procedures).
 *
 * @author ndimola
 */
public class JdbcDatabaseConfiguration extends MasterDatabaseConfiguration {

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public SimpleJdbcCall simpleJdbcCall(DataSource dataSource) {
		return new SimpleJdbcCall(dataSource);
	}

}
