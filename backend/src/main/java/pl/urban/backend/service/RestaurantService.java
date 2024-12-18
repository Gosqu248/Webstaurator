package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.dto.AddRestaurantDTO;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.*;

import java.util.*;

@Service
public class RestaurantService {

     private final RestaurantRepository restaurantRepository;
     private final GeocodingService geocodingService;
     private final PaymentRepository paymentRepository;
     private final AdditivesRepository additivesRepository;


    public RestaurantService(RestaurantRepository restaurantRepository, GeocodingService geocodingService, PaymentRepository paymentRepository, AdditivesRepository additivesRepository) {
        this.restaurantRepository = restaurantRepository;
        this.geocodingService = geocodingService;
        this.paymentRepository = paymentRepository;
        this.additivesRepository = additivesRepository;
    }


    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id).orElse(null);
    }

    @Transactional
    public Restaurant addRestaurant(AddRestaurantDTO addRestaurantDto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(addRestaurantDto.getName());
        restaurant.setCategory(addRestaurantDto.getCategory());
        restaurant.setLogoUrl(addRestaurantDto.getLogoUrl());
        restaurant.setImageUrl(addRestaurantDto.getImageUrl());


        if (addRestaurantDto.getRestaurantAddress() != null) {
            RestaurantAddress address = addRestaurantDto.getRestaurantAddress();
            String addressString = address.getStreet() + " " + address.getFlatNumber() + ", " + address.getZipCode() + " " + address.getCity();
            double[] coordinates = geocodingService.getCoordinates(addressString);
            address.setLatitude(coordinates[0]);
            address.setLongitude(coordinates[1]);
            address.setRestaurant(restaurant);
            restaurant.setRestaurantAddress(address);
        } else {
            throw new IllegalArgumentException("Restaurant address is required");
        }

        if (addRestaurantDto.getDelivery() != null) {
            Delivery delivery = addRestaurantDto.getDelivery();
            delivery.setRestaurant(restaurant);
            restaurant.setDelivery(delivery);
        } else {
            throw new IllegalArgumentException("Delivery is required");
        }

        if (addRestaurantDto.getDeliveryHours() != null) {
            for (DeliveryHour hour : addRestaurantDto.getDeliveryHours()) {
                hour.setRestaurant(restaurant);
            }
            restaurant.setDeliveryHours(addRestaurantDto.getDeliveryHours());
        } else {
            throw new IllegalArgumentException("Delivery hours are required");
        }

        if (addRestaurantDto.getPaymentMethods() != null) {
            Set<Payment> payments = new HashSet<>();
            for (String paymentName : addRestaurantDto.getPaymentMethods()) {
                Payment payment = paymentRepository.findByMethod(paymentName);
                if (payment != null) {
                    payments.add(payment);
                }
            }
            restaurant.setPayments(new ArrayList<>(payments));
        } else {
            throw new IllegalArgumentException("Payments are required");
        }

        if (addRestaurantDto.getMenu() != null) {
            Set<Menu> menus = new HashSet<>();
            for (Menu menuItem : addRestaurantDto.getMenu()) {
                if (menuItem.getAdditives() != null) {
                    List<Additives> updatedAdditives = new ArrayList<>();
                    for (Additives additive : menuItem.getAdditives()) {
                        Additives existingAdditive = additivesRepository.findByNameAndValue(additive.getName(), additive.getValue());
                        if (existingAdditive == null) {
                            additivesRepository.save(additive);
                            updatedAdditives.add(additive);
                        } else {
                            updatedAdditives.add(existingAdditive);
                        }
                    }
                    menuItem.setAdditives(updatedAdditives);
                    for (Additives additive : updatedAdditives) {
                        if (additive.getMenus() == null) {
                            additive.setMenus(new ArrayList<>());
                        }
                        additive.getMenus().add(menuItem);
                    }
                } else {
                    menuItem.setAdditives(new ArrayList<>());
                }
                menuItem.setRestaurant(Collections.singleton(restaurant));
                menus.add(menuItem);
            }
            restaurant.setMenu(menus);
        } else {
            throw new IllegalArgumentException("Menu is required");
        }

        return restaurantRepository.save(restaurant);
    }


    public String getLogo(Long id) {
        Restaurant restaurant = restaurantRepository.findLogoById(id);
        return restaurant.getLogoUrl();
    }

    @Transactional
    public Restaurant updateRestaurant(Long id, Restaurant updatedRestaurant) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        restaurant.setName(updatedRestaurant.getName());
        restaurant.setLogoUrl(updatedRestaurant.getLogoUrl());
        restaurant.setDelivery(updatedRestaurant.getDelivery());
        restaurant.setRestaurantAddress(updatedRestaurant.getRestaurantAddress());
        restaurant.setDeliveryHours(updatedRestaurant.getDeliveryHours());
        restaurant.setRestaurantOpinions(updatedRestaurant.getRestaurantOpinions());

        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
}