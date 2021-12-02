package io.sdsolutions.particle.security.config.impl;

import io.sdsolutions.particle.security.filter.AutoLoginFilter;
import io.sdsolutions.particle.security.config.SecurityConfiguration;
import io.sdsolutions.particle.security.filter.impl.PassThroughSecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This configuration should NEVER be used for a production application. This is specifically for local development
 * to eliminate the annoyances of dealing with security. Upon commit, a real security implementation must be extended
 * so that users are properly identified by the application.
 *
 * @author ndimola
 *
 */
public class PassThroughSecurityConfiguration extends SecurityConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassThroughSecurityConfiguration.class);

	@Override
	protected AutoLoginFilter getAutoLoginFilter() {
		if (this.autoLoginFilter == null) {
			try {
				this.autoLoginFilter = new PassThroughSecurityFilter(super.authenticationManagerBean());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		return this.autoLoginFilter;
	}

}
