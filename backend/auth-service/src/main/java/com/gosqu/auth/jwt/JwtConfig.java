package com.gosqu.auth.jwt;

import com.gosqu.auth.config.AuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@Slf4j
public class JwtConfig {

    private final ResourceLoader resourceLoader;

    public JwtConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Value("${AUTH_PRIVATE:classpath:keys/private_key.pem}")
    private String privateKeyPath;

    @Value("${AUTH_PUBLIC:classpath:keys/public_key.pem}")
    private String publicKeyPath;

    @Bean
    public PrivateKey privateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        try {
            String keyContent = readKeyFromPath(privateKeyPath);
            String cleanKey = cleanKey(keyContent, "PRIVATE");
            byte[] decoded = Base64.getDecoder().decode(cleanKey);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey key = keyFactory.generatePrivate(keySpec);
            log.info("Private key successfully loaded from: {}", describeKeySource(privateKeyPath));
            return key;
        } catch (Exception e) {
            log.error("Failed to load private key from {}: {}", describeKeySource(privateKeyPath), e.getMessage());
            throw e;
        }
    }

    @Bean
    public PublicKey publicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        try {
            String keyContent = readKeyFromPath(publicKeyPath);
            String cleanKey = cleanKey(keyContent, "PUBLIC");
            byte[] decoded = Base64.getDecoder().decode(cleanKey);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PublicKey key = keyFactory.generatePublic(keySpec);
            log.info("Public key successfully loaded from: {}", describeKeySource(publicKeyPath));
            return key;
        } catch (Exception e) {
            log.error("Failed to load public key from {}: {}", describeKeySource(publicKeyPath), e.getMessage());
            throw e;
        }
    }

    private String readKeyFromPath(String path) throws IOException {
        if (path.startsWith("classpath:") || path.startsWith("file:")) {
            Resource resource = resourceLoader.getResource(path);
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        }
        return path;
    }

    private String describeKeySource(String path) {
        return path.startsWith("-----BEGIN") ? "[environment variable]" : path;
    }

    private String cleanKey(String key, String keyType) {
        return key
                .replace("-----BEGIN " + keyType + " KEY-----", "")
                .replace("-----END " + keyType + " KEY-----", "")
                .replace("-----BEGIN RSA " + keyType + " KEY-----", "")
                .replace("-----END RSA " + keyType + " KEY-----", "")
                .replaceAll("\\s+", "")
                .trim();
    }
}
