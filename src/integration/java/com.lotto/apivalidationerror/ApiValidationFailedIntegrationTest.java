package com.lotto.apivalidationerror;

import com.lotto.BaseIntegrationTest;
import com.lotto.infrastructure.apivalidation.ApiValidationErrorDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
public class ApiValidationFailedIntegrationTest extends BaseIntegrationTest {

    @Test
    public void should_return_400_bad_request_and_validation_message_when_request_does_not_have_input_numbers() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(post("/inputNumbers")
                .content("""
                        {
                        
                        }
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON)
        );

        // then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ApiValidationErrorDto result = objectMapper.readValue(json, ApiValidationErrorDto.class);
        assertThat(result.messages()).containsExactlyInAnyOrder(
                "inputNumbers must not be null",
                "inputNumbers must not be empty"
        );
    }

    @Test
    public void should_return_400_bad_request_and_validation_message_when_request_has_empty_input_numbers() throws Exception {
        // when & then
        mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {
                                "inputNumbers": []
                                }
                                """.trim()
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isNotEmpty());
    }

    @Test
    public void should_return_400_bad_request_when_input_numbers_are_out_of_range() throws Exception {
        // when & then
        mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {
                                "inputNumbers": [0, 100]
                                }
                                """.trim()
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isNotEmpty());
    }

    @Test
    public void should_return_400_bad_request_when_input_numbers_have_duplicates() throws Exception {
        // when & then
        mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {
                                "inputNumbers": [1,1,2,3,4,5]
                                }
                                """.trim()
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isNotEmpty());
    }

    @Test
    public void should_return_400_bad_request_when_input_numbers_size_is_too_small() throws Exception {
        // when & then
        mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {
                                "inputNumbers": [1,2]
                                }
                                """.trim()
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isNotEmpty());
    }

    @Test
    public void should_return_400_bad_request_when_input_numbers_size_is_too_large() throws Exception {
        // when & then
        mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {
                                "inputNumbers": [1,2,3,4,5,6,7]
                                }
                                """.trim()
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isNotEmpty());
    }

    @Test
    public void should_return_400_bad_request_when_json_is_malformed() throws Exception {
        // when & then
        mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {
                                "inputNumbers": [1,2,3
                                }
                                """.trim()
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
