package io.github.paulushcgcj.devopsdemo.routes;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.swagger.v3.oas.models.tags.Tag;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

public abstract class BaseRouter {
  abstract String basePath();

  abstract String routeTagName();

  abstract String routeTagDescription();

  abstract RouterFunction<ServerResponse> routerRoute();

  @Bean
  public RouterFunction<ServerResponse> routes() {
    return nest(path(basePath()), routerRoute());
  }

  @Bean
  public OpenApiCustomiser tagCustomizer() {
    return openAPI ->
        openAPI.getTags().add(new Tag().name(routeTagName()).description(routeTagDescription()));
  }
}
