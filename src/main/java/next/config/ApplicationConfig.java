package next.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("next.controller")
public class ApplicationConfig {
//	
//	@Bean
//	public HomeController homeController(){
//		return new HomeController();
//	}
}
