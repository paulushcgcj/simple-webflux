package io.github.paulushcgcj.devopsdemo.repositories;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Repository
@Slf4j
public class CompanyRepositoryImpl implements CompanyRepository {


  @Getter
  private final Map<String, Company> companyRepository = new HashMap<>();


  @Override
  public Flux<Company> findAll() {
    return Flux.fromIterable(companyRepository.values());
  }

  @Override
  public Mono<Company> findById(String id) {
    return
        Flux
            .fromIterable(companyRepository.values())
            .filter(company -> company.getId().equalsIgnoreCase(id))
            .next();
  }

  @Override
  public Mono<Company> save(Company company) {
    return
        Mono
            .just(company)
            .doOnNext(logger())
            .filter(company_ -> StringUtils.isNotBlank(company_.getId()))
            .doOnNext(logger())
            .switchIfEmpty(Mono.just(company.withId(UUID.randomUUID().toString())))
            .doOnNext(logger())
            .map(company_ ->
                Optional
                    .ofNullable(companyRepository.put(company_.getId(), company.withId(company_.getId())))
                    .map(v -> company.withId(company_.getId()))
                    .orElse(company.withId(company_.getId()))
            )
            .doOnNext(logger());
  }

  @Override
  public Mono<Void> deleteById(String id) {
    return findById(id)
        .switchIfEmpty(Mono.error(new CompanyNotFoundException(id)))
        .map(company -> companyRepository.remove(company.getId()))
        .then();
  }

  @Override
  public void clear() {
    companyRepository.clear();
  }

  private static <T> Consumer<T> logger() {
    return content -> log.info("{}", content);
  }

}
