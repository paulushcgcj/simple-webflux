package io.github.paulushcgcj.devopsdemo.services;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.github.paulushcgcj.devopsdemo.repositories.CompanyRepository;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.exceptions.NullCompanyException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CompanyService {

  @Autowired
  @Getter
  @Setter
  private CompanyRepository companyRepository;

  public Mono<List<Company>> listCompanies(long page, long size, String name) {
    log.info("Listing companies {} {} {}", page, size, name);
    return
        companyRepository
            .findAll()
            .filter(filterByName(name))
            .skip(page * size)
            .take(size)
            .collectList()
            .switchIfEmpty(Mono.just(new ArrayList<>()))
            .doOnNext(companies -> log.info("{} companies found", companies.size()));
  }


  public Mono<String> addCompany(Company company) {
    log.info("Adding company {}", company);
    if (company != null) {
      return
          listCompanies(0, 1, company.getName())
              .filter(companies -> !companies.isEmpty())
              .doOnNext(companies -> log.error("A company already exists with name {}", company.getName()))
              .flatMap(companies -> Mono.error(new CompanyAlreadyExistException(company.getName())))
              .map(String.class::cast)
              .switchIfEmpty(saveCompany(company));
    }
    return Mono.error(new NullCompanyException());
  }

  public Mono<Company> getCompany(String id) {
    log.info("Searching for company with id {}", id);
    return
        companyRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new CompanyNotFoundException(id)));
  }

  public Mono<Void> updateCompany(String id, Company company) {
    log.info("Updating company with ID {} to {}", id, company);
    if (company != null) {
      return
          getCompany(id)
              .flatMap(company1 ->
                  companyRepository
                      .save(company.withId(id))
                      .then()
              );
    }
    return Mono.error(new NullCompanyException());
  }

  public Mono<Void> removeCompany(String id) {
    log.info("Removing company with id {}", id);
    return companyRepository.deleteById(id);
  }

  private Mono<String> saveCompany(Company company) {
    return
        companyRepository
            .save(company)
            .map(Company::getId)
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
