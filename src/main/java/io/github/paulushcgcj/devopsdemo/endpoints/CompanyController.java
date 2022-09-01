package io.github.paulushcgcj.devopsdemo.endpoints;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import io.github.paulushcgcj.devopsdemo.models.Company;
import io.github.paulushcgcj.devopsdemo.services.CompanyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

  @Getter
  private final CompanyService service;

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<List<Company>> listCompanies(
      @RequestParam(required = false, defaultValue = "0") long page,
      @RequestParam(required = false, defaultValue = "10") long size,
      @RequestParam(required = false) String name
  ) {
    return service.listCompanies(page, size, name);
  }

  @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Void> addCompany(@RequestBody @Valid Company company, ServerHttpResponse response) {
    return service
        .addCompany(company)
        .doOnNext(id -> response
            .getHeaders()
            .add("Location", String.format("/api/companies/%s", id))
        )
        .then();
  }

  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<Company> getCompany(@PathVariable String id) {
    return service.getCompany(id);
  }

  @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Mono<Void> updateCompany(@PathVariable String id, @RequestBody Company company) {
    return service.updateCompany(id, company);
  }

  @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> removeCompany(@PathVariable String id) {
    return service.removeCompany(id);
  }

}
