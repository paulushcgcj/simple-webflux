package io.github.paulushcgcj.devopsdemo.handlers;

import java.security.Principal;
import java.util.function.Consumer;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractHandler<T> {
  protected final Class<T> contentClass;

  protected static Mono<Void> logPrincipoal(ServerRequest request){
    return
        request
            .principal()
            .map(Principal::getName)
            .doOnNext(principalName -> log.info("Requesting with user {}",principalName))
            .then();
  }

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
