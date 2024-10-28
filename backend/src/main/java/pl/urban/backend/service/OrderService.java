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

    public OrderService(OrderRepository orderRepository, RestaurantRepository restaurantRepository,
                        UserRepository userRepository, MenuRepository menuRepository,
                        AdditivesRepository additivesRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.additivesRepository = additivesRepository;
    }

    @Transactional
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Transactional
    public  List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setDeliveryTime(order.getDeliveryTime());
        orderDTO.setComment(order.getComment());
        orderDTO.setRestaurantId(order.getRestaurant().getId());
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setOrderDate(order.getOrderDate());


        List<OrderMenuDTO> orderMenuDTOs = order.getOrderMenus().stream().map(orderMenu -> {
            OrderMenuDTO orderMenuDTO = new OrderMenuDTO();
            orderMenuDTO.setId(orderMenu.getId());
            orderMenuDTO.setQuantity(orderMenu.getQuantity());
            orderMenuDTO.setMenuId(orderMenu.getMenu().getId());
            orderMenuDTO.setMenuName(orderMenu.getMenu().getName());

            List<AdditivesDTO> additivesDTOs = orderMenu.getChooseAdditives().stream().map(additive -> {
                AdditivesDTO additivesDTO = new AdditivesDTO();
                additivesDTO.setId(additive.getId());
                additivesDTO.setName(additive.getName());
                additivesDTO.setValue(additive.getValue());
                return additivesDTO;
            }).collect(Collectors.toList());

            orderMenuDTO.setChooseAdditives(additivesDTOs);
            return orderMenuDTO;
        }).collect(Collectors.toList());

        orderDTO.setOrderMenus(orderMenuDTOs);
        return orderDTO;
    }

    @Transactional
    public Order createOrder(Order orderRequest) {
        try {
            Order order = new Order();

            order.setPaymentMethod(orderRequest.getPaymentMethod());
            order.setStatus(orderRequest.getStatus());
            order.setTotalPrice(orderRequest.getTotalPrice());
            order.setDeliveryTime(orderRequest.getDeliveryTime());
            order.setComment(orderRequest.getComment());

            Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurant().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
            order.setRestaurant(restaurant);

            User user = userRepository.findById(orderRequest.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            order.setUser(user);

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