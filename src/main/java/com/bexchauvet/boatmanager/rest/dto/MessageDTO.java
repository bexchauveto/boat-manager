package com.bexchauvet.boatmanager.rest.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MessageDTO {
    private String message;
    private HttpStatus status;
    private Object data;
}
