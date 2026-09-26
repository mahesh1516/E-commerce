package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.AddressRequest;
import com.nexora.ecommerce.dto.AddressResponse;
import com.nexora.ecommerce.entity.Address;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.AddressMapper;
import com.nexora.ecommerce.repository.AddressRepository;
import com.nexora.ecommerce.service.AddressService;
import com.nexora.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses() {
        Long userId = securityUtils.getCurrentUser().getId();
        return addressRepository.findByUserIdOrderByDefaultAddressDescIdAsc(userId).stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse create(AddressRequest request) {
        User user = securityUtils.getCurrentUser();

        Address address = new Address();
        address.setUser(user);
        AddressMapper.apply(request, address);
        addressRepository.save(address);

        // First address is always the default
        boolean first = addressRepository.countByUserId(user.getId()) == 1;
        if (first || Boolean.TRUE.equals(request.defaultAddress())) {
            makeDefault(user.getId(), address.getId());
        }
        return AddressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse update(Long id, AddressRequest request) {
        Address address = findOwn(id);
        AddressMapper.apply(request, address);
        if (Boolean.TRUE.equals(request.defaultAddress())) {
            makeDefault(address.getUser().getId(), id);
        }
        return AddressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Address address = findOwn(id);
        Long userId = address.getUser().getId();
        boolean wasDefault = address.isDefaultAddress();

        addressRepository.delete(address);
        addressRepository.flush();

        // Promote another address to default if needed
        if (wasDefault) {
            addressRepository.findByUserIdOrderByDefaultAddressDescIdAsc(userId).stream()
                    .findFirst()
                    .ifPresent(a -> a.setDefaultAddress(true));
        }
    }

    @Override
    @Transactional
    public AddressResponse setDefault(Long id) {
        Address address = findOwn(id);
        makeDefault(address.getUser().getId(), id);
        return AddressMapper.toResponse(address);
    }

    /** Exactly one default address per user. */
    private void makeDefault(Long userId, Long addressId) {
        addressRepository.findByUserIdOrderByDefaultAddressDescIdAsc(userId)
                .forEach(a -> a.setDefaultAddress(a.getId().equals(addressId)));
    }

    private Address findOwn(Long id) {
        Long userId = securityUtils.getCurrentUser().getId();
        return addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", id));
    }
}
