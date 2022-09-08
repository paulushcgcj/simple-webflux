package io.github.paulushcgcj.devopsdemo.services;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.exceptions.NullCompanyException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import io.github.paulushcgcj.devopsdemo.repositories.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Unit Test | Company Service")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompanyServiceTest {

  private final CompanyService service = new CompanyService();
  private final CompanyRepository repository = mock(CompanyRepository.class);

  @BeforeEach
  public void setUp() {
    service.setCompanyRepository(repository);
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
    when(repository.findAll())
        .thenReturn(Flux.empty());

    StepVerifier
        .create(service.listCompanies(0, 10, null))
        .expectNext(new ArrayList<>())
        .verifyComplete();
  }

  @Test
  @DisplayName("Insert works")
  void shouldAddCompany() {

    when(repository.save(any(Company.class)))
        .thenReturn(Mono.just(Company.builder().id(UUID.randomUUID().toString()).name("DaCompany").build()));

    when(repository.findAll())
        .thenReturn(Flux.empty());

    StepVerifier
        .create(service.addCompany(Company.builder().name("DaCompany").build()))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  @DisplayName("Get company when Exists")
  void shouldGetExistingCompany() {

    when(repository.findById(anyString()))
        .thenReturn(Mono.just(Company.builder().name("DaCompany").build()));

    StepVerifier
        .create(service.getCompany(UUID.randomUUID().toString()))
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
    when(repository.findById(anyString()))
        .thenReturn(Mono.empty());

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

      when(repository.findById(anyString()))
          .thenReturn(Mono.just(company.withId(id)));

      StepVerifier
          .create(service.updateCompany(id, company))
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

    when(repository.findById(anyString()))
        .thenReturn(Mono.just(Company.builder().id(UUID.randomUUID().toString()).name("DaCompany").build()));

    when(repository.deleteById(anyString()))
        .thenReturn(Mono.empty());

    StepVerifier
        .create(service.removeCompany(UUID.randomUUID().toString()))
        .verifyComplete();

  }

  @Test
  @DisplayName("Don't remove cuz it's not there")
  void shouldNotRemoveCompany() {

    when(repository.findById(anyString()))
        .thenReturn(Mono.empty());

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
            Arguments.of(UUID.randomUUID().toString(), Company.builder().name("LeCompany").build(), null)
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
