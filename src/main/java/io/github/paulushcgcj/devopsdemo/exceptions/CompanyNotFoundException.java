package io.github.paulushcgcj.devopsdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CompanyNotFoundException extends ResponseStatusException {
  public CompanyNotFoundException(String id) {
    super(HttpStatus.NOT_FOUND, "No company with id " + id + " found");
  }
}
