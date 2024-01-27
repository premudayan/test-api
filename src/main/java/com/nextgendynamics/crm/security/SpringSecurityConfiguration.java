package com.nextgendynamics.crm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
public class SpringSecurityConfiguration {


/*    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //All Auth should be authenticated
        http.authorizeHttpRequests(
                auth -> auth.anyRequest().authenticated()
        );
        http.httpBasic(Customizer.withDefaults());

        //CSRF -> POST , PUT
        http.csrf(AbstractHttpConfigurer::disable);


        return http.build();

    }*/
}
