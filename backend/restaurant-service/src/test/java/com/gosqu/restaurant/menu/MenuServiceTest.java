package com.gosqu.restaurant.menu;

import com.gosqu.restaurant.menu.dto.request.CategoryRequest;
import com.gosqu.restaurant.menu.dto.request.MenuItemRequest;
import com.gosqu.restaurant.menu.dto.response.CategoryResponse;
import com.gosqu.restaurant.menu.exception.CategoryNotFoundException;
import com.gosqu.restaurant.menu.exception.MenuItemNotFoundException;
import com.gosqu.restaurant.restaurant.RestaurantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    static final UUID REST_ID  = UUID.fromString("00000000-0000-0000-0000-000000000001");
    static final UUID OWNER_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    static final UUID CAT_ID   = UUID.fromString("00000000-0000-0000-0000-000000000005");
    static final UUID ITEM_ID  = UUID.fromString("00000000-0000-0000-0000-000000000007");
    static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private MenuService service;

    @Nested
    @DisplayName("addCategory")
    class AddCategory {

        @Test
        @DisplayName("saves category when owner matches")
        void addCategory_ownerMatches_savesAndReturns() {
            var cat = Category.builder().id(CAT_ID).restaurantId(REST_ID).name("Przystawki").displayOrder(0).build();
            when(categoryRepository.save(any())).thenReturn(cat);

            CategoryResponse result = service.addCategory(OWNER_ID, REST_ID, new CategoryRequest("Przystawki", null));

            assertThat(result.name()).isEqualTo("Przystawki");
            verify(restaurantService).findOrThrowOwned(OWNER_ID, REST_ID);
        }
    }

    @Nested
    @DisplayName("deleteCategory")
    class DeleteCategory {

        @Test
        @DisplayName("deletes when category belongs to restaurant")
        void deleteCategory_belongsToRestaurant_deletes() {
            var cat = Category.builder().id(CAT_ID).restaurantId(REST_ID).name("Napoje").displayOrder(1).build();
            when(categoryRepository.findByIdAndRestaurantId(CAT_ID, REST_ID)).thenReturn(Optional.of(cat));

            service.deleteCategory(OWNER_ID, REST_ID, CAT_ID);

            verify(restaurantService).findOrThrowOwned(OWNER_ID, REST_ID);
            verify(categoryRepository).deleteById(CAT_ID);
        }

        @Test
        @DisplayName("throws CategoryNotFoundException when category belongs to another restaurant")
        void deleteCategory_differentRestaurant_throws() {
            when(categoryRepository.findByIdAndRestaurantId(OTHER_ID, REST_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteCategory(OWNER_ID, REST_ID, OTHER_ID))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessageContaining(OTHER_ID.toString());

            verify(restaurantService).findOrThrowOwned(OWNER_ID, REST_ID);
            verify(categoryRepository, never()).deleteById(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("deleteMenuItem")
    class DeleteMenuItem {

        @Test
        @DisplayName("deletes when item belongs to restaurant")
        void deleteMenuItem_belongsToRestaurant_deletes() {
            var item = MenuItem.builder().id(ITEM_ID).restaurantId(REST_ID).categoryId(CAT_ID).name("Pizza").build();
            when(menuItemRepository.findByIdAndRestaurantId(ITEM_ID, REST_ID)).thenReturn(Optional.of(item));

            service.deleteMenuItem(OWNER_ID, REST_ID, ITEM_ID);

            verify(restaurantService).findOrThrowOwned(OWNER_ID, REST_ID);
            verify(menuItemRepository).deleteById(ITEM_ID);
        }

        @Test
        @DisplayName("throws MenuItemNotFoundException when item belongs to another restaurant")
        void deleteMenuItem_differentRestaurant_throws() {
            when(menuItemRepository.findByIdAndRestaurantId(OTHER_ID, REST_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteMenuItem(OWNER_ID, REST_ID, OTHER_ID))
                    .isInstanceOf(MenuItemNotFoundException.class)
                    .hasMessageContaining(OTHER_ID.toString());

            verify(restaurantService).findOrThrowOwned(OWNER_ID, REST_ID);
            verify(menuItemRepository, never()).deleteById(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("updateCategory")
    class UpdateCategory {

        @Test
        @DisplayName("throws CategoryNotFoundException for missing category")
        void updateCategory_missingCategory_throwsCategoryNotFound() {
            when(categoryRepository.findByIdAndRestaurantId(OTHER_ID, REST_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateCategory(OWNER_ID, REST_ID, OTHER_ID, new CategoryRequest("X", null)))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessageContaining(OTHER_ID.toString());

            verify(restaurantService).findOrThrowOwned(OWNER_ID, REST_ID);
        }
    }

    @Nested
    @DisplayName("updateMenuItem")
    class UpdateMenuItem {

        @Test
        @DisplayName("throws MenuItemNotFoundException for missing item")
        void updateMenuItem_missingItem_throwsMenuItemNotFound() {
            when(menuItemRepository.findByIdAndRestaurantId(OTHER_ID, REST_ID)).thenReturn(Optional.empty());

            var request = new MenuItemRequest(CAT_ID, "Danie", null, new BigDecimal("10.00"), null, null);
            assertThatThrownBy(() -> service.updateMenuItem(OWNER_ID, REST_ID, OTHER_ID, request))
                    .isInstanceOf(MenuItemNotFoundException.class)
                    .hasMessageContaining(OTHER_ID.toString());

            verify(restaurantService).findOrThrowOwned(OWNER_ID, REST_ID);
        }
    }
}
