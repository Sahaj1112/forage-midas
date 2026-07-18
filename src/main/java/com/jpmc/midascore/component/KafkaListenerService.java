package com.jpmc.midascore.component;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaListenerService {

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        // This will print the values straight to your terminal log when the test runs!
        System.out.println("====== RECEIVED TRANSACTION: " + transaction.getAmount() + " ======");
    }
}
