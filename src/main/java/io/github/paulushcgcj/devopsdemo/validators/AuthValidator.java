package io.github.paulushcgcj.devopsdemo.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.paulushcgcj.devopsdemo.dtos.AuthRequest;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return AuthRequest.class.equals(clazz);
  }

  @Override
  @Timed(value = "service.validator",longTask = true,description = "Monitors the validator that validates a request")
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors,"username","empty.field");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors,"password","empty.field");
  }
}
