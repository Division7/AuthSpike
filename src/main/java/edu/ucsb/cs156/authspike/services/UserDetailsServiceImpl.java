package edu.ucsb.cs156.authspike.services;

import edu.ucsb.cs156.authspike.entities.User;
import edu.ucsb.cs156.authspike.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailsServiceImpl extends OidcUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        OidcUser oidcUser = super.loadUser(userRequest);
        return managePrimarySignIn(oidcUser);
    }

    private OidcUser managePrimarySignIn(OidcUser oidcUser){
        Optional<User> currentUser = userRepository.findBySub(oidcUser.getSubject());
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            if(user.isAdmin()){
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            else if(user.isModerator()){
                authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
            }else{
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            if(!user.getName().equals(oidcUser.getFullName())){
                user.setName(oidcUser.getFullName());
                userRepository.save(user);
            }
        }else{
            User newUser = User.builder()
                    .sub(oidcUser.getSubject())
                    .name(oidcUser.getFullName())
                    .email(oidcUser.getEmail())
                    .build();
            userRepository.save(newUser);
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        authorities.addAll(oidcUser.getAuthorities());
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(),  oidcUser.getUserInfo());
    }

}
