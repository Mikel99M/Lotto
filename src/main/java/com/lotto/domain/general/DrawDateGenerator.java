package com.lotto.domain.general;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

@Component
@AllArgsConstructor
public class DrawDateGenerator {

    static final DayOfWeek DRAW_DAY = DayOfWeek.SATURDAY;
    static final int DRAW_HOUR = 20;

    private final ZoneId businessZone;

    public Instant generateNextDrawDate(Instant nowIntant) {

        ZonedDateTime now = nowIntant.atZone(businessZone);

        ZonedDateTime nextDraw = now
                .with(TemporalAdjusters.nextOrSame(DRAW_DAY))
                .withHour(DRAW_HOUR)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        if (now.isAfter(nextDraw)) {
            nextDraw = nextDraw.plusWeeks(1);
        }

        return nextDraw.toInstant();
    }

}
