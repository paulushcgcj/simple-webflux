package io.github.paulushcgcj.devopsdemo.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.github.paulushcgcj.devopsdemo.routes.BaseRouter;
import lombok.extern.slf4j.Slf4j;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class ServiceRouter {

  @Bean
  public RouterFunction<ServerResponse> route(List<BaseRouter> routes) {


    log.info("Configuring {} routes",routes.size());

    RouterFunctions.Builder route = RouterFunctions.route();
    routes.forEach(routeEntry -> route.path(routeEntry.basePath(), routeEntry.routes()));

    return route.build();
  }

}
