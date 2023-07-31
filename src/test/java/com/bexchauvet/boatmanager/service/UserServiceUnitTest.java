package com.bexchauvet.boatmanager.service;


import com.bexchauvet.boatmanager.error.exception.BadLoginUnauthorizedException;
import com.bexchauvet.boatmanager.rest.dto.TokenDTO;
import com.bexchauvet.boatmanager.rest.dto.UserDTO;
import com.bexchauvet.boatmanager.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {


    UserService userService;
    @Mock
    JwtEncoder encoder;
    @Mock
    UserDetailsService userDetailsService;
    @Mock
    PasswordEncoder passwordEncoder;


    @BeforeEach
    void init() {
        userService = new UserServiceImpl(encoder, userDetailsService, passwordEncoder);
    }

    @Test
    @DisplayName("Test the generate token function with bad login")
    void generateToken_whenBadLoginPassword() {
        when(userDetailsService.loadUserByUsername(Mockito.anyString()))
                .thenThrow(new UsernameNotFoundException("UserName"));
        assertThrows(UsernameNotFoundException.class,
                () -> userService.generateToken(new UserDTO("Username", "password")));
        verify(userDetailsService).loadUserByUsername(Mockito.anyString());
        verifyNoMoreInteractions(userDetailsService);
        verifyNoInteractions(encoder, passwordEncoder);
    }

    @Test
    @DisplayName("Test the generate token function with bad password")
    void generateToken_whenGoodLogin_BadPassword() {
        when(userDetailsService.loadUserByUsername(Mockito.anyString()))
                .thenReturn(User.builder().username("Username").password("password").roles("USER").build());
        when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(false);
        assertThrows(BadLoginUnauthorizedException.class,
                () -> userService.generateToken(new UserDTO("Username", "badPassword")));
        verify(userDetailsService).loadUserByUsername(Mockito.anyString());
        verify(passwordEncoder).matches(Mockito.anyString(), Mockito.anyString());
        verifyNoMoreInteractions(userDetailsService, passwordEncoder);
        verifyNoInteractions(encoder);
    }

    @Test
    @DisplayName("Test the generate token function with good login/password")
    void generateToken_whenGoodLogin_GoodPassword() {
        when(userDetailsService.loadUserByUsername(Mockito.anyString()))
                .thenReturn(User.builder().username("Username").password("password").roles("USER").build());
        when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);
        when(encoder.encode(Mockito.any(JwtEncoderParameters.class)))
                .thenReturn(new Jwt("TOKEN", Instant.now(), Instant.now().plusSeconds(36000L),
                        Collections.singletonMap("Headers", "data"), Collections.singletonMap("claims", "data")));
        assertEquals(new TokenDTO("TOKEN"),
                userService.generateToken(new UserDTO("Username", "badPassword")));
        verify(userDetailsService).loadUserByUsername(Mockito.anyString());
        verify(passwordEncoder).matches(Mockito.anyString(), Mockito.anyString());
        verify(encoder).encode(Mockito.any(JwtEncoderParameters.class));
        verifyNoMoreInteractions(userDetailsService, passwordEncoder, encoder);
    }


}
