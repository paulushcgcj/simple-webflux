package io.github.paulushcgcj.devopsdemo.utils;

import java.util.function.Consumer;

import org.springdoc.core.fn.builders.operation.Builder;

import io.github.paulushcgcj.devopsdemo.entities.Company;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.arrayschema.Builder.arraySchemaBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.header.Builder.headerBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springdoc.core.fn.builders.securityrequirement.Builder.securityRequirementBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwaggerUtils {


  public static <T> Consumer<Builder> buildApi(String operation, String description, String tag, Class<T> tClass) {
    return ops ->
        ops
            .tag(tag)
            .beanClass(tClass)
            .description(description)
            .beanMethod(operation)
            .operationId(operation)
            .security(securityRequirementBuilder().name("bearer-key"))
        ;
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder responseOps(
      String responseCode,
      String description,
      org.springdoc.core.fn.builders.content.Builder schema
  ) {
    return responseBuilder()
        .responseCode(responseCode)
        .description(description)
        .content(schema);
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder notFoundOps(
      String example
  ) {
    return responseOps(
        "404",
        "Not Found",
        contentBuilder().schema(SwaggerUtils.classSchema(String.class, example))
    );
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder okOps(
      Class<Company> schema
  ) {
    return responseOps(
        "200",
        "OK",
        contentBuilder().schema(classSchema(schema))
    );
  }


  public static org.springdoc.core.fn.builders.apiresponse.Builder okListOps(
      Class<Company> schema
  ) {
    return responseOps(
        "200",
        "OK",
        contentBuilder().array(arraySchemaBuilder().schema(classSchema(schema)))
    );
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder badRequestOps(
      String example
  ) {
    return responseOps(
        "400",
        "Bad Request, Validation failed",
        contentBuilder().schema(SwaggerUtils.classSchema(String.class, example))
    );
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder conflictOps(
      String example
  ) {
    return responseOps(
        "409",
        "Conflict",
        contentBuilder().schema(SwaggerUtils.classSchema(String.class, example))
    );
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder createdOps(String exampleSchema) {
    return responseOps(
        "201",
        "Created",
        contentBuilder().schema(schemaBuilder())
    )
        .header(
            headerBuilder()
                .name("Location")
                .description("The location header pointing out to the content created")
                .schema(schemaBuilder().implementation(String.class).example(exampleSchema))
        );
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder acceptedOps() {
    return responseOps(
        "202",
        "Accepted",
        contentBuilder().schema(schemaBuilder())
    );
  }

  public static org.springdoc.core.fn.builders.apiresponse.Builder noContentOps() {
    return responseOps(
        "204",
        "No Content",
        contentBuilder().schema(schemaBuilder())
    );
  }

  public static <T> org.springdoc.core.fn.builders.schema.Builder classSchema(Class<T> tClass) {
    return schemaBuilder().implementation(Company.class);
  }

  public static <T> org.springdoc.core.fn.builders.schema.Builder classSchema(Class<T> tClass, String example) {
    return schemaBuilder().implementation(Company.class).example(example);
  }

  public static <T> org.springdoc.core.fn.builders.requestbody.Builder requestBodyOps(Class<T> tClass) {
    return requestBodyBuilder()
        .content(contentBuilder().schema(schemaBuilder().implementation(tClass)));
  }

}
