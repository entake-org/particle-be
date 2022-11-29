package io.sdsolutions.particle.exceptions.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import io.sdsolutions.particle.exceptions.model.JsonResponseDTO;
import io.sdsolutions.particle.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.util.WebUtils;

import javax.xml.datatype.DatatypeConfigurationException;

/**
 * This control advice will capture all thrown exceptions so that they are converted to proper status
 * codes with appropriate messages. Custom messages can be supplied in the Exception, if not the default
 * message for that HttpStatus will be provided back to the client.
 *
 * @author ndimola
 */
@ControllerAdvice
public class GlobalRestExceptionHandler extends DefaultHandlerExceptionResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

	private static final String DEFAULT_EXCEPTION_MESSAGE = "An unexpected error has occurred. Please try again later.";
	private static final String NONCOMPLIANT_JSON = "Noncompliant JSON";

	private final Environment environment;

	public GlobalRestExceptionHandler(Environment environment) {
		this.environment = environment;
	}

	@ExceptionHandler(value = { ConflictException.class, IllegalArgumentException.class })
	protected ResponseEntity<Object> handleConflict(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.CONFLICT, request, response);
	}

	@ExceptionHandler(value = { NotFoundException.class })
	protected ResponseEntity<Object> handleNotFound(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.NOT_FOUND, request, response);
	}

	@ExceptionHandler(value = { ForbiddenException.class })
	protected ResponseEntity<Object> handleForbidden(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.FORBIDDEN, request, response);
	}

	@ExceptionHandler(value = { BadRequestException.class })
	protected ResponseEntity<Object> handleBadRequest(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.BAD_REQUEST, request, response);
	}

	@ExceptionHandler(value = { UnsupportedMediaTypeException.class })
	protected ResponseEntity<Object> handleUnsupportedMediaType(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, response);
	}

	@ExceptionHandler(value = { RestClientException.class, DatatypeConfigurationException.class })
	protected ResponseEntity<Object> handleServiceUnavailableException(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.SERVICE_UNAVAILABLE, request, response);
	}

	@ExceptionHandler(value = { UnauthorizedException.class })
	protected ResponseEntity<Object> handleUnauthorizedException(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.UNAUTHORIZED, request, response);
	}

	@ExceptionHandler(value = { AccessDeniedException.class })
	protected ResponseEntity<Object> handleAccessDeniedException(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.FORBIDDEN, request, response);
	}

	@ExceptionHandler(value = {GenericRestException.class})
	protected ResponseEntity<Object> handleGenericRestException(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, ((GenericRestException) e).getStatus(), request, response);
	}

	@ExceptionHandler(value = { InternalServerException.class, IllegalStateException.class, RuntimeException.class })
	protected ResponseEntity<Object> handleAllException(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR, request, response);
	}

	@ExceptionHandler(value = {RequestTimeOutException.class})
	public ResponseEntity<Object> handleRequestTimeOutExcpeiton(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.REQUEST_TIMEOUT, request, response);
	}

	@ExceptionHandler(value = { MethodArgumentNotValidException.class })
	public ResponseEntity<Object> processValidationError(MethodArgumentNotValidException e, WebRequest request, HttpServletResponse response) {
		BindingResult result = e.getBindingResult();
		String message = result.getFieldErrors().get(0).getDefaultMessage();

		return handleException(new RuntimeException(message, e), HttpStatus.BAD_REQUEST, request, response);
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> fallbackExceptionHandler(RuntimeException e, WebRequest request, HttpServletResponse response) {
		return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR, request, response);
	}

	/**
	 * Determines if there is a Rest End-Point JSON contract breach and issues a Bad Request.
	 * 
	 * @param e RuntimeException
	 * @param request WebRequest
	 * @param response HttpServletResponse
	 * @return ResponseEntity
	 */
	@ExceptionHandler(value = { HttpMessageNotReadableException.class })
	protected ResponseEntity<Object> handleHttpMessageNotReadableException(RuntimeException e, WebRequest request, HttpServletResponse response) {
		if (e.getCause() instanceof JsonMappingException) {
			return handleException(new RuntimeException(NONCOMPLIANT_JSON, e), HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, response);
		} else {
			return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR, request, response);
		}
	}

	/**
	 * All other exception handlers should funnel their response through this method. It will handle both the logging and
	 * return the proper response in the expected format. This will ensure consistent messages returned from all endpoints in
	 * situations where an HTTP Status of 200 is not provided.
	 * 
	 * @param e RuntimeException
	 * @param status HttpStatus
	 * @param request HttpServletResponse
	 * @return ResponseEntity
	 */
	private ResponseEntity<Object> handleException(RuntimeException e, HttpStatus status, WebRequest request, HttpServletResponse response) {
		String message = e.getMessage() != null ? e.getMessage() : status.getReasonPhrase();

		response.reset();

		switch (status) {
			case INTERNAL_SERVER_ERROR -> message = handleInternalServerError(e, request);
			case SERVICE_UNAVAILABLE, UNSUPPORTED_MEDIA_TYPE -> LOGGER.error(e.getMessage(), e);
			default -> LOGGER.debug(e.getMessage(), e);
		}

		return new ResponseEntity<>(getResponseDto(message, status), getHeaders(), status);
	}

	/**
	 * Helper method for {@link #handleException handleException}. This method deals with Internal Server Errors specifically
	 * in order to make the code more testable, readable, and maintainable.
	 * 
	 * @param e RuntimeException
	 * @param request WebRequest
	 * @return Exception Message
	 */
	private String handleInternalServerError(RuntimeException e, WebRequest request) {
		LOGGER.error(e.getMessage(), e);
		request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, e, RequestAttributes.SCOPE_REQUEST);

		String overrideMessage = environment.getProperty("exception.message.default");
		if (StringUtils.isNotBlank(overrideMessage)) {
			return overrideMessage;
		}

		return DEFAULT_EXCEPTION_MESSAGE;
	}

	/**
	 * Helper method to create response DTO.
	 * 
	 * @param message Response Message
	 * @return JsonResponseDTO
	 */
	private JsonResponseDTO getResponseDto(String message, HttpStatus status) {
		JsonResponseDTO dto = new JsonResponseDTO();
		dto.setError(status.getReasonPhrase());
		dto.setStatus(status.value());
		dto.setMessage(message);
		return dto;
	}

	/**
	 * Get the Origin header from the current request (request will have passed CORS filter by this point) and return it
	 * as Access-Control-Allow-Origin in a MultiValueMap
	 *
	 * @return a MultiValueMap containing the allow origin header, or empty if the current request attributes
	 * do not exist
	 */
	private MultiValueMap<String, String> getHeaders() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			headers.add(
					HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
					request.getHeader(HttpHeaders.ORIGIN)
			);
		}

		return headers;
	}
}
