package ro.eventsourcing.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class EventSerializer {
    public static final ObjectMapper mapper =
            new JsonMapper()
                    .registerModule(new JavaTimeModule())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static EventPayload serialize(Object event) {
        try {
            return new EventPayload(
                    event.getClass().getName(),
                    mapper.writeValueAsString(event)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static Object deserialize(EventPayload event) {
        try {
            Class<?> aClass = Class.forName(event.name());
            return mapper.readValue(event.data(), aClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
