package io.github.paulushcgcj.devopsdemo.bdd;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.github.paulushcgcj.devopsdemo.components.JwtSecurityComponent;
import io.github.paulushcgcj.devopsdemo.dtos.AuthRequest;
import io.github.paulushcgcj.devopsdemo.dtos.AuthResponse;
import io.github.paulushcgcj.devopsdemo.entities.User;
import io.github.paulushcgcj.devopsdemo.extensions.AbstractTestContainerIntegrationTest;
import io.github.paulushcgcj.devopsdemo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.test.StepVerifier;

@ContextConfiguration
@CucumberContextConfiguration
@Slf4j
public class CompanyIntegrationTest extends AbstractTestContainerIntegrationTest {

  @Autowired
  private UserRepository repository;

  @Autowired
  private JwtSecurityComponent jwt;

  private WebTestClient.ResponseSpec result;
  private String token;
  private String password;

  @Given("The application is available locally with a random port")
  public void givenAppIsRunning() {
    doGet("/health")
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.status").isEqualTo("UP");
  }

  @And("The database is available")
  public void andDbIsOK() {

    password = UUID.randomUUID().toString();

    User user = User
        .builder()
        .username("test")
        .password("{noop}" + password)
        .authorities(new ArrayList<>())
        .enabled(true)
        .credentialExpiration(ZonedDateTime.now().plusMinutes(1L))
        .expiration(ZonedDateTime.now().plusMinutes(1L))
        .locked(false)
        .newData(true)
        .build();

    StepVerifier.create(repository.deleteAll()).verifyComplete();

    StepVerifier
        .create(repository.findAll())
        .verifyComplete();

    StepVerifier
        .create(repository.save(user))
        .expectNextCount(1)
        .verifyComplete();

    StepVerifier
        .create(repository.findAll())
        .expectNextCount(1)
        .verifyComplete();
  }

  @And("I have logged in")
  public void andGiveRandomToken() {

    token =
        doPost("/login", new AuthRequest("test", password), AuthRequest.class)
            .expectStatus().isEqualTo(200)
            .expectBody(AuthResponse.class)
            .consumeWith(System.out::println)
            .returnResult()
            .getResponseBody()
            .getToken();
  }

  @When("I fetch companies at {string}")
  public void whenFetch(String uri) {
    result = doGet(uri, new HashMap<>(), token);
  }

  @Then("I should get status {int} and {int} entries")
  public void thenCheck(int status, int quantity) {

    result
        .expectStatus().isEqualTo(status)
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.length()").isEqualTo(quantity);

    StepVerifier.create(repository.deleteAll()).verifyComplete();

  }


}
