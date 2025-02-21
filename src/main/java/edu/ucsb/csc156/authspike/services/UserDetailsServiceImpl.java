package edu.ucsb.csc156.authspike.services;

import edu.ucsb.csc156.authspike.entities.User;
import edu.ucsb.csc156.authspike.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailsServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Optional<User> currentUser = userRepository.findBySub((String) oAuth2User.getAttributes().get("sub"));
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
        }else{
            User newUser = User.builder().sub((String) oAuth2User.getAttributes().get("sub")).build();
            userRepository.save(newUser);
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        System.out.println("This bitch actually ran");
        authorities.addAll(oAuth2User.getAuthorities());
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(),  "sub");
    }

}
