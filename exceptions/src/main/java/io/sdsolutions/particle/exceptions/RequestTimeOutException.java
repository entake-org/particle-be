package io.sdsolutions.particle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
public class RequestTimeOutException extends RuntimeException {

	private static final long serialVersionUID = -702716764852992892L;

	public RequestTimeOutException() {
        super();
    }

    public RequestTimeOutException(String s) {
        super(s);
    }
}
