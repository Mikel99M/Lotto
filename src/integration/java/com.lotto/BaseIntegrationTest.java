package com.lotto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {LottoApplication.class, IntegrationConfiguration.class}, properties = "spring.task.scheduling.enabled: false")
@ActiveProfiles({"integration"})
@AutoConfigureMockMvc
@Testcontainers
public class BaseIntegrationTest {

    public static final String WIRE_MOCK_HOST = "http://localhost";
    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ObjectMapper objectMapper;

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("lotto.number-generator.http.client.config.port", () -> wireMockServer.getPort());
        registry.add("lotto.number-generator.http.client.config.uri", () -> WIRE_MOCK_HOST);
    }

    public ResultActions performGetActionWithToken(String url, String token) throws Exception {
        return mockMvc.perform(get(url)
                .header("Authorization", "Bearer " + token));
    }

    public void registerTestUser() throws Exception {
        mockMvc.perform(post("/register")
                        .content("""
                                {
                                "username": "someUser",
                                "password": "somePassword"
                                }
                                """.trim())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("someUser"))
                .andExpect(jsonPath("$.created").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    public String loginInTestUserAndGetToken() throws Exception {
        ResultActions successLoginRequest = mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
        );

        String loginResponseJson = successLoginRequest
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("someUser"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(loginResponseJson, "$.token");
    }
}
