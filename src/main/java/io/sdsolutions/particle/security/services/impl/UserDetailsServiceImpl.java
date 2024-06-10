package io.sdsolutions.particle.security.services.impl;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

	public UserDetails loadUserByUsername(String username) {
		return new User(username, "", new ArrayList<>());
	}

}
