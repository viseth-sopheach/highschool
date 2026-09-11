package com.seth.backend.security;

import com.seth.backend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

   private final JwtProperties jwtProperties;

   private SecretKey signingKey() {
      byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
      return Keys.hmacShaKeyFor(keyBytes);
   }

   public String generateAccessToken(UserPrincipal principal) {
      List<String> authorities = principal.getAuthorities().stream()
              .map(Object::toString)
              .collect(Collectors.toList());

      Date now = new Date();
      Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMs());

      return Jwts.builder()
              .subject(principal.getUsername())
              .claim("uid", principal.getId())
              .claim("authorities", authorities)
              .issuedAt(now)
              .expiration(expiry)
              .signWith(signingKey())
              .compact();
   }

   public String extractUsername(String token) {
      return parseClaims(token).getSubject();
   }

   public Long extractUserId(String token) {
      return parseClaims(token).get("uid", Long.class);
   }

   @SuppressWarnings("unchecked")
   public List<String> extractAuthorities(String token) {
      return (List<String>) parseClaims(token).get("authorities");
   }

   public boolean isTokenValid(String token) {
      try {
         Claims claims = parseClaims(token);
         return claims.getExpiration().after(new Date());
      } catch (Exception e) {
         return false;
      }
   }

   private Claims parseClaims(String token) {
      return Jwts.parser()
              .verifyWith(signingKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();
   }
}