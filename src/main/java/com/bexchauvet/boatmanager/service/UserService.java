package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.service.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {


    String generateToken(UserDTO user);

}
