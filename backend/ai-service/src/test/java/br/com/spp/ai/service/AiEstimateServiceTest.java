package br.com.spp.ai.service;

import br.com.spp.ai.client.PropertyClient;
import br.com.spp.ai.client.PropertyView;
import br.com.spp.ai.dto.AiEstimateRequest;
import br.com.spp.ai.dto.AiEstimateResponse;
import br.com.spp.ai.entity.AiEstimate;
import br.com.spp.ai.exception.AiEstimateNotFoundException;
import br.com.spp.ai.mapper.AiEstimateMapper;
import br.com.spp.ai.model.AiModelProperties;
import br.com.spp.ai.model.AiValuationModel;
import br.com.spp.ai.repository.AiEstimateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiEstimateServiceTest {

    @Mock
    private AiEstimateRepository repository;
    @Mock
    private PropertyClient propertyClient;

    private final AiValuationModel model = new AiValuationModel(new AiModelProperties(
            "heuristic-v1", new BigDecimal("8200"), Map.of("APARTMENT", new BigDecimal("1.00"))));
    private final AiEstimateMapper mapper = new AiEstimateMapper();

    private AiEstimateService service() {
        return new AiEstimateService(repository, propertyClient, model, mapper);
    }

    @Test
    void createShouldFetchPropertyRunModelAndPersist() {
        UUID propertyId = UUID.randomUUID();
        PropertyView property = new PropertyView(propertyId, "APARTMENT", new BigDecimal("100"),
                2, 1, 1, null, "São Paulo", "SP");
        when(propertyClient.fetch(propertyId, "token-123")).thenReturn(property);
        when(repository.save(any(AiEstimate.class))).thenAnswer(inv -> {
            AiEstimate e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        AiEstimateResponse response = service()
                .create(new AiEstimateRequest(propertyId), "maria@exemplo.com", "token-123");

        assertThat(response.propertyId()).isEqualTo(propertyId);
        assertThat(response.estimatedValue()).isEqualByComparingTo("873300.00");
        assertThat(response.confidence()).isEqualByComparingTo("0.800");
        assertThat(response.modelVersion()).isEqualTo("heuristic-v1");
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndRequestedBy(id, "maria@exemplo.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(id, "maria@exemplo.com"))
                .isInstanceOf(AiEstimateNotFoundException.class);
    }
}
