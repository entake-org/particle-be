package io.entake.particle.audit.service;

import io.entake.particle.audit.model.AuditEventDTO;

import java.util.List;

public interface AuditLogWriterService {

    void writeAuditLogs(List<AuditEventDTO> events);

}
