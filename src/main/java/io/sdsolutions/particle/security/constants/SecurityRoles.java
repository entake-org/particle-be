package io.sdsolutions.particle.security.constants;

/**
 * Constant declarations to support Spring Security annotations, and other security-related use cases within the system.
 * 
 * @author Nick DiMola
 * 
 */
public class SecurityRoles {

	protected SecurityRoles() {

	}

	// SpEL for PreAuthorize annotations
	protected static final String HAS_ROLE = "hasRole('";
	protected static final String HAS_ANY_ROLE = "hasAnyRole('";
	protected static final String END = "')";
	protected static final String OR = "','";

	// Provider Level Roles
	public static final String USER_ROLE = "ROLE_USER";

	// Spring Security Role Declarations
	// Use these in the PreAuthorize annotations on REST endpoints
	public static final String SS_USER = HAS_ROLE + USER_ROLE + END;

}
