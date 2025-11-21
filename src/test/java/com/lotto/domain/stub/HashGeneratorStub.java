package com.lotto.domain.stub;

import com.lotto.domain.general.HashGenerable;

public class HashGeneratorStub implements HashGenerable {

    @Override
    public String generateHash() {
        return "hash-test";
    }
}
