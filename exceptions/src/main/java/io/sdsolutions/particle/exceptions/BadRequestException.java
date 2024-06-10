package io.sdsolutions.particle.exceptions;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = -5924781531240039466L;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String s) {
		super(s);
	}
}
