package com.lotto.domain.general;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HashGenerator implements HashGenerable {

    @Override
    public String generateHash() {
        return UUID.randomUUID().toString();
    }

}
