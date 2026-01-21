package com.lotto.infrastructure.numbergenerator.http;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lotto.number-generator.http.client.config")
@Builder
public record RandomGeneratorClientConfigProperties(
        long connectionTimeout,
        long readTimeout,
        String uri,
        int port
) {
}
