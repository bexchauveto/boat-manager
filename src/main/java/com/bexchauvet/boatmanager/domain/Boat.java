package com.bexchauvet.boatmanager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "boats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
    private Boolean hasImage;
}
