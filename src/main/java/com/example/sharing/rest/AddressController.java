package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.AddressRequestDTO;
import org.example.sharing.rest.dto.response.AddressResponseDTO;
import org.example.sharing.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
@Tag(name = "AddressController", description = "Контроллер адресов")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Добавление адреса")
    public ResponseEntity<AddressResponseDTO> addAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO addressResponseDTO = addressService.addAddress(addressRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addressResponseDTO.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(addressResponseDTO);
    }
}