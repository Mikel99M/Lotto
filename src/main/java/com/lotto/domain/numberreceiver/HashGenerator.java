package com.lotto.domain.numberreceiver;

import java.util.UUID;

class HashGenerator implements HashGenerable {

    @Override
    public String generateHash() {
        return UUID.randomUUID().toString();
    }

}
