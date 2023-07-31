package com.bexchauvet.boatmanager.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZylaShipInformationResponseDTO {

    private Integer status;
    private Boolean success;
    private String message;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private ZylaShipInformationData[] data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZylaShipInformationData {
        @JsonProperty("imo_number")
        private String imoNumber;
        @JsonProperty("vessel_name")
        private String vesselName;
        @JsonProperty("ship_type")
        private String shipType;
        private String flag;
        @JsonProperty("gross_tonnage")
        private String grossTonnage;
        @JsonProperty("summer_deadweight_t")
        private String summerDeadWeight;
        @JsonProperty("length_overall_m")
        private String lengthOverall;
        @JsonProperty("beam_m")
        private String beam;
        @JsonProperty("year_of_built")
        private String yearOfBuilt;
        @JsonProperty("vessel_local_time")
        private String positionReceived;
        @JsonProperty("latitude_longitude")
        private String latitudeLongitude;
    }
}
