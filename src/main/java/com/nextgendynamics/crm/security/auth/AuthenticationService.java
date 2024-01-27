package com.nextgendynamics.crm.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextgendynamics.crm.security.config.JwtService;
import com.nextgendynamics.crm.exception.CustomException;
import com.nextgendynamics.crm.security.token.Token;
import com.nextgendynamics.crm.security.token.TokenRepository;
import com.nextgendynamics.crm.security.token.TokenType;
import com.nextgendynamics.crm.security.user.User;
import com.nextgendynamics.crm.security.user.UserRepository;
import com.nextgendynamics.crm.validator.ObjectValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames={"authCache"})
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ObjectValidator<RegisterRequest> userValidator;
    private final ObjectValidator<AuthenticationRequest> authValidator;
    public AuthenticationResponse register(RegisterRequest request) {
//        userValidator.validate(request);

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        System.out.println("Email:"+ request.getEmail());
        var findUser = repository.findByEmail(request.getEmail());
        if ( !findUser.isEmpty()){
            throw new CustomException("User with the email "+ request.getEmail() + " already exist!");
        }
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

//    @Cacheable( key="#lookupTypeName +':'+ #lookupCodeName" )
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authValidator.validate(request);
        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken( request.getEmail(), request.getPassword() ) );
        var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public AuthenticationResponse refreshToken( HttpServletRequest request, HttpServletResponse response ) throws IOException {

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;

    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return null;
    }

    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);

    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail).orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return authResponse;
      }
    }
    return null;
  }
}
