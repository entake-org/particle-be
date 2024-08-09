package io.entake.particle.core.model;

/**
 * A DTO that can be used to pass an ID back to the browser as JSON.
 *
 * @author ndimola
 *
 */
public class IdDTO {
	
	private String id;
	
	public IdDTO() {
		
	}
	
	public IdDTO(Integer id) {
		this.id = id + "";
	}
	
	public IdDTO(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
