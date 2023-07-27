package com.bexchauvet.boatmanager.rest;

import com.bexchauvet.boatmanager.error.dto.ErrorDTO;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.rest.dto.TokenDTO;
import com.bexchauvet.boatmanager.service.UserService;
import com.bexchauvet.boatmanager.service.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The JWT token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO user) {
        return new ResponseEntity<>(this.userService.generateToken(user), HttpStatus.valueOf(200));
    }


}
