package io.github.paulushcgcj.devopsdemo.endpoints;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.server.ResponseStatusException;

import io.github.paulushcgcj.devopsdemo.entities.Company;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.extensions.AbstractTestContainerIntegrationTest;
import io.github.paulushcgcj.devopsdemo.services.CompanyService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("Integrated Test | Company Controller")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyControllerIntegrationTest extends AbstractTestContainerIntegrationTest {
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
  @WithMockUser("test")
  void shouldListCreatedCompany(){
    doGet("/api/companies")
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.[*].name").isEqualTo("DaCompany");
  }

  @Test
  @DisplayName("Look for DaCompany, not Gork")
  @Order(4)
  @WithMockUser("test")
  void shouldListCompanyByName(){

    doGet("/api/companies", Map.of("name", "Gork"))
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .json("[]");

    doGet("/api/companies", Map.of("name", "DaCompany"))
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
  @WithMockUser("test")
  void shouldHaveNoCompaniesOnList(){

    doGet("/api/companies")
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .json("[]");
  }

  @Test
  @DisplayName("Insert works")
  @Order(2)
  @WithMockUser("test")
  void shouldAddCompany(){

    doPost("/api/companies",daCompany, Company.class)
        .expectStatus().isCreated()
        .expectHeader()
        .exists("Location")
        .expectBody()
        .isEmpty();
  }

  @Test
  @DisplayName("Get company when Exists")
  @Order(5)
  @WithMockUser("test")
  void shouldGetExistingCompany(){

    String id = service
        .listCompanies(0, 1, "DaCompany")
        .map(companies -> companies.get(0))
        .map(Company::getId)
        .block();

    doGet("/api/companies/"+id)
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(System.out::println)
        .jsonPath("$.name").isEqualTo(daCompany.getName());
  }

  @Test
  @DisplayName("Get no Company with unexpected ID")
  @Order(6)
  @WithMockUser("test")
  void shouldGetNoCompanyWithId(){
    UUID id = UUID.randomUUID();

    doGet("/api/companies/"+ id)
        .expectStatus().isNotFound()
        .expectBody(String.class)
        .isEqualTo("No company with id " + id + " found")
        .consumeWith(System.out::println);
  }

  @ParameterizedTest
  @MethodSource("updateCases")
  @DisplayName("Update and see what happens")
  @Order(7)
  @WithMockUser("test")
  void shouldExecuteUpdateAndHopeForTheBest(String id, Company company, ResponseStatusException exception){

    if (exception == null) {

      String myid = service
          .listCompanies(0, 1, "DaCompany")
          .map(companies -> companies.get(0))
          .map(Company::getId)
          .block();

      doPut("/api/companies/"+ myid,company, Company.class)
          .expectStatus().isAccepted()
          .expectBody()
          .isEmpty();

    } else {
      doPut("/api/companies/"+ id,company, Company.class)
          .expectStatus().isEqualTo(exception.getRawStatusCode())
          .expectBody(String.class)
          .isEqualTo(exception.getReason());
    }

  }

  @Test
  @DisplayName("Remove company")
  @Order(8)
  @WithMockUser("test")
  void shouldRemoveCompany(){

    String id = service
        .listCompanies(0, 1, leCompany.getName())
        .map(companies -> companies.get(0))
        .map(Company::getId)
        .block();

    doDelete("/api/companies/"+ id)
        .expectStatus().isNoContent()
        .expectBody()
        .isEmpty();

    shouldHaveNoCompaniesOnList();
  }

  @Test
  @DisplayName("Don't remove cuz it's not there")
  @Order(9)
  @WithMockUser("test")
  void shouldNotRemoveCompany(){
    UUID id = UUID.randomUUID();

    doDelete("/api/companies/"+ id)
        .expectStatus().isNotFound()
        .expectBody(String.class)
        .isEqualTo("No company with id " + id + " found");
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
