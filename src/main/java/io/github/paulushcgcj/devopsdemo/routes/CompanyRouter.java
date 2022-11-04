package io.github.paulushcgcj.devopsdemo.routes;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.github.paulushcgcj.devopsdemo.entities.Company;
import io.github.paulushcgcj.devopsdemo.handlers.CompanyHandler;
import io.github.paulushcgcj.devopsdemo.utils.SwaggerUtils;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;

import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@RequiredArgsConstructor
public class CompanyRouter extends BaseRouter {

  public static final String ID = "/{id}";

  private final CompanyHandler handler;

  @Override
  protected String basePath() {
    return "/api/companies";
  }

  @Override
  protected String routeTagName() {
    return "Company";
  }

  @Override
  protected String routeTagDescription() {
    return "Manages companies";
  }

  protected Consumer<Builder> listCompaniesOps() {
    return SwaggerUtils.buildApi(
            "listCompanies", "List available companies", routeTagName(), CompanyHandler.class)
        .andThen(ops -> ops.response(SwaggerUtils.okListOps(Company.class)));
  }

  protected Consumer<Builder> addCompanyOps() {
    return SwaggerUtils.buildApi(
            "addCompany", "Add a new company to the system", routeTagName(), CompanyHandler.class)
        .andThen(
            ops ->
                ops.response(
                        SwaggerUtils.createdOps(
                            "/api/companies/f15afcb4-c46a-3a45-82a8-84280b89ae38"))
                    .response(SwaggerUtils.badRequestOps("Name is required"))
                    .response(
                        SwaggerUtils.conflictOps("A company with name example already exists"))
                    .requestBody(SwaggerUtils.requestBodyOps(Company.class)));
  }

  protected Consumer<Builder> getCompanyOps() {
    return SwaggerUtils.buildApi(
            "getCompany",
            "Get the details of a company from its ID",
            routeTagName(),
            CompanyHandler.class)
        .andThen(
            ops ->
                ops.response(SwaggerUtils.okOps(Company.class))
                    .response(
                        SwaggerUtils.notFoundOps(
                            "No company with id f15afcb4-c46a-3a45-82a8-84280b89ae38 found"))
                    .parameter(
                        parameterBuilder()
                            .in(ParameterIn.PATH)
                            .name("id")
                            .schema(
                                schemaBuilder()
                                    .implementation(String.class)
                                    .example("f15afcb4-c46a-3a45-82a8-84280b89ae38"))));
  }

  protected Consumer<Builder> updateCompanyOps() {
    return SwaggerUtils.buildApi(
            "updateCompany",
            "Update the details of a company",
            routeTagName(),
            CompanyHandler.class)
        .andThen(
            ops ->
                ops.response(SwaggerUtils.acceptedOps())
                    .response(
                        SwaggerUtils.notFoundOps(
                            "No company with id f15afcb4-c46a-3a45-82a8-84280b89ae38 found"))
                    .parameter(
                        parameterBuilder()
                            .in(ParameterIn.PATH)
                            .name("id")
                            .schema(
                                schemaBuilder()
                                    .implementation(String.class)
                                    .example("f15afcb4-c46a-3a45-82a8-84280b89ae38")))
                    .requestBody(SwaggerUtils.requestBodyOps(Company.class)));
  }

  protected Consumer<Builder> removeCompanyOps() {
    return SwaggerUtils.buildApi(
            "removeCompany",
            "Remove a company based on its ID",
            routeTagName(),
            CompanyHandler.class)
        .andThen(
            ops ->
                ops.response(SwaggerUtils.noContentOps())
                    .response(
                        SwaggerUtils.notFoundOps(
                            "No company with id f15afcb4-c46a-3a45-82a8-84280b89ae38 found"))
                    .parameter(
                        parameterBuilder()
                            .in(ParameterIn.PATH)
                            .name("id")
                            .schema(
                                schemaBuilder()
                                    .implementation(String.class)
                                    .example("f15afcb4-c46a-3a45-82a8-84280b89ae38"))));
  }

  @Override
  public RouterFunction<ServerResponse> routerRoute() {
    return Stream.of(
            route()
                .GET(
                    StringUtils.EMPTY,
                    accept(MediaType.APPLICATION_JSON),
                    handler::listCompanies,
                    listCompaniesOps())
                .build(),
            route()
                .POST(
                    StringUtils.EMPTY,
                    accept(MediaType.APPLICATION_JSON),
                    handler::addCompany,
                    addCompanyOps())
                .build(),
            route()
                .GET(ID, accept(MediaType.APPLICATION_JSON), handler::getCompany, getCompanyOps())
                .build(),
            route()
                .PUT(
                    ID,
                    accept(MediaType.APPLICATION_JSON),
                    handler::updateCompany,
                    updateCompanyOps())
                .build(),
            route()
                .DELETE(
                    ID,
                    accept(MediaType.APPLICATION_JSON),
                    handler::removeCompany,
                    removeCompanyOps())
                .build())
        .reduce(RouterFunction::and)
        .orElseThrow();
  }
}
