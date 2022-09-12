package io.github.paulushcgcj.devopsdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CompanyPersistenceException extends ResponseStatusException {
  public CompanyPersistenceException(String message) {
    super(HttpStatus.BAD_REQUEST,message);
  }
}
