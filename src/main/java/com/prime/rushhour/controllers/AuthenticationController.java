package com.prime.rushhour.controllers;

import com.prime.rushhour.models.AuthenticationRequest;
import com.prime.rushhour.models.AuthenticationResponse;
import com.prime.rushhour.models.RegisterRequest;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.security.JwtUtil;
import com.prime.rushhour.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtilToken;
    private UserService userService;

    @RequestMapping(value ="/authenticate",method = RequestMethod.POST)
    public ResponseEntity<?> createToken(@RequestBody AuthenticationRequest authenticationRequest) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        final String jwt = jwtUtilToken.createToken(authentication);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @RequestMapping(value="/register",method = RequestMethod.POST)
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid RegisterRequest request){
        UserResponseDTO user = userService.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJwtUtilToken(JwtUtil jwtUtilToken) {
        this.jwtUtilToken = jwtUtilToken;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
