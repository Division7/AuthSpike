package edu.ucsb.cs156.authspike.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.cs156.authspike.entities.Installation;
import edu.ucsb.cs156.authspike.services.PrivateKeyService;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;


@RestController
@RequestMapping("/api/installations")
public class InstallationController {
    private final PrivateKeyService privateKeyService;

    private final RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public InstallationController(PrivateKeyService privateKeyService, RestTemplateBuilder restTemplateBuilder,  ObjectMapper objectMapper) {
        this.privateKeyService = privateKeyService;
        restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    @GetMapping("installation")
    public Object addInstallation(@RequestParam String installation_id,  @RequestParam String setup_action) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Installation installation = new Installation();
        String token = Jwts.builder()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .issuer(privateKeyService.getClientId())
                .signWith(privateKeyService.getPrivateKey())
                .compact();
        return token;
    }

    @GetMapping("privateKey")
    public String getToken() throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        String token = Jwts.builder()
                .issuedAt(Date.from(Instant.now().minus(30, ChronoUnit.SECONDS)))
                .expiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .issuer(privateKeyService.getClientId())
                .signWith(privateKeyService.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
        String ENDPOINT = "https://api.github.com/app/installations/61554210/access_tokens";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Accept", "application/vnd.github+json");
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST,  entity, String.class);

        System.out.println("response: " + response.getBody());
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        String installationToken = responseJson.get("token").asText();

        System.out.println("token: " + installationToken);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + installationToken);
        requestHeaders.add("Accept", "application/vnd.github+json");
        requestHeaders.add("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<String> newEntity = new HttpEntity<>(requestHeaders.toString());

        System.out.println("newEntity: " + newEntity.getBody());
        String NEWENDPOINT = "https://api.github.com/users/Division7/repos";
        ResponseEntity<String> newResponse = restTemplate.exchange(NEWENDPOINT, HttpMethod.GET,  newEntity, String.class);

        return newResponse.getBody();
    }


}
