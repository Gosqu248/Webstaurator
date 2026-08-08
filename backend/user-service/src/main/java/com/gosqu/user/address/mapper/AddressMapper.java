package com.gosqu.user.address.mapper;

import com.gosqu.user.address.Address;
import com.gosqu.user.address.dto.response.AddressResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(Address address);
}
