package io.entake.particle.audit.aspect;

import io.entake.particle.audit.annotation.Auditable;
import io.entake.particle.audit.service.AuditLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * The intent of this aspect is to capture all Audit Events generated in a given system. Any RESTful endpoint in the
 * system simply needs to have the @Auditable annotation applied to it for data to be captured. The expectation of this
 * aspect is that the returned object from the endpoint contains an identifier field or a getIdentifier() method. This
 * will allow us to tag the event generated for that particular individual or organization. Furthermore, the annotation
 * should specify the type of event, that way we can accurately tag the event for later reporting.
 * 
 * @author Nick DiMola
 * 
 */
@Aspect
@Component
public class AuditAspect {

	private final AuditLogService auditLogService;

	public AuditAspect(AuditLogService auditLogService) {
		this.auditLogService = auditLogService;
	}

	/**
	 * This method will be invoked automagically by Spring whenever the @Auditable annotation is applied to a RESTful
	 * endpoint.
	 * 
	 * @param pjp JoinPoint
	 * @param auditable Audit Annotation
	 * @return The proceeding object to continue aspect execution
	 * @throws Throwable
	 */
	@Around(value = "@annotation(auditable)", argNames = "auditable")
	public Object aroundAdvice(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
		Object o = pjp.proceed();

		auditLogService.generateLogs(o, auditable.type().name(), auditable.app(), auditable.function());

		return o;
	}
}
