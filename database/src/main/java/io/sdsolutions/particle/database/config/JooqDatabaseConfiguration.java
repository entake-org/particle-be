package io.sdsolutions.particle.database.config;

import io.sdsolutions.particle.database.util.JooqExceptionTranslator;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * This abstract configuration provides an out-of-the-box means of leveraging jOOQ in your project. Upon extending this
 * configuration, you just need to specify the SQLDialect so that you can start interacting with the database of your
 * choice using jOOQ Pro.
 *
 * @author ndimola
 *
 */
public abstract class JooqDatabaseConfiguration extends MasterDatabaseConfiguration {

	public abstract SQLDialect getSQLDialect();

	@Bean
	public DataSourceConnectionProvider connectionProvider(TransactionAwareDataSourceProxy transactionAwareDataSource) {
		return new DataSourceConnectionProvider(transactionAwareDataSource);
	}

	@Bean
	public JooqExceptionTranslator exceptionTranslator() {
		return new JooqExceptionTranslator();
	}

	@Bean
	public DefaultConfiguration jooqConfig(DataSourceConnectionProvider connectionProvider, JooqExceptionTranslator exceptionTranslator) {
		DefaultConfiguration config = new DefaultConfiguration();
		config.setSQLDialect(getSQLDialect());
		config.setConnectionProvider(connectionProvider);
		config.setExecuteListenerProvider(new DefaultExecuteListenerProvider(exceptionTranslator));
		return config;
	}

	@Bean
	public DefaultDSLContext dslContext(DefaultConfiguration jooqConfig) {
		return new DefaultDSLContext(jooqConfig);
	}

}
