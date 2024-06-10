package io.sdsolutions.particle.security.services.impl;

import io.sdsolutions.particle.exceptions.UnauthorizedException;
import io.sdsolutions.particle.security.model.UserDTO;
import io.sdsolutions.particle.security.services.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityServiceImpl implements SecurityService {

	public UserDTO getLoggedInUser() {
		UserDTO user = new UserDTO();

		Authentication principal = SecurityContextHolder.getContext().getAuthentication();

		if (principal == null) {
			throw new UnauthorizedException();
		}

		user.setUserId(principal.getName());
		user.setRoles(principal.getAuthorities());

		return user;
	}

}
