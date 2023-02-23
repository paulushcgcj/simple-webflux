package io.github.paulushcgcj.devopsdemo.repositories;

import io.github.paulushcgcj.devopsdemo.entities.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CompanyRepository extends ReactiveCrudRepository<Company, String> {

  Flux<Company> findBy(Pageable page);
}
