package org.example.sharing.rest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatFilterRequestDTO {
    private String city;
    private Long bookingStart;
    private Long bookingEnd;
    private Integer numberOfRooms;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer numberOfSingleBeds;
    private Integer numberOfDoubleBeds;
    private String sortField;
}
