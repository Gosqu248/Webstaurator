package com.gosqu.user.address;

import com.gosqu.user.address.dto.request.AddressRequest;
import com.gosqu.user.address.dto.response.AddressResponse;
import com.gosqu.user.address.exception.AddressLimitExceededException;
import com.gosqu.user.address.exception.AddressNotFoundException;
import com.gosqu.user.address.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private static final int MAX_ADDRESSES = 10;

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "addresses", key = "#userId")
    public List<AddressResponse> getAddresses(UUID userId) {
        return addressRepository.findAllByUserId(userId).stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse getDefaultAddress(UUID userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(addressMapper::toResponse)
                .orElseThrow(() -> new AddressNotFoundException(userId, true));
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId")
    public AddressResponse addAddress(UUID userId, AddressRequest request) {
        long count = addressRepository.countByUserId(userId);
        if (count >= MAX_ADDRESSES) {
            log.warn("action=add_address_blocked reason=limit_exceeded userId={} limit={}", userId, MAX_ADDRESSES);
            throw new AddressLimitExceededException(MAX_ADDRESSES);
        }

        Address address = Address.builder()
                .userId(userId)
                .label(request.label())
                .street(request.street())
                .city(request.city())
                .postalCode(request.postalCode())
                .country(request.country() != null ? request.country() : "Poland")
                .latitude(request.latitude())
                .longitude(request.longitude())
                .isDefault(false)
                .build();

        AddressResponse response = addressMapper.toResponse(addressRepository.save(address));
        log.info("action=add_address userId={} addressId={}", userId, response.id());
        return response;
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId")
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        address.setLabel(request.label());
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setPostalCode(request.postalCode());
        if (request.country() != null) address.setCountry(request.country());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());

        AddressResponse response = addressMapper.toResponse(addressRepository.save(address));
        log.info("action=update_address userId={} addressId={}", userId, addressId);
        return response;
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId")
    public void deleteAddress(UUID userId, UUID addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        addressRepository.delete(address);
        log.info("action=delete_address userId={} addressId={}", userId, addressId);
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId")
    public AddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        addressRepository.clearDefaultForUser(userId);
        address.setIsDefault(true);
        AddressResponse response = addressMapper.toResponse(addressRepository.save(address));
        log.info("action=set_default_address userId={} addressId={}", userId, addressId);
        return response;
    }
}
