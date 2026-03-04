package com.lotto;

import com.lotto.domain.AdjustableClock;
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
public class IntegrationConfiguration {

    @Bean
    @Primary
    Clock clock() {
        return AdjustableClock.ofLocalDateAndLocalTime(LocalDate.of(2025, 12, 14), LocalTime.of(19, 30), ZoneId.systemDefault());
    }

}
