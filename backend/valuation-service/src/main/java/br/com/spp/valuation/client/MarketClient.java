package br.com.spp.valuation.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Cliente HTTP para o market-service, propagando o JWT do usuário.
 * A ausência de preço de referência (404) ou indisponibilidade NÃO quebra a
 * avaliação: retorna vazio e o motor cai no preço base de configuração.
 */
@Component
public class MarketClient {

    private static final Logger log = LoggerFactory.getLogger(MarketClient.class);

    private final RestClient restClient;

    public MarketClient(RestClient.Builder builder, MarketClientProperties properties) {
        this.restClient = builder.baseUrl(properties.uri()).build();
    }

    public Optional<BigDecimal> fetchRate(String city, String state, String propertyType, String bearerToken) {
        try {
            RateView rate = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/market-references/rate")
                            .queryParam("city", city)
                            .queryParam("state", state)
                            .queryParam("propertyType", propertyType)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .body(RateView.class);
            return Optional.ofNullable(rate).map(RateView::pricePerSqm);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            log.warn("market-service respondeu {} ao consultar a taxa; usando preço base", e.getStatusCode());
            return Optional.empty();
        } catch (RestClientException e) {
            log.warn("market-service indisponível ao consultar a taxa; usando preço base", e);
            return Optional.empty();
        }
    }
}
