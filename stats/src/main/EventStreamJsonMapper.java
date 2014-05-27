import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import exceptions.StatsException;

import java.io.IOException;

public class EventStreamJsonMapper<T> implements rx.functions.Func1<byte[], T> {

    final ObjectMapper objectMapper;
    final ObjectReader objectReader;

    public EventStreamJsonMapper(Class<T> clazz) {
        this.objectMapper = new ObjectMapper();
        this.objectReader = objectMapper.reader(clazz);
    }

    @Override
    public T call(byte[] bytes) {
        try {
            String data = new String(bytes).replace("data: ", "");
            return objectReader.readValue(data);
        } catch (IOException e) {
            throw new StatsException("kunne ikke parse Stats-json", e);
        }
    }
}
