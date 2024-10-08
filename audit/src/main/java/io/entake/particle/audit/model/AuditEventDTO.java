package io.entake.particle.audit.model;

import java.util.Date;
import java.util.List;

/**
 * DTO that represents an Audit Event. Generated by the auditing service, for serialization to log file.
 *
 * @author ndimola
 *
 */
public class AuditEventDTO {

	private String identifier;
	private String app;
	private String function;
	private String type;
	private String userName;
	private List<String> roles;
	private Date timestamp;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
