package io.github.paulushcgcj.devopsdemo.handlers;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import io.github.paulushcgcj.devopsdemo.components.JwtSecurityComponent;
import io.github.paulushcgcj.devopsdemo.dtos.AuthRequest;
import io.github.paulushcgcj.devopsdemo.dtos.AuthResponse;
import io.github.paulushcgcj.devopsdemo.exceptions.UserNotFoundException;
import io.github.paulushcgcj.devopsdemo.repositories.UserRepository;
import io.github.paulushcgcj.devopsdemo.validators.AuthValidator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoginHandler extends AbstractValidatedHandler<AuthRequest, AuthValidator> {

  private UserRepository userRepository;
  private PasswordEncoder encoder;

  private JwtSecurityComponent jwtComponent;

  protected LoginHandler(AuthValidator validator) {
    super(AuthRequest.class, validator);
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Autowired
  public void setEncoder(PasswordEncoder encoder) {
    this.encoder = encoder;
  }

  @Autowired
  public void setJwtComponent(JwtSecurityComponent jwtComponent) {
    this.jwtComponent = jwtComponent;
  }

  @NewSpan
  public Mono<ServerResponse> login(ServerRequest request) {

    return
        request
            .bodyToMono(AuthRequest.class)
            .doOnNext(authRequest -> log.info("Trying to log in as {}", authRequest.getUsername()))
            .flatMap(validate())
            .flatMap(authenticate())
            .flatMap(buildResponse())
            .doOnError(ResponseStatusException.class, handleStatusResponse())
            .doOnError(handleError());
  }

  private static Function<AuthResponse, Mono<? extends ServerResponse>> buildResponse() {
    return token -> ServerResponse
        .ok()
        .body(Mono.just(token), AuthRequest.class);
  }

  private Function<AuthRequest, Mono<? extends AuthResponse>> authenticate() {
    return authRequest ->
        userRepository
            .findByUsername(authRequest.getUsername())
            .filter(userDetails -> encoder.matches(authRequest.getPassword(), userDetails.getPassword()))
            .map(userDetails -> new AuthResponse(jwtComponent.generateToken(userDetails.getUsername(),userDetails.getAuthorities())))
            .switchIfEmpty(Mono.error(new UserNotFoundException(authRequest.getUsername())));
  }
}
