package io.github.paulushcgcj.devopsdemo.entities;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;

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
@Table(value = "authority_list", schema = "security")
public class Authority implements GrantedAuthority {
  private String authority;
}