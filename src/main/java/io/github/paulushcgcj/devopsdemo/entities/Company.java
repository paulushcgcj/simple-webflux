
package io.github.paulushcgcj.devopsdemo.entities;

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

import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(name = "id",description = "ID of the object on database, should be a UUID",example = "84cd6994-97ad-3170-a58f-77974c0c6276")
  private String id;

  @NotNull
  @NotEmpty
  @Schema(name = "name",description = "Name of the company",example = "ACME Ltd")
  private String name;

  @NotNull
  @NotEmpty
  @Pattern(regexp = "(https?:\\/\\/)?([\\w\\-])+\\.{1}([a-zA-Z]{2,63})([\\/\\w-]*)*\\/?\\??([^#\\n\\r]*)?#?([^\\n\\r]*)",message = "Permalink should be a hyperlink")
  @Schema(name = "permalink",description = "The website of the company, in a form of a hyperlink",example = "https://acme.ca")
  private String permalink;

  @NotNull
  @NotEmpty
  @Pattern(regexp = "([a-z])+@([a-zA-Z0-9._-])+\\.([a-z])+", message = "Email should match the pattern (a-z)+@(a-zA-Z0-9._-)+\\.(a-z)+")
  @Schema(name = "email",description = "Email address to get in touch",example = "contact@acme.ca")
  private String email;

  @NotNull
  @NotEmpty
  @Pattern(regexp = "^\\+?[1-9]([0-9-]){1,14}$",message = "Phone should follow E164 pattern")
  @Schema(name = "phone",description = "Contact phone",example = "250-578-3345")
  private String phone;

  @NotNull
  @NotEmpty
  @Schema(name = "description",description = "A description of what the company does",example = "ACME is a company that does stuff, and does it great.")
  private String description;

  @NotNull
  @NotEmpty
  @Schema(name = "overview",description = "Overview of all operations this company does",example = "We does all sorts of work such as...")
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
