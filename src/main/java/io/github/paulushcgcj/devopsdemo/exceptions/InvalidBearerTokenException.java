package io.github.paulushcgcj.devopsdemo.exceptions;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidBearerTokenException extends AuthenticationException {
  public InvalidBearerTokenException() {
    super("Provided authentication token is invalid");
  }
}
