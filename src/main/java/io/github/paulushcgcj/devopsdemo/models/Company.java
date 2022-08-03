
package io.github.paulushcgcj.devopsdemo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class Company {

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
}
