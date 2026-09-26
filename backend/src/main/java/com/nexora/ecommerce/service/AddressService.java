package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.AddressRequest;
import com.nexora.ecommerce.dto.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> getAddresses();

    AddressResponse create(AddressRequest request);

    AddressResponse update(Long id, AddressRequest request);

    void delete(Long id);

    AddressResponse setDefault(Long id);
}
