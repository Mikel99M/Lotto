package com.lotto.domain.general;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    @Bean
    public ZoneId businessZone(TimeProperties timeProperties) {
        return ZoneId.of(String.valueOf(timeProperties.getTimeZone()));
    }

    @Bean
    public Clock clock(ZoneId businessZone) {
        return Clock.system(businessZone);
    }
}
