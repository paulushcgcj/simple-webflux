package io.github.paulushcgcj.devopsdemo.services;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.exceptions.NullCompanyException;
import io.github.paulushcgcj.devopsdemo.models.Company;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test | Company Service")
class CompanyServiceTest {

  private final CompanyService service = new CompanyService();

  @BeforeEach
  public void setUp(){
    service.getCompanyRepository().clear();
  }

  @Test
  @DisplayName("One Company after Insert")
  void shouldListCreatedCompany(){
    shouldHaveNoCompaniesOnList();
    shouldAddCompany();
    assertThat(service.listCompanies(0,10,null))
        .isNotNull()
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Look for DaCompany, not Gork")
  void shouldListCompanyByName(){
    shouldAddCompany();

    assertThat(service.listCompanies(0,10,"Gork"))
        .isNotNull()
        .isEmpty();

    assertThat(service.listCompanies(0,10,"DaCompany"))
        .isNotNull()
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("No Companies at the beginning")
  void shouldHaveNoCompaniesOnList(){
    assertThat(service.listCompanies(0,10,null))
        .isNotNull()
        .isEmpty();
  }

  @Test
  @DisplayName("Insert works")
  void shouldAddCompany(){
    assertNotNull(service.addCompany(Company.builder().name("DaCompany").build()));
  }

  @ParameterizedTest
  @MethodSource("invalidAddCases")
  @DisplayName("Invalid Add Cases")
  void shoulNotInsertInvalidCompany(Company company,Class exception) {
    shouldAddCompany();
    assertThrows(exception,() -> service.addCompany(company));
  }

  @Test
  @DisplayName("Get company when Exists")
  void shouldGetExistingCompany(){
    shouldAddCompany();
    String id = service.listCompanies(0,1,"DaCompany").get(0).getId();

    assertThat(service.getCompany(id))
        .isNotNull()
        .hasFieldOrPropertyWithValue("name","DaCompany")
        .hasFieldOrPropertyWithValue("id",id);

  }

  @Test
  @DisplayName("Get no Company with unexpected ID")
  void shouldGetNoCompanyWithId(){
    assertThrows(CompanyNotFoundException.class,() ->service.getCompany(UUID.randomUUID().toString()));
  }

  @ParameterizedTest
  @MethodSource("updateCases")
  @DisplayName("Update and see what happens")
  void shouldExecuteUpdateAndHopeForTheBest(String id,Company company,Class exception) {

    if(exception == null){

      shouldAddCompany();
      String myid = service.listCompanies(0,1,"DaCompany").get(0).getId();

      service.updateCompany(myid, company);
      assertThat(service.getCompany(myid))
          .isNotNull()
          .hasFieldOrPropertyWithValue("name",company.getName());

    }else{
      assertThrows(exception,() -> service.updateCompany(id, company));
    }

  }

  @Test
  @DisplayName("Remove company")
  void shouldRemoveCompany(){
    shouldAddCompany();
    String id = service.listCompanies(0,1,"DaCompany").get(0).getId();
    service.removeCompany(id);
    shouldHaveNoCompaniesOnList();
  }

  @Test
  @DisplayName("Don't remove cuz it's not there")
  void shouldNotRemoveCompany(){
    assertThrows(CompanyNotFoundException.class,() -> service.removeCompany(UUID.randomUUID().toString()));
  }

  private static Stream<Arguments> updateCases() {
    return
        Stream.of(
            Arguments.of(null,null,NullCompanyException.class),
            Arguments.of(UUID.randomUUID().toString(),null,NullCompanyException.class),
            Arguments.of(null,Company.builder().name("LeCompany").build(),CompanyNotFoundException.class),
            Arguments.of(null,Company.builder().name("LeCompany").build(),null)
        );
  }

  private static Stream<Arguments> invalidAddCases() {
    return
        Stream.of(
            Arguments.of(null,NullCompanyException.class),
            Arguments.of(Company.builder().name("DaCompany").build(), CompanyAlreadyExistException.class)
        );
  }

}
