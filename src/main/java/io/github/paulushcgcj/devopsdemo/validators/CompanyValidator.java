package io.github.paulushcgcj.devopsdemo.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.github.paulushcgcj.devopsdemo.models.Company;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CompanyValidator implements Validator {
  @Override
  public boolean supports(Class<?> clazz) {
    return Company.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
  }
}
