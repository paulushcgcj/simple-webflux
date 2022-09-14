package io.github.paulushcgcj.devopsdemo.bdd;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.github.paulushcgcj.devopsdemo.extensions.AbstractTestContainerIntegrationTest;
import io.github.paulushcgcj.devopsdemo.repositories.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@ContextConfiguration
@CucumberContextConfiguration
@Slf4j
public class CompanyIntegrationTest extends AbstractTestContainerIntegrationTest {

  @Autowired
  private CompanyRepository repository;

  private WebTestClient.ResponseSpec result;

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
    StepVerifier
        .create(
            repository
                .findAll()
                .doOnNext(System.out::println)
        )
        .verifyComplete();
  }

  @When("I fetch companies at {string}")
  public void whenFetch(String uri) {
    result = doGet(uri);
  }

  @Then("I should get status {int} and {int} entries")
  public void thenCheck(int status, int quantity) {

    result
        .expectStatus().isEqualTo(status)
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.length()").isEqualTo(quantity);

  }


}
