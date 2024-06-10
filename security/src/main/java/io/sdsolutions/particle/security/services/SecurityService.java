package io.sdsolutions.particle.security.services;

import io.sdsolutions.particle.security.model.UserDTO;

public interface SecurityService {

	public UserDTO getLoggedInUser();

}
