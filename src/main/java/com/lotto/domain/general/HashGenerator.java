package com.lotto.domain.general;

import java.util.UUID;

public class HashGenerator implements HashGenerable {

    @Override
    public String generateHash() {
        return UUID.randomUUID().toString();
    }

}
