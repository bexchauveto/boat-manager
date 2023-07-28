package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.rest.dto.TokenDTO;
import com.bexchauvet.boatmanager.rest.dto.UserDTO;

public interface UserService {
    TokenDTO generateToken(UserDTO user);

}
