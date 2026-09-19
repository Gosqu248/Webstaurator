package com.gosqu.restaurant.menu.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID id) {
        super("Category not found: id=" + id);
    }
}
