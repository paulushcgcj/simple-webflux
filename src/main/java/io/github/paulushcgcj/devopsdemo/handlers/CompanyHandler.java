package io.github.paulushcgcj.devopsdemo.handlers;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import io.github.paulushcgcj.devopsdemo.entities.Company;
import io.github.paulushcgcj.devopsdemo.services.CompanyService;
import io.github.paulushcgcj.devopsdemo.validators.CompanyValidator;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CompanyHandler extends AbstractValidatedHandler<Company, CompanyValidator> {

  private final CompanyService service;

  public CompanyHandler(CompanyValidator validator, CompanyService service) {
    super(Company.class, validator);
    this.service = service;
  }

  @Timed(value = "service.handler", longTask = true, description = "Monitors the handler that receives a request")
  public Mono<ServerResponse> listCompanies(ServerRequest request) {

    long page = request.queryParam("page").map(Long::parseLong).orElse(0L);
    long size = request.queryParam("size").map(Long::parseLong).orElse(10L);
    String name = request.queryParam("name").orElse(null);

    log.info("Requesting companies {} {} {}", page, size, name);

    return
        logPrincipoal(request)
            .then(
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.listCompanies(page, size, name), List.class)
                    .doOnError(ResponseStatusException.class, handleStatusResponse())
                    .doOnError(handleError())
            );
  }

  @Timed(value = "service.handler", longTask = true, description = "Monitors the handler that receives a request")
  public Mono<ServerResponse> getCompany(ServerRequest request) {
    log.info("Requesting company details {}", request.pathVariable("id"));

    return
        logPrincipoal(request)
            .then(
                service
                    .getCompany(request.pathVariable("id"))
                    .flatMap(
                        company ->
                            ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(company), Company.class)
                    )
                    .doOnError(ResponseStatusException.class, handleStatusResponse())
                    .doOnError(handleError())
            );
  }

  @Timed(value = "service.handler", longTask = true, description = "Monitors the handler that receives a request")
  public Mono<ServerResponse> addCompany(ServerRequest request) {

    return
        logPrincipoal(request)
            .then(
                request
                    .bodyToMono(Company.class)
                    .doOnNext(company -> log.info("Creating company {}", company))
                    .flatMap(validate())
                    .flatMap(service::addCompany)
                    .flatMap(companyId ->
                        ServerResponse
                            .created(URI.create(String.format("/api/companies/%s", companyId)))
                            .build()
                    )
                    .doOnError(ResponseStatusException.class, handleStatusResponse())
                    .doOnError(handleError())
            );
  }

  @Timed(value = "service.handler", longTask = true, description = "Monitors the handler that receives a request")
  public Mono<ServerResponse> updateCompany(ServerRequest request) {
    log.info("Requesting company update {}", request.pathVariable("id"));

    return
        logPrincipoal(request)
            .then(
                request
                    .bodyToMono(Company.class)
                    .doOnNext(company -> log.info("Updating company {}", company))
                    .flatMap(validate())
                    .flatMap(company -> service.updateCompany(request.pathVariable("id"), company))
                    .then(ServerResponse.accepted().build())
                    .doOnError(ResponseStatusException.class, handleStatusResponse())
                    .doOnError(handleError())
            );
  }

  @Timed(value = "service.handler", longTask = true, description = "Monitors the handler that receives a request")
  public Mono<ServerResponse> removeCompany(ServerRequest request) {
    log.info("Requesting company delete {}", request.pathVariable("id"));

    return
        logPrincipoal(request)
            .then(
                service
                    .removeCompany(request.pathVariable("id"))
                    .then(ServerResponse.noContent().build())
                    .doOnError(ResponseStatusException.class, handleStatusResponse())
                    .doOnError(handleError())
            );
  }


}
