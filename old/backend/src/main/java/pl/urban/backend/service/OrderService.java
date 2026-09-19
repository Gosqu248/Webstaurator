package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.dto.request.OrderMenuRequest;
import pl.urban.backend.dto.request.OrderRequest;
import pl.urban.backend.dto.response.AdminOrderResponse;
import pl.urban.backend.dto.response.OrderResponse;
import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final AdditivesRepository additivesRepository;
    private final UserAddressRepository userAddressRepository;
    private final MapperService mapper;

    public List<AdminOrderResponse> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate")).stream()
                .map(mapper::toAdminOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.setStatus(OrderStatus.zapłacone);
        return mapper.fromOrder(orderRepository.save(order));
    }

    @Transactional
    public List<OrderResponse> getUserOrders(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be empty");
        }

        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with email '" + subject + "' not found"));

        List<Order> orders = orderRepository.findByUserId(user.getId(), Sort.by(Sort.Direction.DESC, "orderDate"));
        return orders.stream()  // Changed from parallelStream to regular stream for predictable error handling
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }

        try {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new IllegalArgumentException(format("User not found with ID: %s", request.userId())));

            UserAddress userAddress = userAddressRepository.findById(request.userAddressId())
                    .orElseThrow(() -> new IllegalArgumentException(format("User address not found with ID: %s", request.userAddressId())));

            Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                    .orElseThrow(() -> new IllegalArgumentException(format("Restaurant not found with ID: %s", request.restaurantId())));

            List<OrderMenu> orderMenuEntities = new ArrayList<>();
            if (request.orderMenus() != null && !request.orderMenus().isEmpty()) {
                for (OrderMenuRequest menuReq : request.orderMenus()) {
                    if (menuReq == null) continue;

                    if (menuReq.menuId() == null) {
                        throw new IllegalArgumentException("Menu ID is required for order items");
                    }

                    Menu menu = menuRepository.findById(menuReq.menuId())
                            .orElseThrow(() -> new IllegalArgumentException(format("Menu item not found with ID: %s", menuReq.menuId())));

                    List<Additives> chosenAdditives = new ArrayList<>();
                    if (menuReq.chooseAdditivesId() != null && !menuReq.chooseAdditivesId().isEmpty()) {
                        List<Additives> additives = additivesRepository.findAllById(menuReq.chooseAdditivesId());
                        if (additives.size() != menuReq.chooseAdditivesId().size()) {
                            throw new IllegalArgumentException("One or more additives not found");
                        }
                        chosenAdditives = additives;
                    }

                    OrderMenu oMenu = OrderMenu.builder()
                            .menu(menu)
                            .quantity(menuReq.quantity())
                            .chooseAdditives(chosenAdditives)
                            .build();
                    orderMenuEntities.add(oMenu);
                }
            }

            if (orderMenuEntities.isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one menu item");
            }

            Order order = Order.builder()
                    .user(user)
                    .userAddress(userAddress)
                    .restaurant(restaurant)
                    .orderMenus(orderMenuEntities)
                    .deliveryOption(request.deliveryOption())
                    .deliveryTime(request.deliveryTime())
                    .paymentId(request.paymentId())
                    .paymentMethod(request.paymentMethod())
                    .status(request.status())
                    .totalPrice(request.totalPrice())
                    .comment(request.comment())
                    .build();

            orderMenuEntities.forEach(menu -> menu.setOrder(order));
            order.setOrderMenus(orderMenuEntities);

            Order savedOrder = orderRepository.save(order);
            return mapper.fromOrder(savedOrder);

        } catch (IllegalArgumentException e) {
            log.error("Validation error in createOrder: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }


    @Transactional
    public OrderResponse getOrderById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        return mapper.fromOrder(orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id)));
    }

    public List<OrderResponse> getAllRestaurantOrders(Long restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }

        return orderRepository.findAllByRestaurantId(restaurantId)
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }
}
