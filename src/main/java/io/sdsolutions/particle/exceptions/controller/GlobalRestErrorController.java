package io.sdsolutions.particle.exceptions.controller;

import java.util.Date;
import java.util.Map;

import io.sdsolutions.particle.exceptions.model.JsonResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Global Error Controller that ensures all exceptions are converted to json before going back
 * to the client. Uses the JsonResponseDto as the format for the response.
 *
 * @author ndimola
 */
@RestController
public class GlobalRestErrorController implements ErrorController {

	private final ErrorAttributes errorAttributes;

	public GlobalRestErrorController(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping(value = "/error")
	public JsonResponseDTO error(HttpServletRequest request, WebRequest webRequest, HttpServletResponse response) {
		Map<String, Object> attrs = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

		JsonResponseDTO dto = new JsonResponseDTO();
		dto.setStatus(response.getStatus());
		dto.setError((String) attrs.get("error"));
		dto.setMessage((String) attrs.get("message"));
		dto.setTimestamp((Date) attrs.get("timestamp"));

		return dto;
	}
}
