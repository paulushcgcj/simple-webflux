package io.github.paulushcgcj.devopsdemo.components;

import java.security.Key;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.github.paulushcgcj.devopsdemo.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtSecurityComponent {

  @Value("${io.github.paulushcgcj.security.saltSecret}")
  private String secret;

  @Value("${io.github.paulushcgcj.security.expiration}")
  private Duration expirationTime;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public Claims getAllClaimsFromToken(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String getUsernameFromToken(String token) {
    return getAllClaimsFromToken(token).getSubject();
  }

  public ZonedDateTime getExpirationDateFromToken(String token) {
    return getAllClaimsFromToken(token)
        .getExpiration()
        .toInstant()
        .atZone(ZoneId.systemDefault());
  }

  private Boolean isTokenExpired(String token) {
    final ZonedDateTime expiration = getExpirationDateFromToken(token);
    return expiration.isBefore(ZonedDateTime.now());
  }

  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getAuthorities());
    return doGenerateToken(claims, user.getUsername());
  }

  public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", authorities);
    return doGenerateToken(claims, username);
  }

  private String doGenerateToken(Map<String, Object> claims, String username) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(toDate(ZonedDateTime.now()))
        .setExpiration(toDate(ZonedDateTime.now().plus(expirationTime)))
        .signWith(key)
        .compact();
  }

  public Boolean validateToken(String token) {
    return !isTokenExpired(token);
  }

  private Date toDate(ZonedDateTime time) {
    return Date.from(time.toInstant());
  }


}
