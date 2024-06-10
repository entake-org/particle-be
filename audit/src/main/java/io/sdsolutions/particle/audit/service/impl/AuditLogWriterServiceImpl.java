package io.sdsolutions.particle.audit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sdsolutions.particle.audit.model.AuditEventDTO;
import io.sdsolutions.particle.audit.service.AuditLogWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("auditLogWriterService")
public class AuditLogWriterServiceImpl implements AuditLogWriterService {

    private static final String AUDIT_LOGGER_NAME = "AuditLogger";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogWriterServiceImpl.class);
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger(AUDIT_LOGGER_NAME);

    private final ObjectMapper objectMapper;

    public AuditLogWriterServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void writeAuditLogs(List<AuditEventDTO> events) {
        for (AuditEventDTO event : events) {
            try {
                String eventLog = objectMapper.writeValueAsString(event);
                AUDIT_LOGGER.info(eventLog);
            } catch (JsonProcessingException e) {
                LOGGER.error("Failure in converting JSON", e);
            }
        }

        LOGGER.info("Audit Logs Recorded.");
    }
}
