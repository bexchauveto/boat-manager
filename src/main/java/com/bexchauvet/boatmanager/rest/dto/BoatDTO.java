package com.bexchauvet.boatmanager.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BoatDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
    @Min(1000000)
    @Max(9999999)
    private Integer imoCode;
}
