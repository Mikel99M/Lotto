package com.lotto.infrastructure.numbergenerator.http;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class RandomNumberGeneratorProperties {

    private String uri;
    private int port;
}
