package io.github.paulushcgcj.devopsdemo.endpoints;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class CompanyRouter {

  @Bean
  public RouterFunction<ServerResponse> route(CompanyHandler companyHandler) {
    return
        RouterFunctions
            .route()
            .path("/api/companies", builder ->
                builder
                    .GET("/{id}", accept(MediaType.APPLICATION_JSON), companyHandler::getCompany)
                    .PUT("/{id}", accept(MediaType.APPLICATION_JSON), companyHandler::updateCompany)
                    .DELETE("/{id}", accept(MediaType.APPLICATION_JSON), companyHandler::removeCompany)

                    .GET(accept(MediaType.APPLICATION_JSON), companyHandler::listCompanies)
                    .POST(accept(MediaType.APPLICATION_JSON), companyHandler::addCompany)
            )
            .build();
  }
}
