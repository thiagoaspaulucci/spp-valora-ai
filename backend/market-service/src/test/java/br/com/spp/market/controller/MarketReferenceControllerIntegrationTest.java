package br.com.spp.market.controller;

import br.com.spp.market.TestcontainersConfiguration;
import br.com.spp.market.dto.MarketReferenceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class MarketReferenceControllerIntegrationTest {

    private static final String BASE = "/api/v1/market-references";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static RequestPostProcessor admin() {
        return jwt().jwt(b -> b.subject("admin@exemplo.com"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private static RequestPostProcessor user() {
        return jwt().jwt(b -> b.subject("maria@exemplo.com"))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private String sampleBody() throws Exception {
        return objectMapper.writeValueAsString(new MarketReferenceRequest(
                "São Paulo", "SP", "APARTMENT", new BigDecimal("9000.00"),
                LocalDate.of(2026, 10, 1), "internal"));
    }

    @Test
    void adminCanCreateAndAnyoneCanQueryRate() throws Exception {
        mockMvc.perform(post(BASE).with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(sampleBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdBy").value("admin@exemplo.com"));

        // usuário comum consulta a taxa vigente
        mockMvc.perform(get(BASE + "/rate").with(user())
                        .param("city", "São Paulo").param("state", "SP").param("propertyType", "APARTMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pricePerSqm").value(9000.00));
    }

    @Test
    void nonAdminCannotCreate() throws Exception {
        mockMvc.perform(post(BASE).with(user())
                        .contentType(MediaType.APPLICATION_JSON).content(sampleBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedIsRejected() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rateReturnsNotFoundWhenNoReference() throws Exception {
        mockMvc.perform(get(BASE + "/rate").with(user())
                        .param("city", "Curitiba").param("state", "PR").param("propertyType", "HOUSE"))
                .andExpect(status().isNotFound());
    }
}
