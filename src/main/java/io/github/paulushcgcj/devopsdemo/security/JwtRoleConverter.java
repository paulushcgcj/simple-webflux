package io.github.paulushcgcj.devopsdemo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.nimbusds.jose.shaded.gson.JsonArray;

import reactor.core.publisher.Mono;

public class JwtRoleConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

  private static final String REALM_ACCESS = "realm_access";

  @Override
  public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
    return Mono.just(new JwtAuthenticationToken(jwt, getGrantedAuthorities(jwt)));
  }

  private static List<GrantedAuthority> getGrantedAuthorities(Jwt jwt) {

    if (jwt.getClaims().get(REALM_ACCESS) instanceof Map) {
      Map realmAccessMap = (Map) jwt.getClaims().get(REALM_ACCESS);
      if (realmAccessMap.get(REALM_ACCESS) instanceof Map) {
        Map realmClaim = (Map) realmAccessMap.get(REALM_ACCESS);
        if (realmClaim.get("roles") instanceof JsonArray) {
          JsonArray realmRoles = (JsonArray) realmClaim.get("roles");
          return StreamSupport.stream(realmRoles.spliterator(), false)
              .map(roleName -> "ROLE_" + roleName) // prefix to map to a Spring Security "role"
              .map(SimpleGrantedAuthority::new)
              .map(GrantedAuthority.class::cast)
              .toList();
        }
      }
    }

    return new ArrayList<>();
  }
}
