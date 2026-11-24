package br.com.spp.report.controller;

import br.com.spp.report.TestcontainersConfiguration;
import br.com.spp.report.client.ValuationClient;
import br.com.spp.report.client.ValuationView;
import br.com.spp.report.dto.ReportRequest;
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
class ReportControllerIntegrationTest {

    private static final String BASE = "/api/v1/reports";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ValuationClient valuationClient;

    private static RequestPostProcessor jwtOf(String subject) {
        return jwt().jwt(builder -> builder.subject(subject));
    }

    @Test
    void shouldGenerateReportAndEnforceOwnership() throws Exception {
        UUID valuationId = UUID.randomUUID();
        when(valuationClient.fetch(any(UUID.class), any(String.class)))
                .thenReturn(new ValuationView(valuationId, UUID.randomUUID(),
                        new BigDecimal("836000.00"), new BigDecimal("8000.00"), new BigDecimal("100.00"),
                        "APARTMENT", "São Paulo", "SP", "SIMPLE_FORMULA"));

        String body = objectMapper.writeValueAsString(new ReportRequest(valuationId));

        String created = mockMvc.perform(post(BASE).with(jwtOf("maria@exemplo.com"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.estimatedValue").value(836000.00))
                .andExpect(jsonPath("$.title").value("Laudo de Avaliação — APARTMENT em São Paulo/SP"))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(created).get("id").asString();

        mockMvc.perform(get(BASE).with(jwtOf("maria@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(get(BASE + "/" + id).with(jwtOf("maria@exemplo.com")))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE + "/" + id).with(jwtOf("joao@exemplo.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenValuationIdMissing() throws Exception {
        mockMvc.perform(post(BASE).with(jwtOf("maria@exemplo.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
