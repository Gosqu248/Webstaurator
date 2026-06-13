package com.gosqu.restaurant.menu;

import com.gosqu.restaurant.menu.dto.request.CategoryRequest;
import com.gosqu.restaurant.menu.dto.request.MenuItemRequest;
import com.gosqu.restaurant.menu.dto.response.CategoryResponse;
import com.gosqu.restaurant.menu.dto.response.MenuItemResponse;
import com.gosqu.restaurant.menu.dto.response.MenuResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/restaurants/{id}")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/menu")
    public MenuResponse getMenu(@PathVariable UUID id) {
        return menuService.getMenu(id);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse addCategory(@RequestHeader("X-User-Id") UUID ownerId,
                                        @PathVariable UUID id,
                                        @RequestBody @Valid CategoryRequest request) {
        return menuService.addCategory(ownerId, id, request);
    }

    @PutMapping("/categories/{catId}")
    public CategoryResponse updateCategory(@RequestHeader("X-User-Id") UUID ownerId,
                                           @PathVariable UUID id,
                                           @PathVariable UUID catId,
                                           @RequestBody @Valid CategoryRequest request) {
        return menuService.updateCategory(ownerId, id, catId, request);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@RequestHeader("X-User-Id") UUID ownerId,
                                @PathVariable UUID id,
                                @PathVariable UUID catId) {
        menuService.deleteCategory(ownerId, id, catId);
    }

    @PostMapping("/menu/items")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse addMenuItem(@RequestHeader("X-User-Id") UUID ownerId,
                                        @PathVariable UUID id,
                                        @RequestBody @Valid MenuItemRequest request) {
        return menuService.addMenuItem(ownerId, id, request);
    }

    @PutMapping("/menu/items/{itemId}")
    public MenuItemResponse updateMenuItem(@RequestHeader("X-User-Id") UUID ownerId,
                                           @PathVariable UUID id,
                                           @PathVariable UUID itemId,
                                           @RequestBody @Valid MenuItemRequest request) {
        return menuService.updateMenuItem(ownerId, id, itemId, request);
    }

    @DeleteMapping("/menu/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@RequestHeader("X-User-Id") UUID ownerId,
                                @PathVariable UUID id,
                                @PathVariable UUID itemId) {
        menuService.deleteMenuItem(ownerId, id, itemId);
    }
}
