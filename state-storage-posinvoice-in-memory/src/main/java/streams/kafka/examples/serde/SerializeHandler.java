package streams.kafka.examples.serde;


import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import streams.kafka.examples.JsonDeSerializer;
import streams.kafka.examples.types.Notification;
import streams.kafka.examples.types.PosInvoice;

import java.util.HashMap;
import java.util.Map;

public class SerializeHandler extends Serdes {

    static public final class PosInvoiceSerde extends WrapperSerde<PosInvoice> {
        public PosInvoiceSerde() {
            super(new JsonSerializer<>(), new JsonDeSerializer<>());
        }
    }
    static public Serde<PosInvoice> PosInvoice() {
        PosInvoiceSerde serde=new PosInvoiceSerde();
        Map<String,Object> serdeConfig=new HashMap<>();
        serdeConfig.put(JsonDeSerializer.KEY_CLASS_NAME_CONFIG,PosInvoice.class.getName());
        serde.configure(serdeConfig,false);
        return serde;
    }
    static public final class NotificationSerde extends WrapperSerde<Notification> {
        public NotificationSerde() {
            super(new JsonSerializer<>(), new JsonDeSerializer<>());
        }
    }
    static public Serde<Notification> Notification() {
        NotificationSerde serde=new NotificationSerde();
        Map<String,Object> serdeConfig=new HashMap<>();
        serdeConfig.put(JsonDeSerializer.KEY_CLASS_NAME_CONFIG,PosInvoice.class.getName());
        serde.configure(serdeConfig,false);
        return serde;
    }
}
