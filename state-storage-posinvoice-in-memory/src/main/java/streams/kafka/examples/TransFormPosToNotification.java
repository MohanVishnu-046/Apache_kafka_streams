package streams.kafka.examples;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import streams.kafka.examples.types.Notification;
import streams.kafka.examples.types.PosInvoice;

public class TransFormPosToNotification implements ValueTransformer<PosInvoice, Notification> {
    private KeyValueStore<String,Double> stateStore;
    @Override
    public void init(ProcessorContext processorContext) {
        this.stateStore= (KeyValueStore<String, Double>) processorContext.getStateStore(AppConfigs.STATE_STORE_NAME);
    }

    @Override
    public Notification transform(PosInvoice posInvoice) {

        Notification notification=new Notification()
                .withInvoiceNumber(posInvoice.getInvoiceNumber())
                .withCustomerCardNo(posInvoice.getCustomerCardNo())
                .withTotalAmount(posInvoice.getTotalAmount())
                .withEarnedLoyaltyPoints(posInvoice.getTotalAmount()*AppConfigs.LOYALTY_FACTOR)
                .withTotalLoyaltyPoints(0.0);

        Double actualPoints= stateStore.get(notification.getCustomerCardNo());
        Double totalPoints;

        if(actualPoints!=null)
            totalPoints=actualPoints+notification.getEarnedLoyaltyPoints();
        else
            totalPoints=notification.getEarnedLoyaltyPoints();

        stateStore.put(notification.getCustomerCardNo(),totalPoints);
        notification.setTotalLoyaltyPoints(totalPoints);
        return notification;
    }

    @Override
    public void close() {

    }
}
