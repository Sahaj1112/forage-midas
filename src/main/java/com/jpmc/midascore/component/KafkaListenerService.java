package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(String message) {
        try {
            // Split the string by commas (e.g., "7, 3, 42.58")
            String[] tokens = message.split(",");
            if (tokens.length == 3) {
                long senderId = Long.parseLong(tokens[0].trim());
                long recipientId = Long.parseLong(tokens[1].trim());
                float amount = Float.parseFloat(tokens[2].trim());

                // Construct the Transaction object
                Transaction transaction = new Transaction(senderId, recipientId, amount);

                // Pass it to your service for database validation and updates
                transactionService.processTransaction(transaction);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse incoming transaction message: " + message);
            e.printStackTrace();
        }
    }
}