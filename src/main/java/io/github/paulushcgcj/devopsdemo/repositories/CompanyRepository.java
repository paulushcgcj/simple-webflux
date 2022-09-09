package io.github.paulushcgcj.devopsdemo.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.github.paulushcgcj.devopsdemo.models.Company;

public interface CompanyRepository extends ReactiveCrudRepository<Company, String> {
}
