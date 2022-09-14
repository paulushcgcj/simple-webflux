
package io.github.paulushcgcj.devopsdemo.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
@Table(value = "companies",schema = "company")
public class Company implements Persistable<String> {

  @Id
  private String id;

  @NotNull
  @NotEmpty
  private String name;

  @NotNull
  @NotEmpty
  @Pattern(regexp = "(https?:\\/\\/)?([\\w\\-])+\\.{1}([a-zA-Z]{2,63})([\\/\\w-]*)*\\/?\\??([^#\\n\\r]*)?#?([^\\n\\r]*)",message = "Permalink should be a hyperlink")
  private String permalink;

  @NotNull
  @NotEmpty
  @Pattern(regexp = "([a-z])+@([a-z])+\\.com", message = "Email should match the pattern a-z @ a-z .com")
  private String email;

  @NotNull
  @NotEmpty
  @Pattern(regexp = "^\\+[1-9]\\d{1,14}$",message = "Phone should follow E164 pattern")
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
