package io.github.paulushcgcj.devopsdemo.configuration;

import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import io.github.paulushcgcj.devopsdemo.filters.JwtWebFilter;
import io.github.paulushcgcj.devopsdemo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ApplicationSecurity {

  private final UserRepository userRepository;

  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http,final JwtWebFilter filter) {

    return
        http
            .exceptionHandling()
            .authenticationEntryPoint((exchange, ex) -> {
              exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
              exchange.getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE,"Bearer");
              return Mono.empty();
            })

            .and()

            .authorizeExchange()

            .pathMatchers(HttpMethod.POST, "/login").permitAll()
            .pathMatchers(HttpMethod.GET, "/health").permitAll()
            .pathMatchers(HttpMethod.GET, "/metrics").permitAll()

            .anyExchange().authenticated()

            .and()

            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)


            .httpBasic().disable()
            .formLogin().disable()

            .csrf().disable()

            .build();
  }

  @Bean
  @NewSpan
  public ReactiveUserDetailsService userDetailsService() {
    return userRepository::findByUsername;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {return PasswordEncoderFactories.createDelegatingPasswordEncoder(); }

}
