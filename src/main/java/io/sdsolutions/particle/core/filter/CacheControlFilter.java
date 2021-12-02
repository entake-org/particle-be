package io.sdsolutions.particle.core.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

/**
 * If you'd like to achieve client-side caching for certain endpoints you can leverage this Filter's capabilities to
 * do so. In any property, set the cacheable.uris (comma-delimited), as well as the cacheable.max.age (minutes). If you
 * just provide uris, the max age will default to 1 day.
 *
 * Be cautious about setting a long max-age, as this will cause the browser to cache an old version of potentially
 * dynamic data and can cause unexpected behavior for users of your application.
 *
 * @author ndimola
 */
@Component
public class CacheControlFilter extends AbstractFilter {

	private static final String CACHE_CONTROL_HEADER = "Cache-Control";
	private static final String PRAGMA_HEADER = "Pragma";
	private static final String NO_CACHE = "no-cache";
	private static final String MAX_AGE_BASE = "max-age=";
	private static final String MAX_AGE_DEFAULT = "1440";

	private static final String CACHEABLE_URIS_PROPERTY = "cacheable.uris";
	private static final String MAX_AGE_PROPERTY = "cacheable.max.age";

	@Autowired
	private Environment environment;

	@Override
	protected void doFilterImpl(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
		String cacheableUris = environment.getProperty(CACHEABLE_URIS_PROPERTY);

		if (StringUtils.isNotBlank(cacheableUris)) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;

			setCacheControl(response, matchUri(cacheableUris, request.getRequestURI()));
		}
	}

	private boolean matchUri(String cacheableUris, String requestUri) {
		boolean match = false;
		String[] cacheableUriList = cacheableUris.split(",");

		for (String uri : cacheableUriList) {
			UriTemplate template = new UriTemplate(uri.trim());
			match = template.matches(requestUri);

			if (match) {
				break;
			}
		}

		return match;
	}

	private void setCacheControl(HttpServletResponse response, boolean cacheOn) {
		String maxAge = environment.getProperty(MAX_AGE_PROPERTY);

		if (StringUtils.isBlank(maxAge)) {
			maxAge = MAX_AGE_DEFAULT;
		}

		String cacheValue = cacheOn ? MAX_AGE_BASE + maxAge : NO_CACHE;

		response.setHeader(CACHE_CONTROL_HEADER, cacheValue);
		response.setHeader(PRAGMA_HEADER, cacheValue);
	}

}
