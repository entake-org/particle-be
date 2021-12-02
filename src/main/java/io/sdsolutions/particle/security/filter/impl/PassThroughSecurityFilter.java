package io.sdsolutions.particle.security.filter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.sdsolutions.particle.security.filter.AutoLoginFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class PassThroughSecurityFilter extends AutoLoginFilter {

	private static final String DUMMY_USER = "user";
	private static final String DUMMY_PASSWORD = "password";
	private static final String DUMMY_ROLE = "ROLE_USER";

	public PassThroughSecurityFilter(AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest arg0) {
		return DUMMY_PASSWORD;
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest arg0) {
		return DUMMY_USER;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(DUMMY_ROLE));

		Authentication newAuth = new PreAuthenticatedAuthenticationToken(getPreAuthenticatedPrincipal(request), DUMMY_PASSWORD, authorities);

		super.successfulAuthentication(request, response, newAuth);
	}
}
