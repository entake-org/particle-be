package io.sdsolutions.particle.security.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class UserDTO {

	private List<String> roles;
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setRoles(Collection<? extends GrantedAuthority> roles) {
		this.roles = new ArrayList<>();
		for (GrantedAuthority r : roles) {
			this.roles.add(r.getAuthority());
		}
	}

}
