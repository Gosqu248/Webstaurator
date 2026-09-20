package com.gosqu.restaurant.common.messaging;

public final class KafkaTopics {

    public static final String RESTAURANT_CREATED = "restaurant.created";
    public static final String RESTAURANT_UPDATED = "restaurant.updated";
    public static final String RESTAURANT_DEACTIVATED = "restaurant.deactivated";

    public static final String MENU_ITEM_CREATED = "menu.item.created";
    public static final String MENU_ITEM_UPDATED = "menu.item.updated";
    public static final String MENU_ITEM_DELETED = "menu.item.deleted";

    private KafkaTopics() {
    }
}
