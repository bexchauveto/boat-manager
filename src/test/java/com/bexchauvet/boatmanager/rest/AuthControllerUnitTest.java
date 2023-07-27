package com.bexchauvet.boatmanager.rest;

import com.bexchauvet.boatmanager.error.exception.BadLoginUnauthorizedException;
import com.bexchauvet.boatmanager.rest.dto.TokenDTO;
import com.bexchauvet.boatmanager.service.UserService;
import com.bexchauvet.boatmanager.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTest {

    AuthController authController;

    @Mock
    UserService userService;

    @BeforeEach
    void init() {
        authController = new AuthController(userService);
    }


    @Test
    void login_BadCredentials() {
        when(userService.generateToken(Mockito.any(UserDTO.class)))
                .thenThrow(new UsernameNotFoundException("username"));
        assertThrows(UsernameNotFoundException.class,
                () -> authController.login(new UserDTO("badUsername", "password")));
        verify(userService).generateToken(Mockito.any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void login_BadPasswords() {
        when(userService.generateToken(Mockito.any(UserDTO.class)))
                .thenThrow(new BadLoginUnauthorizedException());
        assertThrows(BadLoginUnauthorizedException.class,
                () -> authController.login(new UserDTO("Username", "badPassword")));
        verify(userService).generateToken(Mockito.any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void login_GoodCredentials() {
        when(userService.generateToken(Mockito.any(UserDTO.class)))
                .thenReturn(new TokenDTO("TOKEN"));
        assertEquals(new ResponseEntity<>(new TokenDTO("TOKEN"), HttpStatusCode.valueOf(200)),
                authController.login(new UserDTO("Username", "badPassword")));
        verify(userService).generateToken(Mockito.any(UserDTO.class));
        verifyNoMoreInteractions(userService);
    }
}
