package ru.kata.spring.boot_security.demo.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTServiceImpl implements JWTService {
    @Value("${jwt_secret}")
    private String secret;
    public String generateToken(String username) {
        Date expirationData = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
        return JWT.create()
                .withSubject("User detail")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("iman")
                .withExpiresAt(expirationData)
                .sign(Algorithm.HMAC256(this.secret));
    }
    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(this.secret))
                .withSubject("User detail")
                .withIssuer("iman")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }
}
