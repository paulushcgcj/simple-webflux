package io.github.paulushcgcj.devopsdemo.routes;

import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunctions;

import io.github.paulushcgcj.devopsdemo.handlers.LoginHandler;
import lombok.RequiredArgsConstructor;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@RequiredArgsConstructor
public class SecurityRouter implements BaseRouter {

  private final LoginHandler handler;

  @Override
  public String basePath() {
    return "/login";
  }

  @Override
  public Consumer<RouterFunctions.Builder> routes() {
    return builder -> builder.POST(accept(MediaType.APPLICATION_JSON),handler::login);
  }
}
