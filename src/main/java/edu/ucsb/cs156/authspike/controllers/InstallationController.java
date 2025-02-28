package edu.ucsb.cs156.authspike.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.ucsb.cs156.authspike.entities.Installation;
import edu.ucsb.cs156.authspike.services.JwtService;
import io.jsonwebtoken.Jwts;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@RestController
@RequestMapping("/api/installations")
public class InstallationController {
    private final JwtService jwtService;

    private final RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public InstallationController(JwtService jwtService, RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    @GetMapping("installation")
    public Object addInstallation(@RequestParam String installation_id,  @RequestParam String setup_action) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Installation installation = new Installation();
        String token = jwtService.getJwt();
        String ENDPOINT = "https://api.github.com/app/installations/"+installation_id;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Accept", "application/vnd.github+json");
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(ENDPOINT, HttpMethod.GET,  entity, String.class);
        return response.getBody();
    }

    @GetMapping("privateKey")
    public String getToken() throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {

        String token = jwtService.getInstallationToken("61554210");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + token);
        requestHeaders.add("Accept", "application/vnd.github+json");
        requestHeaders.add("X-GitHub-Api-Version", "2022-11-28");
        HttpEntity<String> newEntity = new HttpEntity<>(requestHeaders);

        String NEWENDPOINT = "https://api.github.com/users/Division7/repos";
        ResponseEntity<String> newResponse = restTemplate.exchange(NEWENDPOINT, HttpMethod.GET,  newEntity, String.class);

        return newResponse.getBody();
    }

    @GetMapping("testStudentRepos")
    public String testStudentRepos() throws JsonProcessingException {

        String ENDPOINT = "https://api.github.com/repos/ucsb-cs156-s25/STARTER-team01/forks";
        String token = jwtService.getInstallationToken("61829186");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + token);
        requestHeaders.add("Accept", "application/vnd.github+json");
        requestHeaders.add("X-GitHub-Api-Version", "2022-11-28");
        String body = """
                {
                \"organization\":\"ucsb-cs156-s25\",
                \"name\":\"student-repo-1\",
                \"default_branch_only\": true
                }
                """;
        HttpEntity<String> entity = new HttpEntity<>(body, requestHeaders);
        ResponseEntity<String> newResponse = restTemplate.exchange(ENDPOINT, HttpMethod.POST,  entity, String.class);
        return newResponse.getBody();
    }


}
