package br.com.spp.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

// Sem mock do PropertyClient: o contexto cria o cliente real, exercitando o
// bean RestClient.Builder (que o Boot 4 não auto-configura).
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AiServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
