package org.example.sharing.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    private Long id;

    private String region;

    private String city;

    private String street;

    private String house;

    private Integer numFlat;

    private Double lng;

    private Double lat;

}