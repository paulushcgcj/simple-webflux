
package io.github.paulushcgcj.devopsdemo.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Table("companies")
public class Company implements Persistable<String> {

  @Id
  private String id;
  @NotNull
  @NotEmpty
  private String name;
  @NotNull
  @NotEmpty
  private String permalink;
  @NotNull
  @NotEmpty
  private String email;
  @NotNull
  @NotEmpty
  private String phone;
  @NotNull
  @NotEmpty
  private String description;
  @NotNull
  @NotEmpty
  private String overview;

  @Transient
  @Builder.Default
  @JsonIgnore
  private boolean newData = false;

  @Override
  @JsonIgnore
  public boolean isNew() {
    return this.newData || id == null;
  }
}
