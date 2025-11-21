package com.lotto.domain.stub;

import com.lotto.domain.general.DrawDateGenerator;

import java.time.LocalDateTime;

public class DrawDateGeneratorStub extends DrawDateGenerator {

    @Override
    public LocalDateTime generateNextDrawDate(final LocalDateTime now) {
        return LocalDateTime.of(2025, 11, 15, 20, 0);
    }
}
