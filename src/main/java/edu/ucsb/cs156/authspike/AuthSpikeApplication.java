package edu.ucsb.cs156.authspike;

import edu.ucsb.cs156.authspike.entities.User;
import edu.ucsb.cs156.authspike.services.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@SpringBootApplication
@RestController
@RequestMapping("/api/testing")
public class AuthSpikeApplication {

    private final CurrentUserService currentUserService;

    @Autowired
    public AuthSpikeApplication(@Autowired CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthSpikeApplication.class, args);
    }

    @GetMapping("/test/user")
    public Object user(@AuthenticationPrincipal OAuth2User principal) {
        return principal;
    }

    @GetMapping("/test/getLocalUser")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public User getUser() {
        return currentUserService.getCurrentUser();
    }

}
