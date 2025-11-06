package net.digitalbooster.musicshop.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter(autoApply = true)
public class SqliteDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final Logger log = LoggerFactory.getLogger(SqliteDateTimeConverter.class);
    private static final DateTimeFormatter SQL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SQL_DATE_TIME_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toString() : null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String s = dbData.trim();
        if (s.isEmpty()) return null;

        // Try ISO format first (e.g. 2020-01-01T12:34:56)
        try {
            return LocalDateTime.parse(s);
        } catch (DateTimeParseException e) {
            // continue to other attempts
        }

        // Some SQLite DATETIME values may be stored as 'yyyy-MM-dd HH:mm:ss' (space instead of 'T')
        try {
            return LocalDateTime.parse(s, SQL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // try with milliseconds
        }

        try {
            return LocalDateTime.parse(s, SQL_DATE_TIME_MS);
        } catch (DateTimeParseException e) {
            // continue
        }

        // If the DB stores epoch seconds or millis as numeric string
        if (s.matches("^-?\\d+$")) {
            try {
                long v = Long.parseLong(s);
                // Heuristic: if value looks like milliseconds (>= 10^12) treat as millis
                ZoneId zone = ZoneId.systemDefault();
                if (Math.abs(v) > 1_000_000_000_000L) {
                    Instant inst = Instant.ofEpochMilli(v);
                    return LocalDateTime.ofInstant(inst, zone);
                } else {
                    Instant inst = Instant.ofEpochSecond(v);
                    return LocalDateTime.ofInstant(inst, zone);
                }
            } catch (NumberFormatException ex) {
                // fall through
            }
        }

        // Last resort: try parsing common ISO-like variants replacing space with 'T'
        try {
            String t = s.replace(' ', 'T');
            return LocalDateTime.parse(t);
        } catch (DateTimeParseException e) {
            log.warn("SqliteDateTimeConverter: failed to parse datetime value '{}' - returning null", dbData);
            return null;
        }
    }
}