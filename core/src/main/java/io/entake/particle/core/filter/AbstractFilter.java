package io.entake.particle.core.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * This AbstractFilter is a helper to make it quicker and easier to stand up a simple filter. By implementing doFilterImpl
 * the filterChain will be automatically invoked for you, leaving just the business code for your implementation without
 * worrying about the underlying Filter specifics.
 *
 * @author ndimola
 *
 */
public abstract class AbstractFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		doFilterImpl(servletRequest, servletResponse, filterChain);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	/**
	 * Your specific filter logic must be implemented with this method.
	 *
	 * @param servletRequest
	 * @param servletResponse
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
     */
	protected abstract void doFilterImpl(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException;

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
