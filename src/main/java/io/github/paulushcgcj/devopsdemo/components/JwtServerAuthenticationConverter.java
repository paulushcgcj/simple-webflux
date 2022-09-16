package io.github.paulushcgcj.devopsdemo.components;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.github.paulushcgcj.devopsdemo.dtos.BearerToken;
import reactor.core.publisher.Mono;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

  @Override
  public Mono<Authentication> convert(ServerWebExchange exchange) {
    return Mono
        .justOrEmpty(
            exchange
                .getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION)
        )
        .filter(header -> header.startsWith("Bearer "))
        .map(header -> header.substring(7))
        .map(BearerToken::new);
  }


}
