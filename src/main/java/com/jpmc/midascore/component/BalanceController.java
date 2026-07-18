package com.jpmc.midascore.component;

import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jpmc.midascore.foundation.Balance;

@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        // 1. Query the database using the incoming userId
        // 2. If the user is found, wrap their balance inside a new Balance object
        // 3. If the user doesn't exist, return a Balance object initialized to 0
        return userRepository.findById(userId)
                .map(user -> new Balance(user.getBalance()))
                .orElseGet(() -> new Balance(0f));
    }
}