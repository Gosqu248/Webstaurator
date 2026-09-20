package com.gosqu.restaurant.search;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public record MenuItemSummary(
        @Field(type = FieldType.Keyword) String id,
        @Field(type = FieldType.Text) String name,
        @Field(type = FieldType.Text) String description,
        @Field(type = FieldType.Boolean) Boolean isAvailable
) {
}
