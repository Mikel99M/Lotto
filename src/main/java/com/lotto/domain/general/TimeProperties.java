package com.lotto.domain.general;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;

@ConfigurationProperties(prefix = "lotto.time")
@Setter
@Getter
public class TimeProperties {

    ZoneId timeZone;

}


