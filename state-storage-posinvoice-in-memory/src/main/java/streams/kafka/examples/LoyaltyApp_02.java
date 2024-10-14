package streams.kafka.examples;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import streams.kafka.examples.serde.SerializeHandler;
import streams.kafka.examples.types.PosInvoice;

import java.util.Properties;

public class LoyaltyApp_02 {
    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties streamConfig = new Properties();
        streamConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        streamConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);

        StreamsBuilder builder = new StreamsBuilder();

//  consuming record from the topic
        KStream<String, PosInvoice> KS0 = builder.stream(
                AppConfigs.posTopicName,
                Consumed.with(SerializeHandler.String(), SerializeHandler.PosInvoice()))
//  filtering the prime customers
                .filter((k, v) ->
                v.getCustomerType().equalsIgnoreCase(AppConfigs.CUSTOMER_TYPE_PRIME));

    //  building the KeyValueInMemoryStateStore by passing the store name, key and value type.
//        StoreBuilder storeBuilder = Stores.keyValueStoreBuilder(
//                Stores.inMemoryKeyValueStore(AppConfigs.STATE_STORE_NAME),
//                SerializeHandler.String(), SerializeHandler.Double()
//        );
    //  add the StoreBuilder to StreamBuilder
//        builder.addStateStore(storeBuilder);

//  filtered prime customer stream is produced and repartitioned using custom partitioning.
        KS0.through(AppConfigs.TEMP_TOPIC_STATE_STORE_NAME,
                        Produced.with(SerializeHandler.String(),SerializeHandler.PosInvoice(),new TransFormPartitioner()))

//  repartitioned stream is transformed to notification using  KeyValueInMemoryStateStore
                .transformValues(TransFormPosToNotification::new, AppConfigs.STATE_STORE_NAME)
//                .mapValues()
                .to(AppConfigs.notificationTopic,
                        Produced.with(SerializeHandler.String(), SerializeHandler.Notification()));

//  Topology topology=builder.build();
        KafkaStreams streams = new KafkaStreams(builder.build(), streamConfig);
//  shutdownHook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("kafka Streams is stopped.....");
                streams.cleanUp();
            }));
    }
}
