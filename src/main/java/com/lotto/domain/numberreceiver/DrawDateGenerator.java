package com.lotto.domain.numberreceiver;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

class DrawDateGenerator {

    static DayOfWeek dayOfWeek = DayOfWeek.SATURDAY;
    static int drawHour = 20;

    public static LocalDateTime generateNextDrawDate(LocalDateTime now) {

        if (isSaturday(now)) {
            if (!isAfterHour(now)) {
                return now.withHour(drawHour).withMinute(0).withSecond(0).withNano(0);
            }
        }

        LocalDateTime nextDrawDate = now.with(TemporalAdjusters.next(dayOfWeek));
        return nextDrawDate.withHour(drawHour).withMinute(0).withSecond(0).withNano(0);
    }

    private static Boolean isSaturday(LocalDateTime date) {
        return date.getDayOfWeek() == dayOfWeek;
    }

    private static Boolean isAfterHour(LocalDateTime date) {
        return date.getHour() >= drawHour;
    }
}
