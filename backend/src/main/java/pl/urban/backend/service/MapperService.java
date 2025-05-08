package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.dto.response.*;
import pl.urban.backend.model.*;

import java.util.List;

@Service
public class MapperService {
    public DeliveryHourResponse fromDeliveryHour(DeliveryHour deliveryHour) {
        return new DeliveryHourResponse(
                deliveryHour.getId(),
                deliveryHour.getDayOfWeek(),
                deliveryHour.getOpenTime(),
                deliveryHour.getCloseTime(),
                deliveryHour.getRestaurant().getId()
        );
    }
    public DeliveryResponse fromDelivery(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getDeliveryMinTime(),
                delivery.getDeliveryMaxTime(),
                delivery.getDeliveryPrice(),
                delivery.getMinimumPrice(),
                delivery.getPickupTime(),
                delivery.getRestaurant().getId()
        );
    }

    public AdditivesResponse fromAdditives(Additives additives) {
        return new AdditivesResponse(
                additives.getId(),
                additives.getName(),
                additives.getValue(),
                additives.getPrice()
        );
    }

    public MenuResponse fromMenu(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getCategory(),
                menu.getIngredients(),
                menu.getPrice(),
                menu.getImage(),
                menu.getAdditives().stream().map(this::fromAdditives).toList()
                );
    }

    public RestaurantResponse fromRestaurant(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getLogoUrl(),
                restaurant.getImageUrl()
        );
    }

    public PaymentResponse fromPayment(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMethod(),
                payment.getImage()
        );
    }

    public RestaurantAddressResponse fromRestaurantAddress(RestaurantAddress restaurantAddress) {
        return new RestaurantAddressResponse(
                restaurantAddress.getId(),
                restaurantAddress.getStreet(),
                restaurantAddress.getFlatNumber(),
                restaurantAddress.getCity(),
                restaurantAddress.getZipCode(),
                restaurantAddress.getLatitude(),
                restaurantAddress.getLongitude(),
                restaurantAddress.getRestaurant().getId()
        );
    }

    public OrderMenuResponse fromOrderMenu(OrderMenu orderMenu) {
        return new OrderMenuResponse(
                orderMenu.getId(),
                orderMenu.getQuantity(),
                fromMenu(orderMenu.getMenu()),
                orderMenu.getChooseAdditives().stream()
                        .map(additive -> new AdditivesResponse(
                                additive.getId(),
                                additive.getName(),
                                additive.getValue(),
                                additive.getPrice()))
                        .toList()
        );
    }

    public OrderResponse fromOrder(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getDeliveryOption(),
                order.getDeliveryTime(),
                order.getOrderDate(),
                order.getOrderMenus()
                        .stream()
                        .map(this::fromOrderMenu)
                        .toList(),
                order.getPaymentId(),
                order.getPaymentMethod(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getRestaurant().getLogoUrl(),
                order.getStatus(),
                order.getTotalPrice(),
                fromUserAddress(order.getUserAddress()),
                order.getRestaurantOpinion() != null
        );
    }
    public AdminOrderResponse toAdminOrderResponse(Order order) {
        UserAddress userAddress = order.getUserAddress();
        if (userAddress == null) {
            throw new IllegalArgumentException("User address not found for order with ID: " + order.getId());
        }
        User user = order.getUser();
        if (user == null) {
            throw new IllegalArgumentException("User not found for order with ID: " + order.getId());
        }

        return new AdminOrderResponse(
                order.getId(),
                order.getDeliveryOption(),
                order.getDeliveryTime(),
                order.getOrderDate(),
                order.getOrderMenus()
                        .stream()
                        .map(this::fromOrderMenu)
                        .toList(),
                order.getPaymentId(),
                order.getPaymentMethod(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getRestaurant().getLogoUrl(),
                order.getStatus(),
                order.getTotalPrice(),
                user.getName(),
                user.getEmail(),
                userAddress.getPhoneNumber(),
                userAddress.getStreet(),
                userAddress.getHouseNumber(),
                userAddress.getCity(),
                userAddress.getZipCode()
        );
    }

    public RestaurantOpinionResponse fromRestaurantOpinion(RestaurantOpinion opinion) {
        User user = opinion.getUser();
        if (user == null) {
            throw new IllegalArgumentException("User not found for opinion with ID: " + opinion.getId());
        }
        UserNameResponse userResponse = new UserNameResponse(user.getId(), user.getName());

        return new RestaurantOpinionResponse(
                opinion.getId(),
                opinion.getQualityRating(),
                opinion.getDeliveryRating(),
                opinion.getComment(),
                opinion.getCreatedAt(),
                userResponse
        );
    }

    public UserAddressResponse fromUserAddress(UserAddress userAddress) {
        return new UserAddressResponse(
                userAddress.getId(),
                userAddress.getStreet(),
                userAddress.getHouseNumber(),
                userAddress.getCity(),
                userAddress.getZipCode(),
                userAddress.getPhoneNumber(),
                userAddress.getFloorNumber(),
                userAddress.getAccessCode(),
                userAddress.getLatitude(),
                userAddress.getLongitude(),
                userAddress.getUser().getId()
        );
    }

    public FavouriteRestaurantResponse fromFavouriteRestaurant(FavouriteRestaurant favouriteRestaurant) {
        Restaurant restaurant = favouriteRestaurant.getRestaurant();
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found for favourite restaurant with ID: " + favouriteRestaurant.getId());
        }
        RestaurantAddress address = restaurant.getRestaurantAddress();
        if (address == null) {
            throw new IllegalArgumentException("Restaurant address not found for restaurant with ID: " + restaurant.getId());
        }

        List<OpinionResponse> opinions = restaurant.getRestaurantOpinions().stream()
                .map(restaurantOpinion -> new OpinionResponse(
                        restaurantOpinion.getId(),
                        restaurantOpinion.getQualityRating(),
                        restaurantOpinion.getDeliveryRating()
                ))
                .toList();

        return new FavouriteRestaurantResponse(
                favouriteRestaurant.getId(),
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getLogoUrl(),
                address.getStreet(),
                address.getFlatNumber(),
                opinions
        );
    }
}
