package br.com.spp.property.controller;

import br.com.spp.property.TestcontainersConfiguration;
import br.com.spp.property.dto.PropertyRequest;
import br.com.spp.property.entity.PropertyType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class PropertyControllerIntegrationTest {

    private static final String BASE = "/api/v1/properties";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private PropertyRequest sample() {
        return new PropertyRequest("Apartamento Centro", PropertyType.APARTMENT, "Rua das Flores", "200",
                "Apto 51", "Centro", "São Paulo", "SP", "01000-000",
                new BigDecimal("75.50"), 2, 1, 1, 2015, new BigDecimal("450000"));
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor jwtOf(String subject) {
        return jwt().jwt(builder -> builder.subject(subject));
    }

    @Test
    void shouldCreateListAndEnforceOwnership() throws Exception {
        String body = objectMapper.writeValueAsString(sample());

        String created = mockMvc.perform(post(BASE).with(jwtOf("maria@exemplo.com"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.ownerSubject").value("maria@exemplo.com"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(created).get("id").asString();

        // dono lista e vê o imóvel
        mockMvc.perform(get(BASE).with(jwtOf("maria@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id));

        // dono busca por id
        mockMvc.perform(get(BASE + "/" + id).with(jwtOf("maria@exemplo.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("São Paulo"));

        // outro usuário NÃO acessa o imóvel alheio -> 404
        mockMvc.perform(get(BASE + "/" + id).with(jwtOf("joao@exemplo.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestOnInvalidPayload() throws Exception {
        PropertyRequest invalid = new PropertyRequest("", null, "", null, null, null, "", "SPP", "",
                new BigDecimal("-1"), -1, 0, 0, null, null);

        mockMvc.perform(post(BASE).with(jwtOf("maria@exemplo.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
