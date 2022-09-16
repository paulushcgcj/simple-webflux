package io.github.paulushcgcj.devopsdemo.components;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;

import io.github.paulushcgcj.devopsdemo.dtos.BearerToken;
import io.github.paulushcgcj.devopsdemo.exceptions.InvalidBearerTokenException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

  private final JwtSecurityComponent jwtComponent;
  private final ReactiveUserDetailsService detailsService;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    return
        Mono
            .justOrEmpty(authentication)
            .filter(BearerToken.class::isInstance)
            .map(BearerToken.class::cast)
            .flatMap(this::validate)
            .onErrorMap(error -> new InvalidBearerTokenException());
  }


  private Mono<Authentication> validate(BearerToken token) {
    return
        Mono
            .just((String) token.getCredentials())
            .filter(jwtComponent::validateToken)
            .filter(jwtToken -> StringUtils.isNotBlank(jwtComponent.getUsernameFromToken(jwtToken)))
            .map(jwtComponent::getUsernameFromToken)
            .flatMap(detailsService::findByUsername)
            .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()))
            .map(Authentication.class::cast)
            .switchIfEmpty(Mono.error(new InvalidBearerTokenException()));
  }


}
