package io.sdsolutions.particle.database.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.util.StringUtils;

/**
 * Base RDBMS configuration. Offers up a dataSource that can be configured using jndi (spring.datasource.jndi).
 * Alternatively, you can provide all the connection parameters required and a HikariCP connection pooled datasource
 * will be created. This makes it viable to build out applications as runnable jar files that have Tomcat/Jetty/etc
 * embedded, and can function as production components without using an external app server.
 *
 * @author ndimola
 */
public class MasterDatabaseConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterDatabaseConfiguration.class);

	@Bean
	public DataSource dataSource(Environment environment) {
		final JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		dataSourceLookup.setResourceRef(true);

		DataSource dataSource = null;

		String jndiName = environment.getProperty("spring.datasource.jndi");

		if (StringUtils.hasText(jndiName)) {
			try {
				dataSource = dataSourceLookup.getDataSource(jndiName);
			} catch (DataSourceLookupFailureException e) {
				LOGGER.debug("Can't find JNDI Data Source", e);
			}
		}

		if (dataSource == null) {
			LOGGER.info("JNDI Data Source Not Found, Using Embedded HikariCP.");

			HikariConfig hikariConfig = new HikariConfig();
			hikariConfig.setDriverClassName(environment.getRequiredProperty("spring.datasource.driverClassName"));
			hikariConfig.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
			hikariConfig.setUsername(environment.getRequiredProperty("spring.datasource.username"));
			hikariConfig.setPassword(environment.getRequiredProperty("spring.datasource.password"));

			hikariConfig.setMaximumPoolSize(environment.getRequiredProperty("spring.datasource.maximumPoolSize", Integer.class));
			hikariConfig.setConnectionTestQuery(environment.getRequiredProperty("spring.datasource.connectionTestQuery"));
			hikariConfig.setPoolName(environment.getRequiredProperty("spring.datasource.poolName"));

			hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", environment.getRequiredProperty("spring.datasource.cachePrepStmts"));
			hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", environment.getRequiredProperty("spring.datasource.prepStmtCacheSize"));
			hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", environment.getRequiredProperty("spring.datasource.prepStmtCacheSqlLimit"));
			hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", environment.getRequiredProperty("spring.datasource.useServerPrepStmts"));

			dataSource = new HikariDataSource(hikariConfig);
		}

		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Primary
	@Bean
	@DependsOn("dataSource")
	public TransactionAwareDataSourceProxy transactionAwareDataSource(DataSource dataSource) {
		return new TransactionAwareDataSourceProxy(dataSource);
	}

}
