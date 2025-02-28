package edu.ucsb.cs156.authspike.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    @Value("${app.private.key}")
    private String privateKey;

    @Value("${app.client.id}")
    private String clientId;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public JwtService(RestTemplateBuilder restTemplateBuilder,  ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    private RSAPrivateKey getPrivateKey()  {
        try {
            String key = privateKey;
            key = key.replace("-----BEGIN PRIVATE KEY-----", "");
            key = key.replace("-----END PRIVATE KEY-----", "");
            key = key.replaceAll(System.lineSeparator(), "");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public String getJwt(){
        String token = Jwts.builder()
                .issuedAt(Date.from(Instant.now().minus(30, ChronoUnit.SECONDS)))
                .expiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .issuer(clientId)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
        return token;
    }

    public String getInstallationToken(String installationId) throws JsonProcessingException {
        String token = getJwt();
        String ENDPOINT = "https://api.github.com/app/installations/"+installationId+"/access_tokens";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Accept", "application/vnd.github+json");
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST,  entity, String.class);
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        String installationToken = responseJson.get("token").asText();
        return installationToken;
    }
}
