package com.arogut.homex.data.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtParserBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expirationTime;

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(Base64.getEncoder().encode(secret.getBytes()),
                        SignatureAlgorithm.HS512.getJcaName()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public SubjectAuthTypePair getSubjectAndType(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return new SubjectAuthTypePair(claims.getSubject(), AuthType.valueOf(claims.get("role", String.class)));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String deviceId, Map<String, Object> claims) {
        long expirationTimeLong = Long.parseLong(expirationTime); //in second

        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(deviceId)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(new SecretKeySpec(Base64.getEncoder().encode(secret.getBytes()),
                        SignatureAlgorithm.HS512.getJcaName()))
                .compact();
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}
