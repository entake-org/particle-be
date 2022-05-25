package io.sdsolutions.particle.core.dozer;

import com.github.dozermapper.core.DozerConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class LocalDateToUtilDateConverter extends DozerConverter<LocalDateTime, Date> {

    public LocalDateToUtilDateConverter() {
        super(LocalDateTime.class, Date.class);
    }

    @Override
    public Date convertTo(LocalDateTime source, Date destination) {
        if (source == null) {
            return null;
        }

        return Date.from(source.toInstant(ZoneOffset.UTC));
    }

    @Override
    public LocalDateTime convertFrom(Date source, LocalDateTime destination) {
        if (source == null) {
            return null;
        }

        return Instant.ofEpochMilli(source.getTime()).atOffset(ZoneOffset.UTC).toLocalDateTime();
    }

}
