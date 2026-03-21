package guru.spring.spring7restmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http.authorizeHttpRequests(authorizeRequests ->
            authorizeRequests.anyRequest().authenticated())
        .oauth2ResourceServer( oauth2ResourceServer ->
            oauth2ResourceServer.jwt(Customizer.withDefaults()));
//        .csrf( httpSecurityCsrfConfigurer ->
//            httpSecurityCsrfConfigurer.ignoringRequestMatchers("/api/**"));


    return http.build();
  }

}
