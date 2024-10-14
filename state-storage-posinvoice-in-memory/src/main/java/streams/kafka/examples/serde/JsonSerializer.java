package streams.kafka.examples.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper mapper=new ObjectMapper();
    @Override
    public byte[] serialize(String topic, Object data) {
        if(data==null)
            return null;
        try{
            return mapper.writeValueAsBytes(data);
        }catch (Exception e){
            throw new SerializationException("Error occur in serializing the Json Data"+e);
        }
    }
}
