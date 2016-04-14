package next.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import next.config.ApplicationConfig;
import next.controller.HomeController;

public class ApplicationContextTest {
	private AnnotationConfigApplicationContext ac;

	@Before
	public void setup() {
		ac = new AnnotationConfigApplicationContext(ApplicationConfig.class);
	}

	@Test
	public void equalsBean() throws Exception {
		HomeController mp1 = ac.getBean(HomeController.class);
		HomeController mp2 = ac.getBean(HomeController.class);
		assertTrue(mp1 == mp2); // 결과는?
	}

	@After
	public void teardown() {
		ac.close();
	}
}
