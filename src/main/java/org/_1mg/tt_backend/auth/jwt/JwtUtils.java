package org._1mg.tt_backend.auth.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Slf4j
@Component
public class JwtUtils {

    private final SecretKey secretKey;


    public JwtUtils(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getSubject(Claims claims) {
        return claims.get("sub", String.class);
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public String getCategory(Claims claims) {
        return claims.get("category", String.class);
    }

    //verifyToken()을 통해 만료 여부가 검증됨
//    public Boolean isExpired(Claims claims) throws ExpiredJwtException {
//        return claims.getExpiration().before(new Date());
//    }

    public String createJwt(String category, String memberId, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("sub", memberId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims verifyToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
