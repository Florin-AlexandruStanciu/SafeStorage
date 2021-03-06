package com.SafeStorage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;


@Service
public class JwtProvider {
    public static final long JWT_EXPIRATION_TIME = 300000L;
    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/keystore");
            keyStore.load(resourceAsStream, "password".toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Eraore la incarcarea KeyStore-ului");
        }
    }

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(JWT_EXPIRATION_TIME)))
                .compact();
    }

    public boolean validateToken(String jwt) {
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("alias", "password".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException("Eroarea la recuerarea cheii private");
        }
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("alias").getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException("Eroarea la recuerarea cheii publice");
        }
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
