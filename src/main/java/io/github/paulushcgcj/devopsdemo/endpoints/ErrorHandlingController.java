package io.github.paulushcgcj.devopsdemo.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class ErrorHandlingController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {Exception.class,ResponseStatusException.class})
  protected ResponseEntity<Object> handleResponseStatusException(Exception ex, WebRequest request) {

    log.error("Received an exception from the application {}",
        ex.getMessage(),
        ex
    );

    if(ex instanceof ResponseStatusException){
      return handleExceptionInternal(ex, ((ResponseStatusException)ex).getReason(), new HttpHeaders(), ((ResponseStatusException)ex).getStatus(), request);
    }

    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);

  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

    return handleExceptionInternal(ex, ex
        .getAllErrors()
        .stream()
        .map(convert())
        .collect(Collectors.toList()), new HttpHeaders(), HttpStatus.PRECONDITION_FAILED, request);
  }

  private Function<ObjectError,String> convert(){
    return objectError -> {

      if(objectError instanceof DefaultMessageSourceResolvable){
        DefaultMessageSourceResolvable message = objectError;
        DefaultMessageSourceResolvable subMessage = (DefaultMessageSourceResolvable) message.getArguments()[0];
        return (subMessage.getDefaultMessage() + " " + message.getDefaultMessage());
      }

      return objectError.toString();

    };
  }
}
