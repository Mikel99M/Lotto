package com.lotto.redis;

import com.lotto.BaseIntegrationTest;
import com.lotto.LottoApplication;
import com.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = LottoApplication.class, properties = {"spring.cache.type=redis"})
public class RedisLottoCacheIntegrationTest extends BaseIntegrationTest {

    @Container
    private static final GenericContainer<?> REDIS;

    static {
        REDIS = new GenericContainer<>("redis").withExposedPorts(6379);
        REDIS.start();
    }

    @SpyBean
    WinningNumbersGeneratorFacade facade;
    @Autowired
    CacheManager cacheManager;

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("lotto.number-generator.http.client.config.port", () -> wireMockServer.getPort());
        registry.add("lotto.number-generator.http.client.config.uri", () -> WIRE_MOCK_HOST);
        registry.add("spring.redis.port", REDIS::getFirstMappedPort);
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.cache.redis.time-to-live", () -> "PT1S");
    }

    @Test
    public void should_save__most_recent_WinningNumbersDto_to_cache_and_then_invalidate_by_time_to_live() throws Exception {
        //step 1: user made POST /register with username=someUser, password=somePassword and system registered user with status CREATED(201)
        // when & then
        registerTestUser();

        //step 2: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC
        // given & when
        String token = loginInTestUserAndGetToken();

        // step 3: should save to cache result request
        // given && when
        performGetActionWithToken("/result/recent", token);

        // then
        verify(facade, times(1)).retrieveMostRecentWinningNumbersDto();
        assertThat(cacheManager.getCacheNames().contains("recentWinningNumbers")).isTrue();

        // step 4: cache should be invalidated
        // given && when && then
        await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                            performGetActionWithToken("/result/recent", token);
                            verify(facade, atLeast(2)).retrieveMostRecentWinningNumbersDto();
                        }
                );
    }
}