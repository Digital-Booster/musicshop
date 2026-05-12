package net.digitalbooster.musicshop.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

@Converter(autoApply = true)
@Profile("sqlite") // This converter will only be active when the 'sqlite' profile is active
public class SqliteDateTimeConverter implements AttributeConverter<OffsetDateTime, String> {

    private static final Logger log = LoggerFactory.getLogger(SqliteDateTimeConverter.class);
    // Formatter for writing to database (without offset, with milliseconds)
    private static final DateTimeFormatter DB_WRITE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    // Formatter for parsing from database (with nanoseconds, no offset, 'T' separator)
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_NANOS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
    // Formatter for parsing from database (with milliseconds, no offset, 'T' separator)
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    // Formatter for parsing from database (without milliseconds/seconds, no offset, 'T' separator)
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    // Formatter for parsing from database (without offset, with space separator)
    private static final DateTimeFormatter SQL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SQL_DATE_TIME_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    @Override
    public String convertToDatabaseColumn(OffsetDateTime entityData) {
        if (entityData == null) {
            return null;
        }
        // Format to a string without offset, with milliseconds, suitable for SQLite
        return entityData.format(DB_WRITE_FORMATTER);
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String s = dbData.trim();
        if (s.isEmpty()) return null;

        ZoneId zone = ZoneId.systemDefault();

        // Attempt 1: Try parsing as ISO_OFFSET_DATE_TIME (if the DB somehow preserves offset)
        // This handles cases where toString() output (with offset) is stored and read directly
        try {
            return OffsetDateTime.parse(s);
        } catch (DateTimeParseException e) {
            // continue to other attempts
        }

        // Attempt 2: Try parsing as LocalDateTime with nanosecond precision (from DB_WRITE_FORMATTER's output or similar)
        try {
            return LocalDateTime.parse(s, ISO_LOCAL_DATE_TIME_NANOS).atZone(zone).toOffsetDateTime();
        } catch (DateTimeParseException e) {
            // continue
        }

        // Attempt 3: Try parsing as LocalDateTime with millisecond precision
        try {
            return LocalDateTime.parse(s, ISO_LOCAL_DATE_TIME_MS).atZone(zone).toOffsetDateTime();
        } catch (DateTimeParseException e) {
            // continue
        }

        // Attempt 4: Try parsing as LocalDateTime without fractional seconds
        try {
            return LocalDateTime.parse(s, ISO_LOCAL_DATE_TIME).atZone(zone).toOffsetDateTime();
        } catch (DateTimeParseException e) {
            // continue
        }

        // Attempt 5: Some SQLite DATETIME values may be stored as 'yyyy-MM-dd HH:mm:ss' (space instead of 'T')
        try {
            return LocalDateTime.parse(s, SQL_DATE_TIME).atZone(zone).toOffsetDateTime();
        } catch (DateTimeParseException e) {
            // try with milliseconds
        }

        // Attempt 6: ... with milliseconds
        try {
            return LocalDateTime.parse(s, SQL_DATE_TIME_MS).atZone(zone).toOffsetDateTime();
        } catch (DateTimeParseException e) {
            // continue
        }

        // Attempt 7: If the DB stores epoch seconds or millis as numeric string
        if (s.matches("^-?\\d+$")) {
            try {
                long v = Long.parseLong(s);
                // Heuristic: if value looks like milliseconds (>= 10^12) treat as millis
                if (Math.abs(v) > 1_000_000_000_000L) {
                    Instant inst = Instant.ofEpochMilli(v);
                    return OffsetDateTime.ofInstant(inst, zone);
                } else {
                    Instant inst = Instant.ofEpochSecond(v);
                    return OffsetDateTime.ofInstant(inst, zone);
                }
            } catch (NumberFormatException ex) {
                // fall through
            }
        }

        // Last resort: try parsing common ISO-like variants replacing space with 'T'
        // And then parse as LocalDateTime and convert to OffsetDateTime
        try {
            String t = s.replace(' ', 'T');
            // Check if it looks like an OffsetDateTime (contains + or Z)
            if (t.contains("+") || t.endsWith("Z")) {
                return OffsetDateTime.parse(t);
            } else {
                return LocalDateTime.parse(t).atZone(zone).toOffsetDateTime();
            }
        } catch (DateTimeParseException e) {
            log.warn("SqliteDateTimeConverter.convertToEntityAttribute: failed to parse datetime value '{}' - returning null", dbData);
            return null;
        }
    }
}