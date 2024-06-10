package io.sdsolutions.particle.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that will trigger the auditing aspect to kick into gear. Users should provide the type of event, the
 * application it's generated from and the specific function that triggered the event.
 *
 * @author ndimola
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

	AuditableType type();

	String app();

	String function() default "All";
}
