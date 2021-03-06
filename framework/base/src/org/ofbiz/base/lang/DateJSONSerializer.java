package org.ofbiz.base.lang;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by galaxypan on 16.8.6.
 */
public class DateJSONSerializer extends JsonSerializer<Date> {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public void serialize(Date timestamp, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(format.format(timestamp));
    }
}
