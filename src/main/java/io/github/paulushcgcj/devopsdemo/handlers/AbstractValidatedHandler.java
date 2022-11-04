package io.github.paulushcgcj.devopsdemo.handlers;

import java.util.function.Function;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyPersistenceException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractValidatedHandler<T, U extends Validator> extends AbstractHandler<T> {

  private final U validator;

  protected AbstractValidatedHandler(Class<T> clazz, U validator) {
    super(clazz);
    this.validator = validator;
  }

  protected Function<T, Mono<T>> validate() {
    return target ->
        Mono.just(new BeanPropertyBindingResult(target, contentClass.getName()))
            .doOnNext(errors -> validator.validate(target, errors))
            .filter(AbstractBindingResult::hasErrors)
            .flatMap(getErrorMessages())
            .flatMap(errorMessage -> Mono.error(new CompanyPersistenceException(errorMessage)))
            .map(value -> (T) value)
            .switchIfEmpty(Mono.just(target));
  }

  private static Function<BeanPropertyBindingResult, Mono<String>> getErrorMessages() {
    return errors ->
        Flux.fromIterable(errors.getAllErrors())
            .map(FieldError.class::cast)
            .map(DefaultMessageSourceResolvable::getCode)
            .reduce((message1, message2) -> String.join(",", new String[] {message1, message2}));
  }
}
