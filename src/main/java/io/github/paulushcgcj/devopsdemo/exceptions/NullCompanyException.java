package io.github.paulushcgcj.devopsdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.FAILED_DEPENDENCY)
public class NullCompanyException extends ResponseStatusException {
  public NullCompanyException() {
    super(HttpStatus.FAILED_DEPENDENCY,"Cannot save a null company");
  }
}
