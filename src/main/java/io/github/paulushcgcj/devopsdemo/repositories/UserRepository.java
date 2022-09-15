package io.github.paulushcgcj.devopsdemo.repositories;

import io.github.paulushcgcj.devopsdemo.models.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User,String> {
  Mono<UserDetails> findByUsername(String username);
}
