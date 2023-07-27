package com.bexchauvet.boatmanager.service.Impl;

import com.bexchauvet.boatmanager.error.exception.BadLoginUnauthorizedException;
import com.bexchauvet.boatmanager.rest.dto.TokenDTO;
import com.bexchauvet.boatmanager.service.UserService;
import com.bexchauvet.boatmanager.service.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {


    private final JwtEncoder encoder;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenDTO generateToken(UserDTO user) throws UsernameNotFoundException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        if (passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            Instant now = Instant.now();
            long expiry = 36000L;
            String scope = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(userDetails.getUsername())
                    .claim("scope", scope)
                    .build();
            return new TokenDTO(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
        } else {
            throw new BadLoginUnauthorizedException();
        }
    }
}
