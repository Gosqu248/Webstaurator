package com.gosqu.restaurant.menu;

import com.gosqu.restaurant.menu.dto.request.CategoryRequest;
import com.gosqu.restaurant.menu.dto.request.MenuItemRequest;
import com.gosqu.restaurant.menu.dto.response.CategoryResponse;
import com.gosqu.restaurant.menu.dto.response.MenuItemResponse;
import com.gosqu.restaurant.menu.dto.response.MenuResponse;
import com.gosqu.restaurant.menu.exception.CategoryNotFoundException;
import com.gosqu.restaurant.menu.exception.MenuItemNotFoundException;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import com.gosqu.restaurant.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    public MenuResponse getMenu(UUID restaurantId) {
        RestaurantResponse restaurantResponse = restaurantService.getById(restaurantId);
        List<MenuResponse.CategoryWithItemsResponse> categories =
                categoryRepository.findAllByRestaurantIdAndIsActiveTrueOrderByDisplayOrder(restaurantId)
                        .stream()
                        .map(c -> new MenuResponse.CategoryWithItemsResponse(
                                c.getId(), c.getName(), c.getDisplayOrder(),
                                menuItemRepository.findAllByCategoryIdAndIsAvailableTrue(c.getId())
                                        .stream().map(MenuItemResponse::from).toList()
                        ))
                        .toList();
        return new MenuResponse(restaurantResponse, categories);
    }

    @Transactional
    public CategoryResponse addCategory(UUID ownerId, UUID restaurantId, CategoryRequest request) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        Category category = Category.builder()
                .restaurantId(restaurantId)
                .name(request.name())
                .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(UUID ownerId, UUID restaurantId, UUID categoryId,
                                           CategoryRequest request) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        Category category = categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        category.setName(request.name());
        if (request.displayOrder() != null) category.setDisplayOrder(request.displayOrder());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID ownerId, UUID restaurantId, UUID categoryId) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public MenuItemResponse addMenuItem(UUID ownerId, UUID restaurantId, MenuItemRequest request) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        MenuItem item = MenuItem.builder()
                .restaurantId(restaurantId)
                .categoryId(request.categoryId())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .preparationTimeMin(request.preparationTimeMin())
                .calories(request.calories())
                .build();
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemResponse updateMenuItem(UUID ownerId, UUID restaurantId, UUID itemId,
                                           MenuItemRequest request) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        MenuItem item = menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new MenuItemNotFoundException(itemId));
        item.setCategoryId(request.categoryId());
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setPreparationTimeMin(request.preparationTimeMin());
        item.setCalories(request.calories());
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public void deleteMenuItem(UUID ownerId, UUID restaurantId, UUID itemId) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new MenuItemNotFoundException(itemId));
        menuItemRepository.deleteById(itemId);
    }
}
