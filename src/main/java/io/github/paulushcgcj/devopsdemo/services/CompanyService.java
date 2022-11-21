package io.github.paulushcgcj.devopsdemo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import io.github.paulushcgcj.devopsdemo.entities.Company;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyPersistenceException;
import io.github.paulushcgcj.devopsdemo.exceptions.NullCompanyException;
import io.github.paulushcgcj.devopsdemo.repositories.CompanyRepository;
import io.github.paulushcgcj.devopsdemo.validators.CompanyValidator;
import io.micrometer.core.annotation.Timed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

  @Getter private final CompanyRepository companyRepository;

  @Getter private final CompanyValidator validator;

  @Timed(value = "service.service", description = "Monitors the service that process a request")
  @NewSpan
  public Mono<List<Company>> listCompanies(long page, long size, String name) {
    log.info("Listing companies {} {} {}", page, size, name);

    return companyRepository
        .findAll()
        .filter(filterByName(name))
        .skip(page * size)
        .take(size)
        .collectList()
        .switchIfEmpty(Mono.just(new ArrayList<>()))
        .doOnNext(companies -> log.info("{} companies found", companies.size()));
  }

  @Timed(
      value = "service.service",
      longTask = true,
      description = "Monitors the service that process a request")
  @NewSpan
  public Mono<String> addCompany(Company company) {
    log.info("Adding company {}", company);
    if (company != null) {
      return listCompanies(0, 1, company.getName())
          .filter(List::isEmpty)
          .doOnNext(logger())
          .flatMap(
              companies ->
                  saveCompany(company.withId(null)))
          .doOnNext(id -> log.info("Company added with ID {}", id))
          .switchIfEmpty(Mono.error(new CompanyAlreadyExistException(company.getName())));
    }
    return Mono.error(new NullCompanyException());
  }

  @Timed(
      value = "service.service",
      longTask = true,
      description = "Monitors the service that process a request")
  @NewSpan
  public Mono<Company> getCompany(String id) {
    log.info("Searching for company with id {}", id);
    return companyRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new CompanyNotFoundException(id)));
  }

  @Timed(
      value = "service.service",
      longTask = true,
      description = "Monitors the service that process a request")
  @NewSpan
  public Mono<Void> updateCompany(String id, Company company) {
    log.info("Updating company with ID {} to {}", id, company);
    if (company != null) {
      return getCompany(id).flatMap(company1 -> companyRepository.save(company.withId(id))).then();
    }
    return Mono.error(new NullCompanyException());
  }

  @Timed(
      value = "service.service",
      longTask = true,
      description = "Monitors the service that process a request")
  @NewSpan
  public Mono<Void> removeCompany(String id) {
    log.info("Removing company with id {}", id);
    return getCompany(id).flatMap(company -> companyRepository.deleteById(id)).then();
  }

  private Mono<String> saveCompany(Company company) {

    Errors errors = new BeanPropertyBindingResult(company, Company.class.getName());
    validator.validate(company, errors);

    if (!errors.hasErrors())
      return companyRepository
          .save(company)
          .doOnNext(logger())
          .doOnError(logger())
          .map(Company::getId);

    return Flux.fromIterable(errors.getAllErrors())
        .map(FieldError.class::cast)
        .map(DefaultMessageSourceResolvable::getCode)
        .reduce((m1, m2) -> String.join(",", new String[] {m1, m2}))
        .flatMap(v -> Mono.error(new CompanyPersistenceException(v)));
  }

  private Predicate<Company> filterByName(String name) {
    if (StringUtils.isNotBlank(name)) {
      return company -> company.getName().toLowerCase().contains(name.toLowerCase());
    }
    return company -> true;
  }

  private static <T> Consumer<T> logger() {
    return content -> log.info("{}", content);
  }
}
