package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.AddressRequest;
import com.nexora.ecommerce.dto.AddressResponse;
import com.nexora.ecommerce.dto.ApiResponse;
import com.nexora.ecommerce.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "List my addresses (default first)")
    public List<AddressResponse> getAll() {
        return addressService.getAddresses();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add an address")
    public AddressResponse create(@Valid @RequestBody AddressRequest request) {
        return addressService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit an address")
    public AddressResponse update(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return addressService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ApiResponse delete(@PathVariable Long id) {
        addressService.delete(id);
        return ApiResponse.ok("Address deleted");
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Set as default address")
    public AddressResponse setDefault(@PathVariable Long id) {
        return addressService.setDefault(id);
    }
}
