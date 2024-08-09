package io.entake.particle.exceptions;

import org.springframework.http.HttpStatus;

public class GenericRestException extends RuntimeException {

	private static final long serialVersionUID = -5924781531241139466L;

	private HttpStatus status;

	public GenericRestException(HttpStatus status) {
		super();
		this.status = status;
	}

	public GenericRestException(HttpStatus status, String s) {
		super(s);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return this.status;
	}
}

