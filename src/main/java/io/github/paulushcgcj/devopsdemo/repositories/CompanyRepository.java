package io.github.paulushcgcj.devopsdemo.repositories;

import io.github.paulushcgcj.devopsdemo.models.Company;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CompanyRepository extends ReactiveCrudRepository<Company, String> {
}
