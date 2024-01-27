package com.nextgendynamics.crm.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register( @RequestBody RegisterRequest request ) {
        try {
            //returns AuthenticationResponse
            return ResponseEntity.ok(service.register(request));
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }

    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate( @RequestBody AuthenticationRequest request ) {
        try {
            // returns AuthenticationResponse
            return ResponseEntity.ok(service.authenticate(request));
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?>  refreshToken( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        return ResponseEntity.ok(service.refreshToken(request, response));
    }


}