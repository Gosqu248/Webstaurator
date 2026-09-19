package com.gosqu.restaurant.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosqu.restaurant.common.exception.GlobalExceptionHandler;
import com.gosqu.restaurant.menu.dto.request.CategoryRequest;
import com.gosqu.restaurant.menu.dto.response.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    static final UUID REST_ID  = UUID.fromString("00000000-0000-0000-0000-000000000001");
    static final UUID CAT_ID   = UUID.fromString("00000000-0000-0000-0000-000000000005");
    static final UUID OWNER_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");

    @Mock
    MenuService menuService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(menuService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("POST /restaurants/{id}/categories")
    class AddCategory {

        @Test
        @DisplayName("returns 400 when category name is blank")
        void addCategory_blankName_returns400() throws Exception {
            var invalid = new CategoryRequest("", null);

            mockMvc.perform(post("/restaurants/" + REST_ID + "/categories")
                            .header("X-User-Id", OWNER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 201 with valid category request")
        void addCategory_valid_returns201() throws Exception {
            var cat = new CategoryResponse(CAT_ID, "Przystawki", 0);
            when(menuService.addCategory(any(UUID.class), any(UUID.class), any())).thenReturn(cat);

            mockMvc.perform(post("/restaurants/" + REST_ID + "/categories")
                            .header("X-User-Id", OWNER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CategoryRequest("Przystawki", 0))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Przystawki"));
        }
    }

    @Nested
    @DisplayName("DELETE /restaurants/{id}/categories/{catId}")
    class DeleteCategory {

        @Test
        @DisplayName("returns 204 when category successfully deleted")
        void deleteCategory_success_returns204() throws Exception {
            mockMvc.perform(delete("/restaurants/" + REST_ID + "/categories/" + CAT_ID)
                            .header("X-User-Id", OWNER_ID.toString()))
                    .andExpect(status().isNoContent());
        }
    }
}
