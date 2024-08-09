package io.entake.particle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException {

	private static final long serialVersionUID = 1624386562663405086L;

	public InternalServerException() {
		super();
	}

	public InternalServerException(String s) {
		super(s);
	}

}
