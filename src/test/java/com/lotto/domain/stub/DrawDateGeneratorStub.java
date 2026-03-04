package com.lotto.domain.stub;

import com.lotto.domain.general.DrawDateGenerator;
import org.junit.Before;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DrawDateGeneratorStub extends DrawDateGenerator {

    private final ZoneId zoneId;
    private ZonedDateTime zonedDateTime = ZonedDateTime.of(2025, 11, 15, 20, 0, 0, 0, ZoneId.of("Europe/Warsaw"));;
    private Instant nextDrawDate = zonedDateTime.toInstant();

    public DrawDateGeneratorStub(final ZoneId businessZone) {
        super(businessZone);
        zoneId = businessZone;
    }

    @Override
    public Instant generateNextDrawDate(Instant now) {
        return nextDrawDate;
    }

    public void setNextDrawDate(final Instant nextDrawDate) {
        this.nextDrawDate = nextDrawDate;
    }
}
