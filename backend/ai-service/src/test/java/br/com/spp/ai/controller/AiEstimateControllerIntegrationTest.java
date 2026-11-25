package br.com.spp.ai.controller;

import br.com.spp.ai.TestcontainersConfiguration;
import br.com.spp.ai.client.PropertyClient;
import br.com.spp.ai.client.PropertyView;
import br.com.spp.ai.dto.AiEstimateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AiEstimateControllerIntegrationTest {

    private static final String BASE = "/api/v1/ai/estimates";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyClient propertyClient;

    private static RequestPostProcessor jwtOf(String subject) {
        return jwt().jwt(b -> b.subject(subject));
    }

    @Test
    void shouldEstimateAndEnforceOwnership() throws Exception {
        UUID propertyId = UUID.randomUUID();
        when(propertyClient.fetch(any(UUID.class), any(String.class)))
                .thenReturn(new PropertyView(propertyId, "APARTMENT", new BigDecimal("100"),
                        2, 1, 1, null, "São Paulo", "SP"));

        String body = objectMapper.writeValueAsString(new AiEstimateRequest(propertyId));

        String created = mockMvc.perform(post(BASE).with(jwtOf("maria@exemplo.com"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.estimatedValue").value(873300.00))
                .andExpect(jsonPath("$.confidence").value(0.8))
                .andExpect(jsonPath("$.modelVersion").value("heuristic-v1"))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(created).get("id").asString();

        mockMvc.perform(get(BASE).with(jwtOf("maria@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(get(BASE + "/" + id).with(jwtOf("joao@exemplo.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenPropertyIdMissing() throws Exception {
        mockMvc.perform(post(BASE).with(jwtOf("maria@exemplo.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
