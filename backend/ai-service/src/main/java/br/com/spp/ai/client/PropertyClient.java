package br.com.spp.ai.client;

import br.com.spp.ai.exception.PropertyNotFoundException;
import br.com.spp.ai.exception.PropertyServiceUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

/**
 * Cliente HTTP para o property-service, propagando o JWT do usuário.
 */
@Component
public class PropertyClient {

    private final RestClient restClient;

    public PropertyClient(RestClient.Builder builder, PropertyClientProperties properties) {
        this.restClient = builder.baseUrl(properties.uri()).build();
    }

    public PropertyView fetch(UUID propertyId, String bearerToken) {
        try {
            return restClient.get()
                    .uri("/api/v1/properties/{id}", propertyId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .body(PropertyView.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new PropertyNotFoundException(propertyId);
            }
            throw new PropertyServiceUnavailableException(e);
        } catch (RestClientException e) {
            throw new PropertyServiceUnavailableException(e);
        }
    }
}
