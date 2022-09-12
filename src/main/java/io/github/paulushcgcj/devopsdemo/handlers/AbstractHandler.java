package io.github.paulushcgcj.devopsdemo.handlers;

import java.util.function.Consumer;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractHandler<T> {
  protected final Class<T> contentClass;

  protected static Consumer<Throwable> handleError() {
    return throwable -> ServerResponse
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(BodyInserters.fromValue(throwable.getMessage()));
  }

  protected static Consumer<ResponseStatusException> handleStatusResponse() {
    return t -> ServerResponse
        .status(t.getStatus())
        .body(BodyInserters.fromValue(t.getReason()));
  }


}
