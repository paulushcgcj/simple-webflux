package io.github.paulushcgcj.devopsdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserNotFoundException extends ResponseStatusException {

  public UserNotFoundException(String username) {
    super(HttpStatus.UNAUTHORIZED,"Cannot login with username " + username + " :: not found");
  }
}
