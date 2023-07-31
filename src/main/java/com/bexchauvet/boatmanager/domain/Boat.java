package com.bexchauvet.boatmanager.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "boats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    private String description;
    @Column(name = "has_image")
    @JsonProperty("has_image")
    private Boolean hasImage;
    @Column(name = "imo_code", unique = true)
    @JsonProperty("imo_code")
    private Integer imoCode;
    @Column(name = "declared_name")
    @JsonProperty("declared_name")
    private String declaredName;
    @Column(name = "ship_type")
    @JsonProperty("ship_type")
    private String shipType;
    private String flag;
    @Column(name = "gross_tonnage")
    @JsonProperty("gross_tonnage")
    private Integer grossTonnage;
    @Column(name = "summer_dead_weight")
    @JsonProperty("summer_dead_weight")
    private Integer summerDeadWeight;
    @Column(name = "length_overall")
    @JsonProperty("length_overall")
    private Integer lengthOverall;
    private Integer beam;
    @Column(name = "year_of_built")
    @JsonProperty("year_of_built")
    private Integer yearOfBuilt;
    @Column(name = "position_date")
    @JsonProperty("position_date")
    private Instant positionDate;
    private Double latitude;
    private Double longitude;
}
