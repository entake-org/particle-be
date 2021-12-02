package io.sdsolutions.particle.audit.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.sdsolutions.particle.audit.service.AuditLogWriterService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import io.sdsolutions.particle.audit.model.AuditEventDTO;
import io.sdsolutions.particle.audit.service.AuditLogService;
import io.sdsolutions.particle.security.model.UserDTO;
import io.sdsolutions.particle.security.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service("auditLogService")
public class AuditLogServiceImpl implements AuditLogService {

	private static final String FIELD_NAME = "identifier";
	private static final String METHOD_NAME = "getIdentifier";

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogServiceImpl.class);

	private final SecurityService securityService;
	private final AuditLogWriterService auditLogWriterService;

	public AuditLogServiceImpl(SecurityService securityService, AuditLogWriterService auditLogWriterService) {
		this.securityService = securityService;
		this.auditLogWriterService = auditLogWriterService;
	}

	public void generateLogs(Object o, String eventType, String app, String function) {
		try {
			this.auditLogWriterService.writeAuditLogs(getEvents(o, eventType, app, function));
		} catch (NoSuchFieldException e) {
			LOGGER.trace("No Identifier Found - Not Logging", e);
		}
	}

	/**
	 * This method inspects the response object, gathers the user information and produces either a single event or a
	 * list of events, assuming a list of transactions or individuals were returned via an endpoint.
	 */
	private List<AuditEventDTO> getEvents(Object o, String eventType, String app, String function) throws NoSuchFieldException {
		List<AuditEventDTO> events = new ArrayList<>();
		UserDTO user = securityService.getLoggedInUser();

		if (o instanceof Collection<?>) {
			for (Object obj : (Collection<?>) o) {
				events.add(extractEventDTO(obj, eventType, app, function, user));
			}
		} else {
			events.add(extractEventDTO(o, eventType, app, function, user));
		}

		return events;
	}

	/**
	 * This method will produce the event object and will make the necessary call to extract the identifier from the
	 * endpoint's response.
	 */
	private AuditEventDTO extractEventDTO(Object o, String eventType, String app, String function, UserDTO user) throws NoSuchFieldException {
		AuditEventDTO dto = new AuditEventDTO();
		dto.setApp(app);
		dto.setIdentifier(extractIdentifier(o));
		dto.setFunction(function);

		if (user.getRoles() != null && !user.getRoles().isEmpty()) {
			dto.setRole(user.getRoles().get(0));
		} else {
			dto.setRole("ROLE_USER");
		}
		
		dto.setTimestamp(new Date());
		dto.setType(eventType);
		dto.setUserName(user.getUserId());

		return dto;
	}

	/**
	 * This delegating method will first attempt to gather the identifier from a field on the object "identifier" and
	 * will only attempt to gather the data from the "getIdentifier()" method if it is unable to find it off a simple
	 * field.
	 */
	private String extractIdentifier(Object o) throws NoSuchFieldException {
		String returnValue = null;

		returnValue = extractIdentifierFromField(o);
		returnValue = returnValue != null ? returnValue : extractIdentifierFromMethod(o);

		if (returnValue != null) {
			return returnValue;
		}

		throw new NoSuchFieldException();
	}

	/**
	 * Uses reflection to extract the identifier from a field of the same name.
	 */
	private String extractIdentifierFromField(Object o) {
		try {
			Field field = FieldUtils.getField(o.getClass(), FIELD_NAME, true);
			field.setAccessible(true);

			Object returnValue = field.get(o);

			if (returnValue != null) {
				return returnValue.toString();
			}
		} catch (SecurityException e) {
			LOGGER.trace("No Access to Field", e);
		} catch (IllegalArgumentException e) {
			LOGGER.trace("Bad Argument to Retrieve Field", e);
		} catch (IllegalAccessException e) {
			LOGGER.trace("Illegal Access to Field", e);
		}

		return null;
	}

	/**
	 * Uses reflection to gather the identifier from a getIdentifier() method on the class.
	 */
	private String extractIdentifierFromMethod(Object o) {
		try {
			Method method = MethodUtils.getMatchingMethod(o.getClass(), METHOD_NAME);
			method.setAccessible(true);

			Object returnValue = method.invoke(o);

			if (returnValue != null) {
				return returnValue.toString();
			}
		} catch (IllegalAccessException e) {
			LOGGER.debug("Illegal Access to Method", e);
		} catch (InvocationTargetException e) {
			LOGGER.debug("Failed to Invoke Method", e);
		}

		return null;
	}


}
