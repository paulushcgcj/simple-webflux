package io.github.paulushcgcj.devopsdemo.services;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.exceptions.NullCompanyException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CompanyService {

  @Getter
  private final Map<String, Company> companyRepository = new HashMap<>();

  public Mono<List<Company>> listCompanies(long page, long size, String name) {
    log.info("Listing companies {} {} {}", page, size, name);
    return
        Flux.fromIterable(companyRepository.values())
            .doOnNext(logger())
            .filter(filterByName(name))
            .doOnNext(logger())
            .skip(page * size)
            .doOnNext(logger())
            .take(size)
            .doOnNext(logger())
            .collectList()
            .switchIfEmpty(Mono.just(new ArrayList<>()));
  }


  public Mono<String> addCompany(Company company) {
    log.info("Adding company {}", company);
    if (company != null) {
      return
          listCompanies(0, 1, company.getName())
              .doOnNext(logger())
              .filter(companies -> !companies.isEmpty())
              .flatMap(companies -> Mono.error(new CompanyAlreadyExistException(company.getName())))
              .doOnNext(logger())
              .map(String.class::cast)
              .doOnNext(logger())
              .switchIfEmpty(saveCompany(company))
              .doOnNext(logger());
    }
    return Mono.error(new NullCompanyException());
  }

  public Mono<Company> getCompany(String id) {
    return
        Flux
            .fromIterable(companyRepository.values())
            .doOnNext(logger())
            .filter(company -> company.getId().equalsIgnoreCase(id))
            .doOnNext(logger())
            .next()
            .doOnNext(logger())
            .switchIfEmpty(Mono.error(new CompanyNotFoundException(id)));
  }

  public Mono<Void> updateCompany(String id, Company company) {
    if (company != null) {
      return
          getCompany(id)
              .doOnNext(logger())
              .map(_company -> companyRepository.put(id, company.withId(id)))
              .doOnNext(logger())
              .then();
    }
    return Mono.error(new NullCompanyException());
  }

  public Mono<Void> removeCompany(String id) {
    return
        getCompany(id)
            .doOnNext(logger())
            .map(company -> companyRepository.remove(company.getId()))
            .doOnNext(logger())
            .then();
  }

  private Mono<String> saveCompany(Company company) {
    return
        Mono
            .just(company)
            .doOnNext(logger())
            .map(_company -> _company.withId(UUID.randomUUID().toString()))
            .doOnNext(logger())
            .map(_company ->
                Optional
                    .ofNullable(companyRepository.put(_company.getId(), _company))
                    .map(Company::getId)
                    .orElse(_company.getId())
            )
            .doOnNext(logger());
  }

  private Predicate<Company> filterByName(String name) {
    if (StringUtils.isNotBlank(name)) {
      return company -> company
          .getName()
          .toLowerCase()
          .contains(name.toLowerCase());
    }
    return company -> true;
  }

  private static <T> Consumer<T> logger() {
    return content -> log.info("{}", content);
  }

}
