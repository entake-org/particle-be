package io.sdsolutions.particle.core.dozer;

import org.modelmapper.AbstractConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class MapperConverters {

    public static AbstractConverter<String, Boolean> STRING_BOOLEAN = new AbstractConverter<>() {
        @Override
        protected Boolean convert(String source) {
            if (source != null) {
                if (source.toLowerCase().startsWith("y")) {
                    return Boolean.TRUE;
                } else if (source.toLowerCase().startsWith("n")) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }
    };

    public static AbstractConverter<Boolean, String> BOOLEAN_STRING = new AbstractConverter<>() {
        @Override
        protected String convert(Boolean source) {
            return source != null && (source) ? "Yes" : "No";
        }
    };

    public static AbstractConverter<LocalDateTime, Date> LOCALDATETIME_DATE = new AbstractConverter<>() {
        @Override
        protected Date convert(LocalDateTime source) {
            if (source == null) {
                return null;
            }

            return Date.from(source.toInstant(ZoneOffset.UTC));
        }
    };

    public static AbstractConverter<Date, LocalDateTime> DATE_LOCALDATETIME = new AbstractConverter<>() {
        @Override
        protected LocalDateTime convert(Date source) {
            if (source == null) {
                return null;
            }

            return Instant.ofEpochMilli(source.getTime()).atOffset(ZoneOffset.UTC).toLocalDateTime();
        }
    };

    public static AbstractConverter<String, String> TRIM_STRING = new AbstractConverter<>() {
        @Override
        protected String convert(String source) {
            return source != null ? source.trim() : null;
        }
    };

    public static AbstractConverter<Boolean, Byte> BOOLEAN_BYTE = new AbstractConverter<>() {
        @Override
        protected Byte convert(Boolean source) {
            if (source == null) {
                return 0;
            }

            return (byte) (source ? 1 : 0);
        }
    };

    public static AbstractConverter<LocalDateTime, OffsetDateTime> LOCALDATETIME_OFFSETDATETIME = new AbstractConverter<>() {
        @Override
        protected OffsetDateTime convert(LocalDateTime source) {
            if (source == null) {
                return null;
            }

            return source.atOffset(ZoneOffset.UTC);
        }
    };

    public static AbstractConverter<OffsetDateTime, LocalDateTime> OFFSETDATETIME_LOCALDATETIME = new AbstractConverter<>() {
        @Override
        protected LocalDateTime convert(OffsetDateTime source) {
            if (source == null) {
                return null;
            }

            return source.toLocalDateTime();
        }
    };

}
