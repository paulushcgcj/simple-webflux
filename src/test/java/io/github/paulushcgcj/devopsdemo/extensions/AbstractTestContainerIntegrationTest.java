package io.github.paulushcgcj.devopsdemo.extensions;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@Testcontainers
@AutoConfigureWebTestClient(timeout = "36000")
@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public abstract class AbstractTestContainerIntegrationTest {

  @Autowired
  protected WebTestClient client;

  static final PostgreSQLContainer database;


  static {
    database = new PostgreSQLContainer("postgres")
        .withDatabaseName("simple")
        .withUsername("simple")
        .withPassword(UUID.randomUUID().toString());
    database.start();
  }

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("io.github.paulushcgcj.database.name", () -> database.getDatabaseName()
        .concat("?TC_INITSCRIPT=file:src/test/resources/init_pg.sql"));
    registry.add("io.github.paulushcgcj.database.host",
        () -> String.format("%s:%d", database.getHost(), database.getMappedPort(5432)));
    registry.add("io.github.paulushcgcj.database.username", database::getUsername);
    registry.add("io.github.paulushcgcj.database.password", database::getPassword);
  }

  protected WebTestClient.ResponseSpec doGet(String uri) {
    return doGet(uri, null);
  }

  protected WebTestClient.ResponseSpec doGet(String uri, Map<String, String> queryParams) {
    return doGet(uri, queryParams, "token");
  }

  protected WebTestClient.ResponseSpec doGet(String uri, Map<String, String> queryParams,
                                             String bearer) {

    if (queryParams == null) {
      return
          client
              .get()
              .uri(URI.create(uri))
              .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders(bearer)))
              .exchange();
    }
    return
        client
            .get()
            .uri(uriBuilder ->
                uriBuilder
                    .path(uri)
                    .queryParams(buildMvM(queryParams))
                    .build()
            )
            .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders(bearer)))
            .exchange();
  }

  protected <T> WebTestClient.ResponseSpec doPost(String uri, T content, Class<T> clazz) {
    return
        client
            .post()
            .uri(uri)
            .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders("token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(content), clazz)
            .exchange();
  }

  protected <T> WebTestClient.ResponseSpec doPut(String uri, T content, Class<T> clazz) {
    return
        client
            .put()
            .uri(uri)
            .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders("token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(content), clazz)
            .exchange();
  }

  protected WebTestClient.ResponseSpec doDelete(String uri) {
    return
        client
            .delete()
            .uri(URI.create(uri))
            .headers(httpHeaders -> httpHeaders.putAll(getHttpHeaders("token")))
            .exchange();
  }

  private HttpHeaders getHttpHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    httpHeaders.add(HttpHeaders.USER_AGENT, "JUnit5-Cucumber");
    return httpHeaders;
  }

  private HttpHeaders getHttpHeaders(String bearer) {
    HttpHeaders httpHeaders = getHttpHeaders();
    if (StringUtils.isNotBlank(bearer)) {
      httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearer);
    }
    return httpHeaders;
  }

  private MultiValueMap<String, String> buildMvM(Map<String, String> values) {
    return new LinkedMultiValueMap<>(
        values
            .entrySet()
            .stream()
            .map(entry -> Map.of(entry.getKey(), List.of(entry.getValue())))
            .reduce(Map.of(), (map1, map2) -> {
              Map<String, List<String>> map = new HashMap<>();
              map.putAll(map1);
              map.putAll(map2);
              return map;
            })
    );
  }


}
