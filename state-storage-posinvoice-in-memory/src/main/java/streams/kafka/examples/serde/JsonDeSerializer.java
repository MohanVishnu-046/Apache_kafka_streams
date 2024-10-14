package streams.kafka.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeSerializer<T> implements Deserializer<T> {
    private final ObjectMapper mapper = new ObjectMapper();

    private Class<T> ClassName;
    public static final String KEY_CLASS_NAME_CONFIG = "key.class.name";
    private static final String VALUE_CLASS_NAME_CONFIG = "Value.class.name";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (isKey)
            ClassName = (Class<T>) configs.get(KEY_CLASS_NAME_CONFIG);
        else
            ClassName = (Class<T>) configs.get(VALUE_CLASS_NAME_CONFIG);

    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null)
            return null;
        try {
            return mapper.readValue(data, ClassName);
        } catch (Exception e) {
            throw new SerializationException("Error occur in Deserializing the JsonData" + e);
        }
    }
}
