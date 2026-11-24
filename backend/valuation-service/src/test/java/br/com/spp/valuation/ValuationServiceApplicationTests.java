package br.com.spp.valuation;

import br.com.spp.valuation.client.PropertyClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ValuationServiceApplicationTests {

	@MockitoBean
	private PropertyClient propertyClient;

	@Test
	void contextLoads() {
	}

}
