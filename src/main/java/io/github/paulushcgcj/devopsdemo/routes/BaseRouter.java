package io.github.paulushcgcj.devopsdemo.routes;

import java.util.function.Consumer;

import org.springframework.web.reactive.function.server.RouterFunctions;

public interface BaseRouter {
  String basePath();
  Consumer<RouterFunctions.Builder> routes();


}
