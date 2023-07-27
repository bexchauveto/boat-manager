package com.bexchauvet.boatmanager.rest;

import com.bexchauvet.boatmanager.service.UserService;
import com.bexchauvet.boatmanager.service.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;


@RequestMapping("/auth")
@RestController
@Tag(name = "Authentication", description = "The Authentication API.")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "User Authentication",
            description = "Authenticate the user and return a JWT token if the user is valid.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
    @Content(mediaType = "application/json", examples =
    @ExampleObject(value = "{\n" + "  \"username\": \"jane\",\n"
            + "  \"password\": \"password\"\n" + "}", summary = "User Authentication Example")))
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody UserDTO user) {
        return new ResponseEntity<>(this.userService.generateToken(user), HttpStatus.valueOf(200));
    }


}
