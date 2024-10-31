package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.dto.*;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final AdditivesRepository additivesRepository;
    private final UserAddressRepository userAddressRepository;

    public OrderService(OrderRepository orderRepository, RestaurantRepository restaurantRepository,
                        UserRepository userRepository, MenuRepository menuRepository,
                        AdditivesRepository additivesRepository, UserAddressRepository userAddressRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.additivesRepository = additivesRepository;
        this.userAddressRepository = userAddressRepository;
    }

    @Transactional
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Transactional
    public  List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }



    @Transactional
    public Order createOrder(Order orderRequest) {
        try {
            Order order = new Order();

            order.setPaymentMethod(orderRequest.getPaymentMethod());
            order.setStatus(orderRequest.getStatus());
            order.setTotalPrice(orderRequest.getTotalPrice());
            order.setDeliveryTime(orderRequest.getDeliveryTime());
            order.setDeliveryOption(orderRequest.getDeliveryOption());
            order.setComment(orderRequest.getComment());

            Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurant().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
            order.setRestaurant(restaurant);

            User user = userRepository.findById(orderRequest.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            order.setUser(user);

            if (orderRequest.getUserAddress() != null && orderRequest.getUserAddress().getId() != null) {
                UserAddress userAddress = userAddressRepository.findById(orderRequest.getUserAddress().getId())
                        .orElseThrow(() -> new IllegalArgumentException("User address not found"));
                order.setUserAddress(userAddress);
            }


            List<OrderMenu> orderMenus = new ArrayList<>();
            if (orderRequest.getOrderMenus() != null && !orderRequest.getOrderMenus().isEmpty()) {
                for (OrderMenu requestOrderMenu : orderRequest.getOrderMenus()) {
                    OrderMenu orderMenu = new OrderMenu();

                    orderMenu.setQuantity(requestOrderMenu.getQuantity());

                    Menu menu = menuRepository.findById(requestOrderMenu.getMenu().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
                    orderMenu.setMenu(menu);

                    if (requestOrderMenu.getChooseAdditives() != null && !requestOrderMenu.getChooseAdditives().isEmpty()) {
                        List<Long> additiveIds = requestOrderMenu.getChooseAdditives().stream()
                                .map(Additives::getId)
                                .collect(Collectors.toList());
                        List<Additives> additives = additivesRepository.findAllById(additiveIds);

                        if (additives.size() != requestOrderMenu.getChooseAdditives().size()) {
                            throw new IllegalArgumentException("One or more additives not found");
                        }
                        orderMenu.setChooseAdditives(additives);
                    }

                    orderMenu.setOrder(order);
                    orderMenus.add(orderMenu);
                }
            }

            order.setOrderMenus(orderMenus);


            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }
}