package io.github.paulushcgcj.devopsdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CompanyAlreadyExistException extends ResponseStatusException {
  public CompanyAlreadyExistException(String companyName) {
    super(HttpStatus.CONFLICT,"A company with name " + companyName + " already exists");
  }
}
