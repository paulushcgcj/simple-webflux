package io.github.paulushcgcj.devopsdemo.endpoints;

import java.util.Map;

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

    if (exception instanceof ResponseStatusException) {
      ResponseStatusException responseStatusException = (ResponseStatusException) exception;

      return ServerResponse.status(responseStatusException.getStatus())
          .contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(responseStatusException.getReason()));
    }

    Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
        ErrorAttributeOptions.defaults());

    return ServerResponse.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(errorPropertiesMap));
  }

}
