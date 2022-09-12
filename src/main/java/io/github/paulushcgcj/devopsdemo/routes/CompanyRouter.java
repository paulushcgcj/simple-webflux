package io.github.paulushcgcj.devopsdemo.routes;

import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunctions;

import io.github.paulushcgcj.devopsdemo.handlers.CompanyHandler;
import lombok.RequiredArgsConstructor;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@RequiredArgsConstructor
public class CompanyRouter implements BaseRouter {

  public static final String ID = "/{id}";

  private final CompanyHandler handler;

  @Override
  public String basePath() {
    return "/api/companies";
  }

  @Override
  public Consumer<RouterFunctions.Builder> routes() {
    return builder ->
      builder
            .GET(ID, accept(MediaType.APPLICATION_JSON), handler::getCompany)
            .PUT(ID, accept(MediaType.APPLICATION_JSON), handler::updateCompany)
            .DELETE(ID, accept(MediaType.APPLICATION_JSON), handler::removeCompany)

            .GET(accept(MediaType.APPLICATION_JSON), handler::listCompanies)
            .POST(accept(MediaType.APPLICATION_JSON), handler::addCompany);

  }
}
