package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.dto.response.AdminOrderResponse;
import pl.urban.backend.dto.response.OrderResponse;
import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public OrderResponse createOrder(Order orderRequest) {
        if (orderRequest == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }

        try {
            Order order = new Order();

            // Basic validations
            if (orderRequest.getRestaurant() == null || orderRequest.getRestaurant().getId() == null) {
                throw new IllegalArgumentException("Restaurant information is required");
            }

            if (orderRequest.getUser() == null || orderRequest.getUser().getId() == null) {
                throw new IllegalArgumentException("User information is required");
            }

            order.setPaymentMethod(orderRequest.getPaymentMethod());
            order.setStatus(orderRequest.getStatus());
            order.setTotalPrice(orderRequest.getTotalPrice());
            order.setDeliveryTime(orderRequest.getDeliveryTime());
            order.setDeliveryOption(orderRequest.getDeliveryOption());
            order.setComment(orderRequest.getComment());
            order.setPaymentId(orderRequest.getPaymentId());

            // Find restaurant with proper error handling
            Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurant().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " +
                            orderRequest.getRestaurant().getId()));
            order.setRestaurant(restaurant);

            // Find user with proper error handling
            User user = userRepository.findById(orderRequest.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " +
                            orderRequest.getUser().getId()));
            order.setUser(user);

            // User address handling with null checks
            if (orderRequest.getUserAddress() != null && orderRequest.getUserAddress().getId() != null) {
                UserAddress userAddress = userAddressRepository.findById(orderRequest.getUserAddress().getId())
                        .orElseThrow(() -> new IllegalArgumentException("User address not found with ID: " +
                                orderRequest.getUserAddress().getId()));
                order.setUserAddress(userAddress);
            }

            List<OrderMenu> orderMenus = new ArrayList<>();
            if (orderRequest.getOrderMenus() != null && !orderRequest.getOrderMenus().isEmpty()) {
                for (OrderMenu requestOrderMenu : orderRequest.getOrderMenus()) {
                    if (requestOrderMenu == null) {
                        continue; // Skip null items
                    }

                    OrderMenu orderMenu = new OrderMenu();
                    orderMenu.setQuantity(requestOrderMenu.getQuantity());

                    // Validate menu item exists
                    if (requestOrderMenu.getMenu() == null || requestOrderMenu.getMenu().getId() == null) {
                        throw new IllegalArgumentException("Menu information is required for order items");
                    }

                    Menu menu = menuRepository.findById(requestOrderMenu.getMenu().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " +
                                    requestOrderMenu.getMenu().getId()));
                    orderMenu.setMenu(menu);

                    // Process additives with proper validation
                    if (requestOrderMenu.getChooseAdditives() != null && !requestOrderMenu.getChooseAdditives().isEmpty()) {
                        List<Long> additiveIds = requestOrderMenu.getChooseAdditives().stream()
                                .filter(additive -> additive != null && additive.getId() != null)
                                .map(Additives::getId)
                                .collect(Collectors.toList());

                        if (!additiveIds.isEmpty()) {
                            List<Additives> additives = additivesRepository.findAllById(additiveIds);

                            // Verify all requested additives were found
                            if (additives.size() != additiveIds.size()) {
                                throw new IllegalArgumentException("One or more additives not found");
                            }
                            orderMenu.setChooseAdditives(additives);
                        }
                    }

                    orderMenu.setOrder(order);
                    orderMenus.add(orderMenu);
                }
            }

            if (orderMenus.isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one menu item");
            }

            order.setOrderMenus(orderMenus);
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
