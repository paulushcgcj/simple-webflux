package io.github.paulushcgcj.devopsdemo.endpoints;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import io.github.paulushcgcj.devopsdemo.models.Company;
import io.github.paulushcgcj.devopsdemo.services.CompanyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyHandler {

  @Getter
  private final CompanyService service;

  public Mono<ServerResponse> listCompanies(ServerRequest request) {

    long page = request.queryParam("page").map(Long::parseLong).orElse(0L);
    long size = request.queryParam("size").map(Long::parseLong).orElse(10L);
    String name = request.queryParam("name").orElse(null);

    log.info("Requesting companies {} {} {}", page, size, name);

    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.listCompanies(page, size, name), List.class);
  }

  public Mono<ServerResponse> getCompany(ServerRequest request) {
    log.info("Requesting company details {}", request.pathVariable("id"));

    return
        service
            .getCompany(request.pathVariable("id"))
            .flatMap(
                company ->
                    ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(company), Company.class)
            )
            .doOnError(ResponseStatusException.class, generateErrorResponse());
  }

  public Mono<ServerResponse> addCompany(ServerRequest request) {

    return
        request
            .bodyToMono(Company.class)
            .doOnNext(company -> log.info("Creating company {}", company))
            .flatMap(service::addCompany)
            .flatMap(companyId ->
                ServerResponse
                    .created(URI.create(String.format("/api/companies/%s", companyId)))
                    .build()
            )
            .doOnError(ResponseStatusException.class, generateErrorResponse());
  }

  public Mono<ServerResponse> updateCompany(ServerRequest request) {
    log.info("Requesting company update {}", request.pathVariable("id"));

    return
        request
            .bodyToMono(Company.class)
            .doOnNext(company -> log.info("Updating company {}", company))
            .flatMap(company -> service.updateCompany(request.pathVariable("id"),company))
            .then(ServerResponse.accepted().build())
            .doOnError(ResponseStatusException.class, generateErrorResponse());
  }

  public Mono<ServerResponse> removeCompany(ServerRequest request) {
    log.info("Requesting company delete {}", request.pathVariable("id"));

    return
        service
            .removeCompany(request.pathVariable("id"))
            .then(ServerResponse.noContent().build())
            .doOnError(ResponseStatusException.class, generateErrorResponse());
  }

  private static Consumer<ResponseStatusException> generateErrorResponse() {
    return t -> ServerResponse
        .status(t.getStatus())
        .body(BodyInserters.fromValue(t.getReason()));
  }

}
