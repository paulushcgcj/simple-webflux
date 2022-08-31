package io.github.paulushcgcj.devopsdemo.endpoints;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@DisplayName("Integrated Test | Company Controller")
class CompanyControllerIntegrationTest {

  @Autowired
  private MockMvc client;

  @Autowired
  CompanyController controller;

  private ObjectMapper mapper = new ObjectMapper();

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

  @BeforeEach
  public void setUp() throws JsonProcessingException {
    controller.getService().getCompanyRepository().clear();
  }

  @Test
  @DisplayName("One Company after Insert")
  void shouldListCreatedCompany() throws Exception {
    shouldHaveNoCompaniesOnList();
    shouldAddCompany();
    client
        .perform(get("/api/companies"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*].name").value("DaCompany"));
  }

  @Test
  @DisplayName("Look for DaCompany, not Gork")
  void shouldListCompanyByName() throws Exception {
    shouldAddCompany();

    client
        .perform(get("/api/companies").param("name", "Gork"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    client
        .perform(get("/api/companies").param("name", "DaCompany"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json("[" + mapper.writeValueAsString(daCompany) + "]"));

  }

  @Test
  @DisplayName("No Companies at the beginning")
  void shouldHaveNoCompaniesOnList() throws Exception {

    client
        .perform(get("/api/companies"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }

  @Test
  @DisplayName("Insert works")
  void shouldAddCompany() throws Exception {

    client
        .perform(
            post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(daCompany))
        )
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().string(""));
  }

  @ParameterizedTest
  @MethodSource("invalidAddCases")
  @DisplayName("Invalid Add Cases")
  void shoulNotInsertInvalidCompany(Company company, ResponseStatusException exception) throws Exception {
    shouldAddCompany();

    client
        .perform(
            post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(company))
        )
        .andDo(print())
        .andExpect(status().is(exception.getRawStatusCode()))
        .andExpect(content().string(exception.getReason()));


  }

  @Test
  @DisplayName("Get company when Exists")
  void shouldGetExistingCompany() throws Exception {
    shouldAddCompany();
    String id = controller.getService().listCompanies(0, 1, "DaCompany").get(0).getId();

    client
        .perform(get("/api/companies/" + id))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("DaCompany"));
  }

  @Test
  @DisplayName("Get no Company with unexpected ID")
  void shouldGetNoCompanyWithId() throws Exception {
    UUID id = UUID.randomUUID();

    client
        .perform(get("/api/companies/" + id))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string("No company with id " + id + " found"));
  }

  @ParameterizedTest
  @MethodSource("updateCases")
  @DisplayName("Update and see what happens")
  void shouldExecuteUpdateAndHopeForTheBest(String id, Company company, ResponseStatusException exception) throws Exception {

    if (exception == null) {

      shouldAddCompany();
      String myid = controller.getService().listCompanies(0, 1, "DaCompany").get(0).getId();

      client
          .perform(
              put("/api/companies/" + myid)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(company))
          )
          .andDo(print())
          .andExpect(status().isAccepted())
          .andExpect(content().string(""));

    } else {
      client
          .perform(
              put("/api/companies/" + id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(company))
          )
          .andDo(print())
          .andExpect(status().is(exception.getRawStatusCode()))
          .andExpect(content().string(exception.getReason()));
    }

  }

  @Test
  @DisplayName("Remove company")
  void shouldRemoveCompany() throws Exception {
    shouldAddCompany();
    String id = controller.getService().listCompanies(0, 1, "DaCompany").get(0).getId();

    client
        .perform(delete("/api/companies/" + id))
        .andDo(print())
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));

    shouldHaveNoCompaniesOnList();
  }

  @Test
  @DisplayName("Don't remove cuz it's not there")
  void shouldNotRemoveCompany() throws Exception {
    UUID id = UUID.randomUUID();

    client
        .perform(delete("/api/companies/" + id))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string("No company with id " + id + " found"));
  }

  private static Stream<Arguments> updateCases() {
    return
        Stream.of(
            Arguments.of(null, null, new ResponseStatusException(HttpStatus.BAD_REQUEST, "")),
            Arguments.of(UUID.randomUUID().toString(), null, new ResponseStatusException(HttpStatus.BAD_REQUEST, "")),
            Arguments.of(null, leCompany, new CompanyNotFoundException(null)),
            Arguments.of(null, leCompany, null)
        );
  }

  private static Stream<Arguments> invalidAddCases() {
    return
        Stream.of(
            Arguments.of(null, new ResponseStatusException(HttpStatus.BAD_REQUEST, "")),
            Arguments.of(daCompany, new CompanyAlreadyExistException(daCompany.getName()))
        );
  }

}
