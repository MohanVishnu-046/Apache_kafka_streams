package streams.kafka.examples;

import streams.kafka.examples.types.Notification;
import streams.kafka.examples.types.PosInvoice;

import java.util.ArrayList;
import java.util.List;

class RecordBuilder {

    static Notification getNotification(PosInvoice invoice) {
         Notification notification =new Notification()
                .withInvoiceNumber(invoice.getInvoiceNumber())
                .withCustomerCardNo(invoice.getCustomerCardNo())
                .withTotalAmount(invoice.getTotalAmount())
                .withEarnedLoyaltyPoints(invoice.getTotalAmount() * AppConfigs.LOYALTY_FACTOR)
                .withTotalLoyaltyPoints(0.0);

        Double actualPoints= notification.getTotalLoyaltyPoints();
        Double totalPoints;

//try to implement map here instead of state-store;

        if(actualPoints!=null)
            totalPoints=actualPoints+notification.getEarnedLoyaltyPoints();
        else
            totalPoints=notification.getEarnedLoyaltyPoints();

        notification.setTotalLoyaltyPoints(totalPoints);

        return notification;
    }
}
