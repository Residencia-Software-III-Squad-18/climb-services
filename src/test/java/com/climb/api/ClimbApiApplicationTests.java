package com.climb.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Requer datasource real; os testes focados de auth cobrem o fluxo validado nesta suíte.")
@SpringBootTest
@ActiveProfiles("test")
class ClimbApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
