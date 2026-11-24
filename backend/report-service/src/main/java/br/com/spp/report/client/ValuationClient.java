package br.com.spp.report.client;

import br.com.spp.report.exception.ValuationNotFoundException;
import br.com.spp.report.exception.ValuationServiceUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

/**
 * Cliente HTTP para o valuation-service, propagando o JWT do usuário.
 */
@Component
public class ValuationClient {

    private final RestClient restClient;

    public ValuationClient(RestClient.Builder builder, ValuationClientProperties properties) {
        this.restClient = builder.baseUrl(properties.uri()).build();
    }

    public ValuationView fetch(UUID valuationId, String bearerToken) {
        try {
            return restClient.get()
                    .uri("/api/v1/valuations/{id}", valuationId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .body(ValuationView.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new ValuationNotFoundException(valuationId);
            }
            throw new ValuationServiceUnavailableException(e);
        } catch (RestClientException e) {
            throw new ValuationServiceUnavailableException(e);
        }
    }
}
