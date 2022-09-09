package io.github.paulushcgcj.devopsdemo.endpoints;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTestContainerIntegrationTest {

  @Container
  private static final MariaDBContainer database = new MariaDBContainer("mariadb:10.3");

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {



    registry.add("io.github.paulushcgcj.database", AbstractTestContainerIntegrationTest::r2dbcUrl);
    registry.add("io.github.paulushcgcj.username", database::getUsername);
    registry.add("io.github.paulushcgcj.password", database::getPassword);

  }

  private static String r2dbcUrl() {
    return String.format("mariadb://%s:%s/%s?TC_IMAGE_TAG=10.3",
        database.getHost(),
        database.getMappedPort(3306),
        database.getDatabaseName());
  }

}
