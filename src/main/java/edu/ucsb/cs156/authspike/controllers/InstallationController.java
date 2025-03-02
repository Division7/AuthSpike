package edu.ucsb.cs156.authspike.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.ucsb.cs156.authspike.entities.Installation;
import edu.ucsb.cs156.authspike.services.JwtService;
import io.jsonwebtoken.Jwts;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
    public String testStudentRepos() throws JsonProcessingException, GitAPIException, URISyntaxException {
        String ENDPOINT = "https://api.github.com/orgs/ucsb-cs156-s25/repos";
        String token = jwtService.getInstallationToken("61829186");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + token);
        requestHeaders.add("Accept", "application/vnd.github+json");
        requestHeaders.add("X-GitHub-Api-Version", "2022-11-28");
        Map<String, Object> body = new HashMap<>();
        body.put("name", "student-repo-4");
        String bodyString = objectMapper.writeValueAsString(body);
        HttpEntity<String> entity = new HttpEntity<>(bodyString, requestHeaders);
        ResponseEntity<String> newResponse = restTemplate.exchange(ENDPOINT, HttpMethod.POST,  entity, String.class);
        Git git = Git.cloneRepository()
                .setURI("https://git:"+token+"@github.com/ucsb-cs156-s25/STARTER-team01.git")
                .setDirectory(new File("temp/repo4"))
                .call();
        RemoteAddCommand addCommand = git.remoteAdd();
        addCommand.setName("student4");
        addCommand.setUri(new URIish("https://git:"+token+"@github.com/ucsb-cs156-s25/student-repo-4.git"));
        addCommand.call();

        PushCommand push = git.push();
        push.setRemote("student4");
        push.call();
        HttpHeaders secondRequestHeaders = new HttpHeaders();
        secondRequestHeaders.add("Authorization", "Bearer " + token);
        secondRequestHeaders.add("Accept", "application/vnd.github+json");
        secondRequestHeaders.add("X-GitHub-Api-Version", "2022-11-28");
        String SECONDENDPOINT = "https://api.github.com/rate_limit";
        HttpEntity<String> newEntity = new HttpEntity<>(secondRequestHeaders);
        ResponseEntity<String> secondResponse = restTemplate.exchange(SECONDENDPOINT, HttpMethod.GET,  newEntity, String.class);
        boolean result = FileSystemUtils.deleteRecursively(new File("temp/repo4"));
        return secondResponse.getBody();
    }

    @GetMapping("provideToken")
    public String provideToken() throws JsonProcessingException {
        return jwtService.getInstallationToken("61829186");
    }

    @GetMapping("testPushRepo")
    public String testPushRepo() throws GitAPIException, JsonProcessingException, URISyntaxException {
        String token = jwtService.getInstallationToken("61829186");
        Git git = Git.cloneRepository()
                .setURI("https://git:"+token+"@github.com/ucsb-cs156-s25/STARTER-team01.git")
                .setDirectory(new File("temp/repo1"))
                .call();
        RemoteAddCommand addCommand = git.remoteAdd();
        addCommand.setName("student1");
        addCommand.setUri(new URIish("https://git:"+token+"@github.com/ucsb-cs156-s25/student-repo-2.git"));
        addCommand.call();

        PushCommand push = git.push();
        push.setRemote("student1");
        push.call();
        HttpHeaders secondRequestHeaders = new HttpHeaders();
        secondRequestHeaders.add("Authorization", "Bearer " + token);
        secondRequestHeaders.add("Accept", "application/vnd.github+json");
        secondRequestHeaders.add("X-GitHub-Api-Version", "2022-11-28");
        String SECONDENDPOINT = "https://api.github.com/rate_limit";
        HttpEntity<String> newEntity = new HttpEntity<>(secondRequestHeaders);
        ResponseEntity<String> secondResponse = restTemplate.exchange(SECONDENDPOINT, HttpMethod.POST,  newEntity, String.class);
        return secondResponse.getBody();
    }

    @GetMapping("testRateLimits")
    public String testRateLimits() throws JsonProcessingException {
        String token = jwtService.getInstallationToken("61829186");
        HttpHeaders secondRequestHeaders = new HttpHeaders();
        secondRequestHeaders.add("Authorization", "Bearer " + token);
        secondRequestHeaders.add("Accept", "application/vnd.github+json");
        secondRequestHeaders.add("X-GitHub-Api-Version", "2022-11-28");
        String SECONDENDPOINT = "https://api.github.com/rate_limit";
        HttpEntity<String> newEntity = new HttpEntity<>(secondRequestHeaders);
        ResponseEntity<String> secondResponse = restTemplate.exchange(SECONDENDPOINT, HttpMethod.GET,  newEntity, String.class);
        return secondResponse.getBody();
    }


}
