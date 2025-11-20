package com.app.demo.DTO.Request;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class FlexibleDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter LOCAL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter LOCAL_WITH_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText().trim();
        
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            // Intentar parsear como OffsetDateTime (con zona horaria)
            return OffsetDateTime.parse(dateString);
        } catch (DateTimeParseException e) {
            // Si falla, intentar parsear como LocalDateTime y agregar UTC
            try {
                LocalDateTime localDateTime;
                if (dateString.contains(".")) {
                    // Tiene milisegundos
                    localDateTime = LocalDateTime.parse(dateString, LOCAL_WITH_MILLIS);
                } else {
                    // Sin milisegundos
                    localDateTime = LocalDateTime.parse(dateString, LOCAL_FORMATTER);
                }
                // Convertir a OffsetDateTime con UTC
                return localDateTime.atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException e2) {
                throw new IOException("No se pudo parsear la fecha: " + dateString, e2);
            }
        }
    }
}

