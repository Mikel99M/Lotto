package com.lotto.domain.general;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;

@ConfigurationProperties(prefix = "lotto.time")
@Getter
public class TimeProperties {

    ZoneId timeZone;

}


