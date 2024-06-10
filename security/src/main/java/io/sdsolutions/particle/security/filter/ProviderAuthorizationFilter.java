package io.sdsolutions.particle.security.filter;

import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import io.sdsolutions.particle.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * This filter enables Provider-level Authorization across all endpoints by scanning the requested URL for a provider ID. If
 * one is detected, the requesting user's access will be checked to determine they are allowed to reach the requested
 * endpoint. This implementation requires that any provider-specific endpoints include the provider ID as one of the URL
 * tokens.
 * 
 * @author Nick DiMola
 * 
 */
public abstract class ProviderAuthorizationFilter extends AbstractSecurityFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderAuthorizationFilter.class);

	protected static final int PROVIDER_ID_LENGTH = 8;
	protected static final String PROVIDERS_URL_TOKEN = "providers";

	public void doFilterImpl(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
		Authentication principal = getPrincipal();
		isAuthorized((HttpServletRequest) servletRequest, principal);
	}

	/**
	 * This method is the orchestrator for determining whether or not the current user has provider access to whatever
	 * endpoint they are trying to access. Will always return true or result in an exception.
	 * 
	 * @param request
	 * @param principal
	 * @return
	 * @throws AccessDeniedException
	 * @throws NotFoundException
	 */
	private boolean isAuthorized(HttpServletRequest request, Authentication principal) {
		if (isUserInRoles(principal, getRolesToCheck())) {
			String providerId = getProviderIdFromUrl(request.getRequestURL().toString());

			if (!isUserAuthorizedForProvider(principal.getName(), providerId)) {
				throw new AccessDeniedException(principal.getName() + " does not have access to this provider.");
			}
		}

		return true;
	}

	/**
	 * By utilizing the Spring Security Authentication information, this method will determine if the user has one of the
	 * roles passed in. The current implementation is checking if the user has a provider-based role.
	 * 
	 * @param principal
	 * @param roles
	 * @return
	 */
	private boolean isUserInRoles(Authentication principal, String... roles) {
		for (String role : roles) {
			for (GrantedAuthority authority : principal.getAuthorities()) {
				if (role.equalsIgnoreCase(authority.getAuthority())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * If the user is provider-based, it will reach this block of code to extract from the database which providers they are
	 * linked to. If the provider being accessed isn't in their access list, this method will return false.
	 * 
	 * @param userId
	 * @param providerId
	 * @return
	 */
	private boolean isUserAuthorizedForProvider(String userId, String providerId) {
		if (providerId == null) {
			return true;
		}

		List<String> providerIds = getProvidersForUser(userId);

		return providerIds.contains(providerId);
	}

	/**
	 * Splits the url into tokens and determines if any of them are a provider id. This is done by inspecting each token, if
	 * they appear to be a provider id (8 numeric characters), the token is run through a provider id checker. If an invalid
	 * provider id is detected, a Not Found Exception will be escalated to the client.
	 * 
	 * @param url
	 * @return
	 * @throws NotFoundException
	 */
	private String getProviderIdFromUrl(String url) {
		String previousUrlToken = null;

		String[] urlTokens = url.split("/");
		for (String urlToken : urlTokens) {
			if (PROVIDERS_URL_TOKEN.equalsIgnoreCase(previousUrlToken) && urlToken.length() == PROVIDER_ID_LENGTH && StringUtils.isNumeric(urlToken)) {
				if (validateProviderId(urlToken)) {
					return urlToken;
				} else {
					throw new NotFoundException("Provider " + urlToken + " is not found.");
				}
			}

			previousUrlToken = urlToken;
		}

		return null;
	}

	public abstract boolean validateProviderId(String providerId);

	public abstract List<String> getProvidersForUser(String userId);

	public abstract String[] getRolesToCheck();
}
