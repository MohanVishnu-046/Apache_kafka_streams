package streams.kafka.examples;

import org.apache.kafka.streams.processor.StreamPartitioner;
import streams.kafka.examples.types.PosInvoice;

public class TransFormPartitioner implements StreamPartitioner<String, PosInvoice> {
    @Override
    public Integer partition(String topic, String key, PosInvoice value, int numPartitions) {
        return value.getCustomerCardNo().hashCode()%numPartitions;
    }
}
