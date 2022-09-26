package io.github.paulushcgcj.devopsdemo.configuration;

import io.github.paulushcgcj.devopsdemo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ApplicationSecurity {

  private final UserRepository userRepository;

  @Bean
  public SecurityWebFilterChain filterChain(
      ServerHttpSecurity http,
      ReactiveJwtDecoder jwtDecoder
  ) {

    http
        .authorizeExchange()

        .anyExchange().permitAll()
        .pathMatchers("/api/**").authenticated()

        .and()

        .httpBasic().disable()
        .formLogin().disable()
        .csrf().disable()
        .cors().disable()

        .oauth2ResourceServer()
        .jwt()
        .jwtDecoder(jwtDecoder);

    return http.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public ReactiveJwtDecoder jwtDecoder(
      @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri
  ) {
    return new NimbusReactiveJwtDecoder(jwkSetUri);
  }

}
