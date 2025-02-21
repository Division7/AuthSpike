package edu.ucsb.csc156.authspike.config;

import edu.ucsb.csc156.authspike.entities.User;
import edu.ucsb.csc156.authspike.repositories.UserRepository;
import edu.ucsb.csc156.authspike.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(@Autowired UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests.requestMatchers("/","/index.html").permitAll()
                                .anyRequest().authenticated()
                ).oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.oidcUserService(userDetailsService)));
        return http.build();
    }

    @Bean
    static RoleHierarchy  roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("MODERATOR")
                .role("MODERATOR").implies("USER")
                .build();
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/h2-console/**");
    }

    /*private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
		final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return (userRequest) -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
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
            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(),  "sub");
        };
    }*/
}
