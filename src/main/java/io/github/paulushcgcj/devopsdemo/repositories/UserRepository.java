package io.github.paulushcgcj.devopsdemo.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

import io.github.paulushcgcj.devopsdemo.entities.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User,String> {
  Mono<UserDetails> findByUsername(String username);
}
