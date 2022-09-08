package io.github.paulushcgcj.devopsdemo.repositories;

import io.github.paulushcgcj.devopsdemo.models.Company;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CompanyRepository {

  Flux<Company> findAll();

  Mono<Company> findById(String id);
  Mono<Company> save(Company company);

  Mono<Void> deleteById(String id);

  void clear();
}
