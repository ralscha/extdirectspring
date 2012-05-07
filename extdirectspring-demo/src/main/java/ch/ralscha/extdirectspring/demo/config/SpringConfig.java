package ch.ralscha.extdirectspring.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ch.ralscha.extdirectspring.controller.RouterController;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses=RouterController.class, basePackages = "ch.ralscha.extdirectspring.demo")
public class SpringConfig {

	/*
	@Bean
	public ch.ralscha.extdirectspring.controller.Configuration edsConfig() {
		ch.ralscha.extdirectspring.controller.Configuration config = new ch.ralscha.extdirectspring.controller.Configuration();
		config.setStreamResponse(true);
		config.setTimeout(12000);
		config.setMaxRetries(10);
		config.setEnableBuffer(false);
		return config;
	}
	*/

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	@Lazy
	public ClassPathResource randomdata() {
		return new ClassPathResource("/randomdata.csv");
	}

	@Bean
	@Lazy
	public ClassPathResource pivotdata() {
		return new ClassPathResource("/pivodata.csv");
	}

	@Bean
	@Lazy
	public ClassPathResource userdata() {
		return new ClassPathResource("/users.csv");
	}

}
