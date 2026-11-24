package br.com.spp.report;

import br.com.spp.report.client.ValuationClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ReportServiceApplicationTests {

	@MockitoBean
	private ValuationClient valuationClient;

	@Test
	void contextLoads() {
	}

}
