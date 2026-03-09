package com.lotto;

import com.lotto.domain.AdjustableClock;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Configuration
@Profile("integration")
@AllArgsConstructor
public class IntegrationConfiguration {

    private final ZoneId businessZone;

    @Bean
    @Primary
    Clock clock() {
        return AdjustableClock.ofLocalDateAndLocalTime(LocalDate.of(2025, 12, 14), LocalTime.of(19, 30), businessZone);
    }

}
