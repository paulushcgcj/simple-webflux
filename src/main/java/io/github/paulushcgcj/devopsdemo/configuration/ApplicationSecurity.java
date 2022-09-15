package io.github.paulushcgcj.devopsdemo.configuration;

import io.github.paulushcgcj.devopsdemo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ApplicationSecurity {

  private final UserRepository userRepository;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return
        http
            .authorizeExchange()
            .anyExchange()
            .authenticated()

            .and()

            .httpBasic()

            .and()
            .build();
  }

  @Bean
  @NewSpan
  public ReactiveUserDetailsService userDetailsService(){
    return userRepository::findByUsername;
  }

}
