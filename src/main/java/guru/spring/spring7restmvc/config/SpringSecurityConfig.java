package guru.spring.spring7restmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http.authorizeHttpRequests(authorizeRequests ->
            authorizeRequests.anyRequest().authenticated())
        .csrf( httpSecurityCsrfConfigurer ->
            httpSecurityCsrfConfigurer.ignoringRequestMatchers("/api/**"));
    return http.build();
  }
}
