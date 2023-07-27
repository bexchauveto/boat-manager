package com.bexchauvet.boatmanager.error.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ErrorDTO {
    private String message;
    private HttpStatus status;
    private List<String> errors;

}
