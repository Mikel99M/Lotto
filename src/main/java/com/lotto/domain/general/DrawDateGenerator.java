package com.lotto.domain.general;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class DrawDateGenerator {

    static final DayOfWeek DRAW_DAY = DayOfWeek.SATURDAY;
    static final int DRAW_HOUR = 20;

    public LocalDateTime generateNextDrawDate(LocalDateTime now) {

        LocalDateTime drawDate = now.with(DRAW_DAY);

        LocalDateTime nextDrawDate = now.with(TemporalAdjusters.next(DRAW_DAY));

        return nextDrawDate
                .withHour(DRAW_HOUR)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public LocalDateTime convertToDateTime(final LocalDate drawDate) {
        return LocalDateTime.of(drawDate, LocalTime.of(DRAW_HOUR, 0));
    }

}
