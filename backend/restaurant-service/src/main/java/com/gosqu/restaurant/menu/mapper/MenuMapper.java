package com.gosqu.restaurant.menu.mapper;

import com.gosqu.restaurant.menu.Category;
import com.gosqu.restaurant.menu.MenuItem;
import com.gosqu.restaurant.menu.dto.response.CategoryResponse;
import com.gosqu.restaurant.menu.dto.response.MenuItemResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    CategoryResponse toResponse(Category category);

    MenuItemResponse toResponse(MenuItem menuItem);
}
