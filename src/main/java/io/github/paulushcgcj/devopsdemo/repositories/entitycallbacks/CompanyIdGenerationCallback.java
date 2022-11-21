package io.github.paulushcgcj.devopsdemo.repositories.entitycallbacks;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.core.annotation.Order;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.stereotype.Component;

import io.github.paulushcgcj.devopsdemo.entities.Company;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Order(1)
@Slf4j
public class CompanyIdGenerationCallback implements BeforeSaveCallback<Company> {

  @Override
  public Publisher<Company> onBeforeSave(Company entity, OutboundRow row, SqlIdentifier table) {

    String id = UUID.randomUUID().toString();

    return Mono
        .just(entity)
        .filter(company -> StringUtils.isBlank(company.getId()))
        .map(company -> company.withId(id))
        .doOnNext(company -> row.append("id",Parameter.from(company.getId())))
        .switchIfEmpty(Mono.just(entity));
  }

}
