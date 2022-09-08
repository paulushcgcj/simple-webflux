package io.github.paulushcgcj.devopsdemo.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import io.github.paulushcgcj.devopsdemo.services.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Stream;

@AutoConfigureWebTestClient(timeout = "1000")
@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@DisplayName("Integrated Test | Company Controller")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyControllerIntegrationTest {

  @Autowired
  private WebTestClient client;

  @Autowired
  CompanyService service;

  private static final Company daCompany =
      Company
          .builder()
          .name("DaCompany")
          .permalink("https://mail.ca")
          .email("mail@mail.com")
          .phone("+12505205522")
          .description("Desc")
          .overview("Over view")
          .build();

  private static final Company leCompany =
      Company
          .builder()
          .name("LeCompany")
          .permalink("https://mail.ca")
          .email("mail@mail.com")
          .phone("+12505205522")
          .description("Desc")
          .overview("Over view")
          .build();


  @Test
  @DisplayName("One Company after Insert")
  @Order(3)
  void shouldListCreatedCompany() throws Exception {
    client
        .get()
        .uri(URI.create("/api/companies"))
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.[*].name").isEqualTo("DaCompany");
  }

  @Test
  @DisplayName("Look for DaCompany, not Gork")
  @Order(4)
  void shouldListCompanyByName() throws Exception {

    client
        .get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/api/companies")
                .queryParam("name", "Gork")
                .build()
        )
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .json("[]");

    client
        .get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/api/companies")
                .queryParam("name", "DaCompany")
                .build()
        )
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.[0].name").isEqualTo(daCompany.getName())
        .jsonPath("$.[0].email").isEqualTo(daCompany.getEmail())
        .jsonPath("$.[0].description").isEqualTo(daCompany.getDescription())
        .jsonPath("$.[0].permalink").isEqualTo(daCompany.getPermalink())
        .jsonPath("$.[0].overview").isEqualTo(daCompany.getOverview())
        .jsonPath("$.[0].phone").isEqualTo(daCompany.getPhone());

  }


  @Test
  @DisplayName("No Companies at the beginning")
  @Order(1)
  void shouldHaveNoCompaniesOnList() throws Exception {

    client
        .get()
        .uri(URI.create("/api/companies"))
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .json("[]");
  }

  @Test
  @DisplayName("Insert works")
  @Order(2)
  void shouldAddCompany() throws Exception {

    client
        .post()
        .uri("/api/companies")
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(daCompany), Company.class)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader()
        .exists("Location")
        .expectBody()
        .isEmpty();
  }

  @Test
  @DisplayName("Get company when Exists")
  @Order(5)
  void shouldGetExistingCompany() throws Exception {
    String id = service
        
        .listCompanies(0, 1, "DaCompany")
        .map(companies -> companies.get(0))
        .map(Company::getId)
        .block();

    client
        .get()
        .uri("/api/companies/{id}", id)
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.name").isEqualTo(daCompany.getName());
  }

  @Test
  @DisplayName("Get no Company with unexpected ID")
  @Order(6)
  void shouldGetNoCompanyWithId() throws Exception {
    UUID id = UUID.randomUUID();

    client
        .get()
        .uri("/api/companies/{id}", id)
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class)
        .isEqualTo("No company with id " + id + " found")
        .consumeWith(System.out::println);
  }

  @ParameterizedTest
  @MethodSource("updateCases")
  @DisplayName("Update and see what happens")
  @Order(7)
  void shouldExecuteUpdateAndHopeForTheBest(String id, Company company, ResponseStatusException exception) throws Exception {

    if (exception == null) {

      String myid = service
          
          .listCompanies(0, 1, "DaCompany")
          .map(companies -> companies.get(0))
          .map(Company::getId)
          .block();

      client
          .put()
          .uri("/api/companies/{id}", myid)
          .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .body(Mono.just(company), Company.class)
          .exchange()
          .expectStatus().isAccepted()
          .expectBody()
          .isEmpty();

    } else {
      client
          .put()
          .uri("/api/companies/{id}", id)
          .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .body(Mono.just(company), Company.class)
          .exchange()
          .expectStatus().isEqualTo(exception.getRawStatusCode())
          .expectBody(String.class)
          .isEqualTo(exception.getReason());
    }

  }

  @Test
  @DisplayName("Remove company")
  @Order(8)
  void shouldRemoveCompany() throws Exception {

    String id = service
        
        .listCompanies(0, 1, leCompany.getName())
        .map(companies -> companies.get(0))
        .map(Company::getId)
        .block();

    client
        .delete()
        .uri("/api/companies/{id}", id)
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNoContent()
        .expectBody()
        .isEmpty();

    shouldHaveNoCompaniesOnList();
  }

  @Test
  @DisplayName("Don't remove cuz it's not there")
  @Order(9)
  void shouldNotRemoveCompany() throws Exception {
    UUID id = UUID.randomUUID();

    client
        .delete()
        .uri("/api/companies/{id}", id)
        .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class)
        .isEqualTo("No company with id " + id + " found");
  }

  private HttpHeaders getHttpHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    httpHeaders.add("User-Agent", "JUnit5");
    return httpHeaders;
  }

  private static Stream<Arguments> updateCases() {
    String randomId = UUID.randomUUID().toString();
    return
        Stream.of(
            Arguments.of(randomId, leCompany, new CompanyNotFoundException(randomId)),
            Arguments.of(randomId, leCompany, null)
        );
  }

  private static Stream<Arguments> invalidAddCases() {
    return
        Stream.of(Arguments.of(daCompany, new CompanyAlreadyExistException(daCompany.getName())));
  }

}
