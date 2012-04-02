#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.encoding.PasswordEncoder;

@Configuration
@ImportResource("classpath:${packageInPathFormat}/config/security.xml")
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		org.jasypt.springsecurity3.authentication.encoding.PasswordEncoder pe = new org.jasypt.springsecurity3.authentication.encoding.PasswordEncoder();
		pe.setPasswordEncryptor(new StrongPasswordEncryptor());
		return pe;
	}

}
