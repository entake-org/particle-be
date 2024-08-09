package io.entake.particle.exceptions;

public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = -5243662440134161849L;

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String s) {
		super(s);
	}

}
