package io.github.paulushcgcj.devopsdemo.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.github.paulushcgcj.devopsdemo.entities.Company;

public interface CompanyRepository extends ReactiveCrudRepository<Company, String> {
}
