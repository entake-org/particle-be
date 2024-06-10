package io.sdsolutions.particle.audit.service;

import io.sdsolutions.particle.audit.model.AuditEventDTO;

import java.util.List;

public interface AuditLogWriterService {

    void writeAuditLogs(List<AuditEventDTO> events);

}
