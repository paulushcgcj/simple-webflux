package io.github.paulushcgcj.devopsdemo.handlers;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
@Component
@Order(-2)
public class ErrorHandlingController extends AbstractErrorWebExceptionHandler {

  public ErrorHandlingController(
      ErrorAttributes errorAttributes,
      WebProperties webProperties,
      ApplicationContext applicationContext,
      ServerCodecConfigurer configurer
  ) {
    super(errorAttributes, webProperties.getResources(), applicationContext);
    this.setMessageWriters(configurer.getWriters());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(
      ErrorAttributes errorAttributes) {
    return RouterFunctions.route(
        RequestPredicates.all(), request -> renderErrorResponse(request, errorAttributes));
  }

  private Mono<ServerResponse> renderErrorResponse(
      ServerRequest request,
      ErrorAttributes errorAttributes) {

    Throwable exception = errorAttributes.getError(request).fillInStackTrace();
    String errorMessage = exception.getMessage();
    HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    log.error("An error was generated during request", exception);

    if (exception instanceof ResponseStatusException) {
      ResponseStatusException responseStatusException = (ResponseStatusException) exception;
      errorMessage = responseStatusException.getReason();
      errorStatus = responseStatusException.getStatus();
    }

    errorMessage = BooleanUtils.toString(StringUtils.isBlank(errorMessage), StringUtils.EMPTY, errorMessage);

    log.error("{} - {}", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());

    return ServerResponse.status(errorStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(errorMessage));
  }

}
