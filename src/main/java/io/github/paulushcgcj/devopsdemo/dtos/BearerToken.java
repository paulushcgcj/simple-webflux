package io.github.paulushcgcj.devopsdemo.dtos;

import java.util.ArrayList;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class BearerToken extends AbstractAuthenticationToken {

  private final String token;

  public BearerToken(String token) {
    super(new ArrayList<>());
    this.token = token;
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return token;
  }
}
