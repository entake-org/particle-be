package io.entake.particle.security.filter;

import java.util.Enumeration;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * Base class for all other spring security filters to inherit from
 * 
 * @author Nick DiMola
 * 
 */
public abstract class AutoLoginFilter extends AbstractPreAuthenticatedProcessingFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AutoLoginFilter.class);

	protected void logRequestDetails(HttpServletRequest request) {
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOGGER.info("++                     Request Details                           ++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        LOGGER.info("Method: {}", request.getMethod());
        LOGGER.info("User: {}", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "No User Principal");
        LOGGER.info("Auth Type: {}", request.getAuthType() != null ? request.getAuthType() : "No Auth Type");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		LOGGER.info("");

		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOGGER.info("++                         Attributes                            ++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		Enumeration<String> enNames = request.getAttributeNames();
		while (enNames.hasMoreElements()) {
			String param = enNames.nextElement();

            LOGGER.info("{} - {}", param, request.getAttribute(param));
		}
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		LOGGER.info("");

		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOGGER.info("++                          Headers                              ++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String headerValue = request.getHeader(headerName);

            LOGGER.info("{} - {}", headerName, headerValue);
		}
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		LOGGER.info("");

		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOGGER.info("++                          Cookies                              ++");
		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		if (request.getCookies() != null) {
			int i = 1;
			for (Cookie cookie : request.getCookies()) {
                LOGGER.info("Cookie #{}", i);
                LOGGER.info("Domain: {}", cookie.getDomain());
                LOGGER.info("Name: {}", cookie.getName());
                LOGGER.info("Value: {}", cookie.getValue());
                LOGGER.info("Path: {}", cookie.getPath());
				LOGGER.info("");
				i++;
			}
		}

		LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
