package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    @Transactional
    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender == null || recipient == null) {
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            return;
        }

        // Fetch Incentive from External API
        float incentiveAmount = 0.0f;
        try {
            Incentive response = restTemplate.postForObject(INCENTIVE_API_URL, transaction, Incentive.class);
            if (response != null) {
                incentiveAmount = response.getAmount();
            }
        } catch (Exception e) {
            // Log fallback or handling if the external microservice behaves unexpectedly
            System.err.println("Incentive API error: " + e.getMessage());
        }

        // Adjust balances: Incentive goes to recipient only, NOT deducted from sender
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        // Record with incentive
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
        transactionRepository.save(record);
    }
}