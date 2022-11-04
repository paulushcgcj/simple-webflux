package io.github.paulushcgcj.devopsdemo.configuration;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import com.nimbusds.jose.shaded.json.JSONArray;

import reactor.core.publisher.Mono;

@TestConfiguration
public class TestSecurityConfiguration {

  @Bean
  @Primary
  public ReactiveJwtDecoder jwtDecoder(
      @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri
  ) {
    JSONArray roles = new JSONArray();
    roles.add("test");
    roles.add("junit");

    return new ReactiveJwtDecoder() {
      @Override
      public Mono<Jwt> decode(String token) throws JwtException {
        return Mono.just(new Jwt(
                token,
                Instant.now(),
                Instant.MAX,
                Map.of("alg", "none"),
                Map.of(
                    JwtClaimNames.SUB, "junit",
                    "name", "JUnit",
                    "email", "test@junit.ca",
                    "realm_access", Map.of("roles",  roles)
                )
            )
        );
      }
    };
  }

}
