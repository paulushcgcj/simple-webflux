package io.github.paulushcgcj.devopsdemo.filters;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class JwtWebFilter extends AuthenticationWebFilter {

  public JwtWebFilter(
      ReactiveAuthenticationManager authenticationManager,
      ServerAuthenticationConverter converter
  ) {
    super(authenticationManager);
    setServerAuthenticationConverter(converter);
  }
}
