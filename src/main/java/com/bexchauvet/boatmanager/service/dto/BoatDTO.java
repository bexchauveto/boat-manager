package com.bexchauvet.boatmanager.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoatDTO {


    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
}
