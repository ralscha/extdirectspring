package ch.ralscha.starter.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = { "ch.ralscha.extdirectspring", "ch.ralscha.starter" })
@PropertySource("version.properties")
public class ComponentConfig {
	//nothing here
}