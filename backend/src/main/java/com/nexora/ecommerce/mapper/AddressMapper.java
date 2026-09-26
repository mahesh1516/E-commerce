package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.AddressRequest;
import com.nexora.ecommerce.dto.AddressResponse;
import com.nexora.ecommerce.entity.Address;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static AddressResponse toResponse(Address a) {
        return new AddressResponse(a.getId(), a.getName(), a.getPhone(), a.getAddressLine1(),
                a.getAddressLine2(), a.getCity(), a.getState(), a.getPostalCode(),
                a.getCountry(), a.isDefaultAddress());
    }

    /** Copies request fields onto an entity (used for create and update). */
    public static void apply(AddressRequest r, Address a) {
        a.setName(r.name().trim());
        a.setPhone(r.phone().trim());
        a.setAddressLine1(r.addressLine1().trim());
        a.setAddressLine2(r.addressLine2() == null ? null : r.addressLine2().trim());
        a.setCity(r.city().trim());
        a.setState(r.state().trim());
        a.setPostalCode(r.postalCode().trim());
        a.setCountry(r.country().trim());
    }
}
