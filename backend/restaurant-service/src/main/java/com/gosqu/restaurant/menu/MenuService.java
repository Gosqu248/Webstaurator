package com.gosqu.restaurant.menu;

import com.gosqu.restaurant.menu.dto.request.CategoryRequest;
import com.gosqu.restaurant.menu.dto.request.MenuItemRequest;
import com.gosqu.restaurant.menu.dto.response.CategoryResponse;
import com.gosqu.restaurant.menu.dto.response.MenuItemResponse;
import com.gosqu.restaurant.menu.dto.response.MenuResponse;
import com.gosqu.restaurant.menu.exception.CategoryNotFoundException;
import com.gosqu.restaurant.menu.exception.MenuItemNotFoundException;
import com.gosqu.restaurant.menu.mapper.MenuMapper;
import com.gosqu.restaurant.restaurant.RestaurantService;
import com.gosqu.restaurant.restaurant.dto.response.RestaurantResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;
    private final MenuMapper menuMapper;

    @Cacheable(value = "menu", key = "#restaurantId")
    public MenuResponse getMenu(UUID restaurantId) {
        RestaurantResponse restaurantResponse = restaurantService.getById(restaurantId);

        List<MenuItem> allItems = menuItemRepository
                .findAllByRestaurantIdAndIsAvailableTrueOrderByCategoryId(restaurantId);
        Map<UUID, List<MenuItemResponse>> itemsByCategory = allItems.stream()
                .collect(Collectors.groupingBy(
                        MenuItem::getCategoryId,
                        Collectors.mapping(menuMapper::toResponse, Collectors.toList())
                ));

        List<MenuResponse.CategoryWithItemsResponse> categories =
                categoryRepository.findAllByRestaurantIdAndIsActiveTrueOrderByDisplayOrder(restaurantId)
                        .stream()
                        .map(c -> new MenuResponse.CategoryWithItemsResponse(
                                c.getId(), c.getName(), c.getDisplayOrder(),
                                itemsByCategory.getOrDefault(c.getId(), List.of())
                        ))
                        .toList();

        return new MenuResponse(restaurantResponse, categories);
    }

    @Transactional
    @CacheEvict(value = "menu", key = "#restaurantId")
    public CategoryResponse addCategory(UUID ownerId, UUID restaurantId, CategoryRequest request) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        Category category = Category.builder()
                .restaurantId(restaurantId)
                .name(request.name())
                .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
                .build();
        CategoryResponse response = menuMapper.toResponse(categoryRepository.save(category));
        log.info("category_added restaurantId={} categoryId={}", restaurantId, response.id());
        return response;
    }

    @Transactional
    @CacheEvict(value = "menu", key = "#restaurantId")
    public CategoryResponse updateCategory(UUID ownerId, UUID restaurantId, UUID categoryId,
                                           CategoryRequest request) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        Category category = categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        category.setName(request.name());
        if (request.displayOrder() != null) category.setDisplayOrder(request.displayOrder());
        CategoryResponse response = menuMapper.toResponse(categoryRepository.save(category));
        log.info("category_updated restaurantId={} categoryId={}", restaurantId, categoryId);
        return response;
    }

    @Transactional
    @CacheEvict(value = "menu", key = "#restaurantId")
    public void deleteCategory(UUID ownerId, UUID restaurantId, UUID categoryId) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        categoryRepository.deleteById(categoryId);
        log.info("category_deleted restaurantId={} categoryId={}", restaurantId, categoryId);
    }

    @Transactional
    @CacheEvict(value = "menu", key = "#restaurantId")
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
        MenuItemResponse response = menuMapper.toResponse(menuItemRepository.save(item));
        log.info("menu_item_added restaurantId={} itemId={}", restaurantId, response.id());
        return response;
    }

    @Transactional
    @CacheEvict(value = "menu", key = "#restaurantId")
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
        MenuItemResponse response = menuMapper.toResponse(menuItemRepository.save(item));
        log.info("menu_item_updated restaurantId={} itemId={}", restaurantId, itemId);
        return response;
    }

    @Transactional
    @CacheEvict(value = "menu", key = "#restaurantId")
    public void deleteMenuItem(UUID ownerId, UUID restaurantId, UUID itemId) {
        restaurantService.findOrThrowOwned(ownerId, restaurantId);
        menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new MenuItemNotFoundException(itemId));
        menuItemRepository.deleteById(itemId);
        log.info("menu_item_deleted restaurantId={} itemId={}", restaurantId, itemId);
    }
}
