package io.sdsolutions.particle.audit.service;

public interface AuditLogService {

	void generateLogs(Object o, String eventType, String app, String function);

}
