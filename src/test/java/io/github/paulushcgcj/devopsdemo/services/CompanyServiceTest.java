package io.github.paulushcgcj.devopsdemo.services;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.exceptions.NullCompanyException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Unit Test | Company Service")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompanyServiceTest {

  private final CompanyService service = new CompanyService();

  @BeforeEach
  public void setUp() {
    service.getCompanyRepository().clear();
  }

  @Test
  @DisplayName("One Company after Insert")
  void shouldListCreatedCompany() {
    shouldHaveNoCompaniesOnList();
    shouldAddCompany();

    StepVerifier
        .create(service.listCompanies(0, 10, null))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  @DisplayName("Look for DaCompany, not Gork")
  void shouldListCompanyByName() {
    shouldAddCompany();

    StepVerifier
        .create(service.listCompanies(0, 10, "Gork"))
        .expectNext(new ArrayList<>())
        .verifyComplete();

    StepVerifier
        .create(service.listCompanies(0, 10, "DaCompany"))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  @DisplayName("No Companies at the beginning")
  void shouldHaveNoCompaniesOnList() {
    StepVerifier
        .create(service.listCompanies(0, 10, null))
        .expectNext(new ArrayList<>())
        .verifyComplete();
  }

  @Test
  @DisplayName("Insert works")
  void shouldAddCompany() {

    StepVerifier
        .create(service.addCompany(Company.builder().name("DaCompany").build()))
        .expectNextCount(1)
        .verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("invalidAddCases")
  @DisplayName("Invalid Add Cases")
  void shoulNotInsertInvalidCompany(Company company, Class exception) {
    shouldAddCompany();

    StepVerifier
        .create(service.addCompany(company))
        .expectError(exception)
        .verify();

  }

  @Test
  @DisplayName("Get company when Exists")
  void shouldGetExistingCompany() {
    shouldAddCompany();

    StepVerifier
        .create(
            service
                .listCompanies(0, 1, "DaCompany")
                .map(companies -> companies.get(0))
                .map(Company::getId)
                .flatMap(service::getCompany)
        )
        .assertNext(company ->
            assertThat(company)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "DaCompany")
        )
        .verifyComplete();

  }

  @Test
  @DisplayName("Get no Company with unexpected ID")
  void shouldGetNoCompanyWithId() {
    StepVerifier
        .create(service.getCompany(UUID.randomUUID().toString()))
        .expectError(CompanyNotFoundException.class)
        .verify();
  }

  @ParameterizedTest
  @MethodSource("updateCases")
  @DisplayName("Update and see what happens")
  void shouldExecuteUpdateAndHopeForTheBest(String id, Company company, Class exception) {

    if (exception == null) {

      shouldAddCompany();
      StepVerifier
          .create(
              service
                  .listCompanies(0, 1, "DaCompany")
                  .map(companies -> companies.get(0))
                  .map(Company::getId)
                  .flatMap(myId -> service.updateCompany(myId, company))
          )
          .verifyComplete();

      StepVerifier
          .create(
              service
                  .listCompanies(0, 1, company.getName())
                  .map(companies -> companies.get(0))
                  .map(Company::getId)
                  .flatMap(service::getCompany)
                  .map(Company::getName)
          )
          .expectNext(company.getName())
          .verifyComplete();

    } else {
      StepVerifier
          .create(service.updateCompany(id, company))
          .expectError(exception)
          .verify();
    }

  }

  @Test
  @DisplayName("Remove company")
  void shouldRemoveCompany() {
    shouldAddCompany();

    StepVerifier
        .create(
            service
                .listCompanies(0, 1, "DaCompany")
                .map(companies -> companies.get(0))
                .map(Company::getId)
                .flatMap(service::removeCompany)
        )
        .verifyComplete();

    shouldHaveNoCompaniesOnList();
  }

  @Test
  @DisplayName("Don't remove cuz it's not there")
  void shouldNotRemoveCompany() {
    StepVerifier
        .create(service.removeCompany(UUID.randomUUID().toString()))
        .expectError(CompanyNotFoundException.class)
        .verify();
  }

  private static Stream<Arguments> updateCases() {
    return
        Stream.of(
            Arguments.of(null, null, NullCompanyException.class),
            Arguments.of(UUID.randomUUID().toString(), null, NullCompanyException.class),
            Arguments.of(null, Company.builder().name("LeCompany").build(), CompanyNotFoundException.class),
            Arguments.of(null, Company.builder().name("LeCompany").build(), null)
        );
  }

  private static Stream<Arguments> invalidAddCases() {
    return
        Stream.of(
            Arguments.of(null, NullCompanyException.class),
            Arguments.of(Company.builder().name("DaCompany").build(), CompanyAlreadyExistException.class)
        );
  }

}
