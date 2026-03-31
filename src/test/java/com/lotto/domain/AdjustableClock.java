package com.lotto.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AdjustableClock extends Clock {

    private final ZoneId zone;
    protected Instant instant;

    public AdjustableClock(Instant initialInstant, ZoneId zone) {
        this.instant = initialInstant;
        this.zone = zone;
    }

    public static AdjustableClock ofLocalDateAndLocalTime(LocalDate date, LocalTime time, ZoneId zone) {
        ZonedDateTime zoneDateTime = createZoneDateTime(date, time, zone);
        return new AdjustableClock(zoneDateTime.toInstant(), zone);
    }

    protected static ZonedDateTime createZoneDateTime(LocalDate date, LocalTime time, ZoneId zone) {
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                time.getHour(), time.getMinute(), time.getSecond(), time.getNano(), zone);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        if (zone.equals(this.zone)) {
            return this;
        }
        return new AdjustableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void advanceInTimeBy(Duration clockOffset) {
        this.instant = instant.plus(clockOffset);
    }

    public void plusDaysAndMinutes(int days, int minutes) {
        Duration offset = Duration.ofDays(days);
        advanceInTimeBy(offset);
        Duration ofMinutes = Duration.ofMinutes(minutes);
        advanceInTimeBy(ofMinutes);
    }
}