package io.github.paulushcgcj.devopsdemo.configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.nimbusds.jose.shaded.json.JSONArray;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ApplicationSecurity {

  @Bean
  public SecurityWebFilterChain filterChain(
      ServerHttpSecurity http,
      ReactiveJwtDecoder jwtDecoder
  ) {

    http
        .authorizeExchange()

        .pathMatchers("/api/**").authenticated()
        .anyExchange().permitAll()

        .and()

        .httpBasic().disable()
        .formLogin().disable()
        .csrf().disable()
        .cors().disable()

        .oauth2ResourceServer()
        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter()).jwtDecoder(jwtDecoder));

    return http.build();
  }

  private Converter<Jwt, Mono<AbstractAuthenticationToken>> converter() {
    return jwt -> Mono.just(new JwtAuthenticationToken(jwt,getGrantedAuthorities(jwt)));
  }

  private static List<GrantedAuthority> getGrantedAuthorities(Jwt jwt) {
    final JSONArray realmAccess = (JSONArray) ((Map<String,Object>)jwt.getClaims().get("realm_access")).get("roles");
    return realmAccess.stream()
        .map(roleName -> "ROLE_" + roleName) // prefix to map to a Spring Security "role"
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @Bean
  @ConditionalOnMissingBean
  public ReactiveJwtDecoder jwtDecoder(
      @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri
  ) {
    return new NimbusReactiveJwtDecoder(jwkSetUri);
  }

}
