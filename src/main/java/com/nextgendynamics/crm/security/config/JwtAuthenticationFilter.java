package com.nextgendynamics.crm.security.config;

import com.nextgendynamics.crm.security.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//@CacheConfig(cacheNames = {"userDetailsCache", "tokenCache"})
//@Lazy
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, TokenRepository tokenRepository) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.tokenRepository = tokenRepository;
    this.logger.info("JwtAuthenticationFilter bean initialized.");
  }
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    userEmail = jwtService.extractUsername(jwt);
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      var isTokenValid = tokenRepository.findByToken(jwt)
          .map(t -> !t.isExpired() && !t.isRevoked())
          .orElse(false);
      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }

/*
  @Cacheable(value = "userDetailsCache", key = "#userName")
  public UserDetails getUserDetails(String userName) {
    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
    return userDetails;
  }

  @Cacheable(value = "tokenCache", key = "#jwt")
  public boolean isTokenValid(String jwt) {
    return tokenRepository.findByToken(jwt)
            .map(t -> !t.isExpired() && !t.isRevoked())
            .orElse(false);
  }
*/

}
