package org.example.sharing.rest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @NotEmpty(message = "Ошибка! Вы не ввели регион")
    private String region;

    @NotEmpty(message = "Ошибка! Вы не ввели населенный пункт")
    private String city;

    @NotEmpty(message = "Ошибка! Вы не ввели улицу")
    private String street;

    @NotEmpty(message = "Ошибка! Вы не ввели номер дома")
    private String house;

    @NotNull(message = "Ошибка! Вы не ввели номер квартиры")
    private Integer numFlat;

    @NotNull(message = "Ошибка! Вы не ввели ширину")
    private Double lng;

    @NotNull(message = "Ошибка! Вы не ввели долготу")
    private Double lat;

}
