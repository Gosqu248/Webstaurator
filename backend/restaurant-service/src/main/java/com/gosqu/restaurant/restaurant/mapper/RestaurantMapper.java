package com.gosqu.restaurant.restaurant.mapper;

import com.gosqu.restaurant.restaurant.Restaurant;
import com.gosqu.restaurant.restaurant.dto.request.RestaurantRequest;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    @Mapping(target = "cuisineType", expression = "java(restaurant.getCuisineType().name())")
    RestaurantResponse toResponse(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "avgRating", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    @Mapping(target = "bannerUrl", ignore = true)
    Restaurant toEntity(RestaurantRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "avgRating", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    @Mapping(target = "bannerUrl", ignore = true)
    void updateEntity(RestaurantRequest request, @MappingTarget Restaurant restaurant);
}
