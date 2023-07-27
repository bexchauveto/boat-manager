package com.bexchauvet.boatmanager.error.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    private String message;
    private HttpStatus status;
    private List<String> errors;

}
