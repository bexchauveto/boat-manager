package com.bexchauvet.boatmanager.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String message;
    private HttpStatus status;
    private Object data;
}
