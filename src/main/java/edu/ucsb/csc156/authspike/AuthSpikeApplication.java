package edu.ucsb.csc156.authspike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;


@SpringBootApplication
@RestController
public class AuthSpikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthSpikeApplication.class, args);
    }

    @GetMapping("/user")
    public Collection<? extends GrantedAuthority> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAuthorities();
    }

    @GetMapping("/attributes")
    public Map<String, Object> attributes(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

}
