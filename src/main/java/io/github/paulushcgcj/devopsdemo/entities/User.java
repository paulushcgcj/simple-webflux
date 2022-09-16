package io.github.paulushcgcj.devopsdemo.entities;

import java.time.ZonedDateTime;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.userdetails.UserDetails;

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
@Table(value = "users", schema = "security")
public class User implements UserDetails {

  @Id
  @NotNull
  @NotEmpty
  private String username;

  @NotNull
  @NotEmpty
  private String password;

  private boolean enabled = true;
  private boolean locked = false;
  private ZonedDateTime expiration;

  @NotNull
  @NotEmpty
  private ZonedDateTime credentialExpiration;

  @Singular
  @Transient
  private Set<Authority> authorities;

  @Override
  public boolean isAccountNonExpired() {
    if (expiration != null) {
      expiration.isBefore(ZonedDateTime.now());
    }
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    if (credentialExpiration != null) {
      credentialExpiration.isBefore(ZonedDateTime.now());
    }
    return true;
  }

}
