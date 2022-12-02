package io.github.paulushcgcj.devopsdemo.handlers;

import java.util.function.Consumer;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

  protected static Mono<Void> logPrincipal(ServerRequest request) {

    return request
        .principal()
        .tag(
            "uri",
            request
                .attribute(
                    "org.springframework.web.reactive.function.server.RouterFunctions.matchingPattern")
                .map(String::valueOf)
                .orElse(request.path()))
        .tag("method", request.method().name())
        .tag("ops", request.method().name())
        .metrics()
        .map(JwtAuthenticationToken.class::cast)
        .doOnNext(
            jwtPrincipal ->
                log.info(
                    "{} with email {} and id {} logged in",
                    jwtPrincipal.getToken().getClaim("name"),
                    jwtPrincipal.getToken().getClaim("email"),
                    jwtPrincipal.getName()))
        .doOnNext(
            jwtPrincipal ->
                log.info(
                    "{} with authorities {}",
                    jwtPrincipal.getName(),
                    jwtPrincipal.getAuthorities()))
        .then();
  }

  protected static Consumer<Throwable> handleError() {
    return throwable ->
        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(BodyInserters.fromValue(throwable.getMessage()));
  }

  protected static Consumer<ResponseStatusException> handleStatusResponse() {
    return t -> ServerResponse.status(t.getStatusCode()).body(BodyInserters.fromValue(t.getReason()));
  }
}
