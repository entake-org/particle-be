package io.sdsolutions.particle.exceptions.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The dto that represents the json message sent back to clients in error situations.
 *
 * @author ndimola
 */
public class JsonResponseDTO {

	private Integer status;
	private String error;
	private String message;
	private Date timestamp;

	public JsonResponseDTO() {
		this.timestamp = new Date();
	}

	public JsonResponseDTO(String message) {
		this.message = message;
		this.timestamp = new Date();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getFormattedTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		return sdf.format(new Date());
	}

}
