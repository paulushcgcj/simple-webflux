package io.github.paulushcgcj.devopsdemo.bdd;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.paulushcgcj.devopsdemo.components.JwtSecurityComponent;
import io.github.paulushcgcj.devopsdemo.dtos.AuthRequest;
import io.github.paulushcgcj.devopsdemo.entities.User;
import io.github.paulushcgcj.devopsdemo.extensions.AbstractTestContainerIntegrationTest;
import io.github.paulushcgcj.devopsdemo.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertTrue;

@ContextConfiguration

@Slf4j
public class SecurityIntegrationTest extends AbstractTestContainerIntegrationTest {

  @Autowired
  private UserRepository repository;

  @Autowired
  private JwtSecurityComponent jwt;

  private WebTestClient.ResponseSpec result;
  private String token;

  @Given("User {string} exists with password {string}")
  public void givenUser(String userName, String password) {
    User user = User
        .builder()
        .username(userName)
        .password("{noop}" + password)
        .authorities(new ArrayList<>())
        .enabled(true)
        .credentialExpiration(ZonedDateTime.now().plusMinutes(5L))
        .expiration(ZonedDateTime.now().plusMinutes(5L))
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

  @When("I call {string} with user {string} and password {string}")
  public void whenILogInWith(String uri, String user, String password) {
    result = doPost(uri, new AuthRequest(user, password), AuthRequest.class);
  }

  @Then("I should receive back {int} and check {int} JWT token")
  public void thenShouldCheckStatusAndToken(int status, int valid) {

    result
        .expectStatus().isEqualTo(status)
        .expectBody()
        .consumeWith(System.out::println)
        .consumeWith(exchangeResult -> {
          String content = new String(exchangeResult.getResponseBody(), Charset.defaultCharset());

          if (valid == 1) {
            assertTrue(
                jwt.validateToken(
                    content
                        .substring(10)
                        .replace("\"}:", StringUtils.EMPTY)
                )
            );
          }else{
            assertTrue(content.startsWith("Cannot login with username "));
            assertTrue(content.endsWith(" :: not found"));
          }

        });

    StepVerifier.create(repository.deleteAll()).verifyComplete();

  }

}
