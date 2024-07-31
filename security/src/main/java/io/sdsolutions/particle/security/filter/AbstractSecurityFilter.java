package io.sdsolutions.particle.security.filter;

import java.io.IOException;

import io.sdsolutions.particle.exceptions.NotFoundException;
import io.sdsolutions.particle.exceptions.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public abstract class AbstractSecurityFilter extends GenericFilterBean {

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		try {
			doFilterImpl(servletRequest, servletResponse, filterChain);
			filterChain.doFilter(servletRequest, servletResponse);
		} catch (UnauthorizedException e) {
			logger.debug("UnauthorizedException. handling", e);
			handleException((HttpServletResponse) servletResponse, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
		} catch (AccessDeniedException e) {
			logger.debug("AccessDeniedException. handling", e);
			handleException((HttpServletResponse) servletResponse, HttpStatus.FORBIDDEN.value(), e.getMessage());
		} catch (NotFoundException e) {
			logger.debug("NotFoundException. handling", e);
			handleException((HttpServletResponse) servletResponse, HttpStatus.NOT_FOUND.value(), e.getMessage());
		}
	}

	public abstract void doFilterImpl(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain);

	/**
	 * Error handler for this filter. All exceptions should be sent here with the appropriate HTTP Status Code and message.
	 * 
	 * @param response - the http response
	 * @param statusCode - the status code
	 * @param errorMessage - the error message
	 * @throws IOException - if an input or output exception occurred
	 */
	protected void handleException(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
		response.setStatus(statusCode);
		response.getWriter().println(errorMessage);
	}

	/**
	 * Returns the principal. If none is found, an UnauthorizedException is escalated.
	 * 
	 * @return - the authentication
	 */
	protected Authentication getPrincipal() {
		Authentication principal = SecurityContextHolder.getContext().getAuthentication();

		if (principal == null) {
			throw new UnauthorizedException();
		}

		return principal;
	}

}
