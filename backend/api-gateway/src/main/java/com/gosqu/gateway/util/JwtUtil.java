package com.gosqu.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtUtil {

    private final PublicKey publicKey;

    // auth-service podpisuje tokeny RS256 kluczem prywatnym (JwtService.java) — gateway musi
    // weryfikować tym samym kluczem PUBLICZNYM, nie osobnym sekretem HMAC (poprzedni kod tu
    // używał Keys.hmacShaKeyFor, co nigdy nie mogło zweryfikować prawdziwego tokenu z auth-service
    // — dwa różne, niekompatybilne algorytmy podpisu). AUTH_PUBLIC to ta sama zmienna, którą już
    // czyta auth-service (JwtConfig.java) — musi wskazywać na ten sam klucz w obu serwisach.
    public JwtUtil(ResourceLoader resourceLoader,
                    @Value("${AUTH_PUBLIC:classpath:keys/public_key.pem}") String publicKeyPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keyContent = readKeyFromPath(resourceLoader, publicKeyPath);
        String cleanKey = cleanKey(keyContent);
        byte[] decoded = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        this.publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private static String readKeyFromPath(ResourceLoader resourceLoader, String path) throws IOException {
        if (path.startsWith("classpath:") || path.startsWith("file:")) {
            Resource resource = resourceLoader.getResource(path);
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        }
        return path;
    }

    private static String cleanKey(String key) {
        return key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "")
                .trim();
    }
}
