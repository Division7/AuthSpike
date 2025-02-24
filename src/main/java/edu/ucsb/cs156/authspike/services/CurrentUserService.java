package edu.ucsb.cs156.authspike.services;

import edu.ucsb.cs156.authspike.entities.User;
import edu.ucsb.cs156.authspike.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    UserRepository userRepository;

    public CurrentUserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication auth = securityContext.getAuthentication();
        if (auth != null){
            OidcUser oauthUser =  (OidcUser) auth.getPrincipal();
            return userRepository.findBySub(oauthUser.getSubject()).orElse(null);
        }else{
            return null;
        }
    }
}
