package guru.learningjournal.kafka.examples;

import guru.learningjournal.kafka.examples.serde.AppSerdes;
import guru.learningjournal.kafka.examples.types.PosInvoice;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class POSUsingStreams {


    private static final Logger logger= LogManager.getLogger();

    public static void main(String[] args) {
        Properties pros=new Properties();
        pros.put(StreamsConfig.APPLICATION_ID_CONFIG,AppConfigs.applicationID);
        pros.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,AppConfigs.bootstrapServers);

        StreamsBuilder builder=new StreamsBuilder();

        KStream<String,PosInvoice> KS0 =builder.stream(AppConfigs.posTopicName,
                Consumed.with(Serdes.String(),AppSerdes.PosInvoice()));

        KS0.filter((k,v)->
                        //filtering for Home Delivery
                        v.getDeliveryType().equalsIgnoreCase(AppConfigs.DELIVERY_TYPE_HOME_DELIVERY))
                        //records send to shipment Topic
                        .to(AppConfigs.shipmentTopicName, Produced.with(AppSerdes.String(),AppSerdes.PosInvoice()));

        KS0.filter((k,v)->
                //filtering for prime customers
                v.getCustomerType().equalsIgnoreCase(AppConfigs.CUSTOMER_TYPE_PRIME))
                // mapping PosInvoice values to Notification keys.
                .mapValues(RecordBuilder::getNotification)
                //records send to notification Topic
                .to(AppConfigs.notificationTopic,Produced.with(AppSerdes.String(),AppSerdes.Notification()));

                //Masking Address and contact details.
        KS0.mapValues(RecordBuilder::getMaskedInvoice)
                //create records for trend analytics where list of items are divided into multiple items individual record
                .flatMapValues(RecordBuilder::getHadoopRecords)
                //records are send to Hadoop Topic for Batch Analysis.
                .to(AppConfigs.hadoopTopic,Produced.with(Serdes.String(),AppSerdes.HadoopRecord()));

        KafkaStreams streams = new KafkaStreams(builder.build(), pros);
            streams.start();
            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                logger.info("Stopping Streams....");
            }));
    }
}
