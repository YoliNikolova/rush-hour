package com.prime.rushhour.controllers;

import com.prime.rushhour.models.AuthenticationRequest;
import com.prime.rushhour.models.AuthenticationResponse;
import com.prime.rushhour.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtilToken;

    @RequestMapping(value ="/authenticate",method = RequestMethod.POST)
    public ResponseEntity<?> createToken(@RequestBody AuthenticationRequest authenticationRequest) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        final String jwt = jwtUtilToken.createToken(authentication);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
