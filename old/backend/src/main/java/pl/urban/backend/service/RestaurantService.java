package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.dto.request.AddRestaurantRequest;
import pl.urban.backend.dto.response.AddRestaurantResponse;
import pl.urban.backend.dto.response.RestaurantResponse;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {
     private final RestaurantRepository restaurantRepository;
     private final GeocodingService geocodingService;
     private final PaymentRepository paymentRepository;
     private final AdditivesRepository additivesRepository;
     private final RestaurantAddressRepository restaurantAddressRepository;
     private final DeliveryRepository deliveryRepository;
     private final DeliveryHourRepository deliveryHourRepository;
     private final MapperService mapper;

    @Transactional
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        return mapper.fromRestaurant(restaurant);
    }

    @Transactional
    public RestaurantResponse addRestaurant(AddRestaurantRequest request) {
        validateRequest(request);

        RestaurantAddress address = request.restaurantAddress();
        String addressString = address.getStreet() + " " + address.getFlatNumber() + ", " + address.getZipCode() + " " + address.getCity();
        double[] coordinates = geocodingService.getCoordinates(addressString);
        address.setLatitude(coordinates[0]);
        address.setLongitude(coordinates[1]);

        List<Payment> payments = request.paymentMethods().stream()
                .map(paymentRepository::findByMethod)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Set<Menu> menuItems = processMenuItems(request.menu());

        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .category(request.category())
                .logoUrl(request.logoUrl())
                .imageUrl(request.imageUrl())
                .payments(payments)
                .menu(menuItems)
                .build();

        address.setRestaurant(restaurant);
        restaurant.setRestaurantAddress(address);

        Delivery delivery = request.delivery();
        delivery.setRestaurant(restaurant);
        restaurant.setDelivery(delivery);

        List<DeliveryHour> deliveryHours = request.deliveryHours();
        deliveryHours.forEach(hour -> hour.setRestaurant(restaurant));
        restaurant.setDeliveryHours(deliveryHours);

        menuItems.forEach(menuItem -> menuItem.setRestaurant(Collections.singleton(restaurant)));
        return mapper.fromRestaurant(restaurantRepository.save(restaurant));
    }


    public RestaurantResponse updateRestaurant(AddRestaurantRequest request, Long restaurantId) {
        validateRequest(request);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + restaurantId));

        restaurant.setName(request.name());
        restaurant.setCategory(request.category());
        restaurant.setLogoUrl(request.logoUrl());
        restaurant.setImageUrl(request.imageUrl());

        updateRestaurantAddress(restaurant, request.restaurantAddress());
        updateDelivery(restaurant, request.delivery());
        updateDeliveryHours(restaurant, request.deliveryHours());
        updatePaymentMethods(restaurant, request.paymentMethods());
        updateMenuItems(restaurant, request.menu());

        return mapper.fromRestaurant(restaurantRepository.save(restaurant));
    }

    @Transactional
    public void removeRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        if (restaurant.getRestaurantAddress() != null) {
            RestaurantAddress address = restaurant.getRestaurantAddress();
            restaurantAddressRepository.delete(address);
            restaurant.setRestaurantAddress(null);
        }
        if (restaurant.getDelivery() != null) {
            Delivery delivery = restaurant.getDelivery();
            deliveryRepository.delete(delivery);
            restaurant.setDelivery(null);
        }
        if (restaurant.getDeliveryHours() != null) {
            deliveryHourRepository.deleteAll(restaurant.getDeliveryHours());
            restaurant.getDeliveryHours().clear();
        }
        if (restaurant.getPayments() != null) {
            restaurant.getPayments().clear();
        }
        if (restaurant.getMenu() != null) {
            for (Menu menuItem : restaurant.getMenu()) {
                if (menuItem.getAdditives() != null) {
                    for (Additives additive : menuItem.getAdditives()) {
                        additive.getMenus().remove(menuItem);
                    }
                    menuItem.getAdditives().clear();
                }
            }
            restaurant.getMenu().clear();
        }

        restaurantRepository.delete(restaurant);
    }

    @Transactional
    public AddRestaurantResponse getRestaurantForEdit(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        return new AddRestaurantResponse(
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getLogoUrl(),
                restaurant.getImageUrl(),
                mapper.fromRestaurantAddress(restaurant.getRestaurantAddress()),
                restaurant.getPayments().stream()
                        .map(Payment::getMethod)
                        .collect(Collectors.toList()),
                mapper.fromDelivery(restaurant.getDelivery()),
                restaurant.getDeliveryHours().stream()
                        .map(mapper::fromDeliveryHour)
                        .toList(),
                restaurant.getMenu().stream()
                        .map(mapper::fromMenu)
                        .collect(Collectors.toSet())
        );
    }

    public String getLogo(Long id) {
        Restaurant restaurant = restaurantRepository.findLogoById(id);
        return restaurant.getLogoUrl();
    }

    public Map<String, String> getLogoAndImage(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + id));
        Map<String, String> logoAndImage = new HashMap<>();
        logoAndImage.put("logoUrl", restaurant.getLogoUrl());
        logoAndImage.put("imageUrl", restaurant.getImageUrl());
        return logoAndImage;
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(mapper::fromRestaurant)
                .collect(Collectors.toList());
    }

    private void validateRequest(AddRestaurantRequest request) {
        if (request.restaurantAddress() == null) {
            throw new IllegalArgumentException("Restaurant address is required");
        }
        if (request.delivery() == null) {
            throw new IllegalArgumentException("Delivery is required");
        }
        if (request.deliveryHours() == null) {
            throw new IllegalArgumentException("Delivery hours are required");
        }
        if (request.paymentMethods() == null) {
            throw new IllegalArgumentException("Payments are required");
        }
        if (request.menu() == null) {
            throw new IllegalArgumentException("Menu is required");
        }
    }

    private Set<Menu> processMenuItems(Set<Menu> menuItems) {
        return menuItems.stream()
                .peek(menuItem -> {
                    proccessAdditives(menuItem);
                    menuItem.setRestaurant(Collections.singleton(null));
                })
                .collect(Collectors.toSet());
    }

    private void proccessAdditives(Menu menuItem) {
        if (menuItem.getAdditives() != null) {
            List<Additives> updatedAdditives = menuItem.getAdditives().stream()
                    .map(additive -> {
                        Additives existingAdditive = additivesRepository.findByNameAndValue(additive.getName(), additive.getValue());
                        return existingAdditive != null ? existingAdditive : additivesRepository.save(additive);
                    })
                    .collect(Collectors.toList());
            menuItem.setAdditives(updatedAdditives);
        } else {
            menuItem.setAdditives(new ArrayList<>());
        }
    }

    private void updateRestaurantAddress(Restaurant restaurant, RestaurantAddress newAddress) {
        RestaurantAddress existingAddress = restaurant.getRestaurantAddress();

        if (existingAddress == null) {
            existingAddress = new RestaurantAddress();
            existingAddress.setRestaurant(restaurant);
            restaurant.setRestaurantAddress(existingAddress);
        }

        existingAddress.setStreet(newAddress.getStreet());
        existingAddress.setFlatNumber(newAddress.getFlatNumber());
        existingAddress.setCity(newAddress.getCity());
        existingAddress.setZipCode(newAddress.getZipCode());

        String addressString = newAddress.getStreet() + " " + newAddress.getFlatNumber() + ", " +
                newAddress.getZipCode() + " " + newAddress.getCity();
        double[] coordinates = geocodingService.getCoordinates(addressString);
        existingAddress.setLatitude(coordinates[0]);
        existingAddress.setLongitude(coordinates[1]);
    }

    private void updateDelivery(Restaurant restaurant, Delivery newDelivery) {
        Delivery existingDelivery = restaurant.getDelivery();

        if (existingDelivery == null) {
            existingDelivery = new Delivery();
            existingDelivery.setRestaurant(restaurant);
            restaurant.setDelivery(existingDelivery);
        }

        existingDelivery.setDeliveryMinTime(newDelivery.getDeliveryMinTime());
        existingDelivery.setDeliveryMaxTime(newDelivery.getDeliveryMaxTime());
        existingDelivery.setDeliveryPrice(newDelivery.getDeliveryPrice());
        existingDelivery.setMinimumPrice(newDelivery.getMinimumPrice());
        existingDelivery.setPickupTime(newDelivery.getPickupTime());
    }

    private void updateDeliveryHours(Restaurant restaurant, List<DeliveryHour> updatedHours) {
        List<DeliveryHour> existingHours = restaurant.getDeliveryHours();

        Map<Integer, DeliveryHour> hoursMap = existingHours.stream()
                .collect(Collectors.toMap(DeliveryHour::getDayOfWeek, hour -> hour));

        for (DeliveryHour updatedHour : updatedHours) {
            DeliveryHour existingHour = hoursMap.get(updatedHour.getDayOfWeek());
            if (existingHour != null) {
                existingHour.setOpenTime(updatedHour.getOpenTime());
                existingHour.setCloseTime(updatedHour.getCloseTime());
            } else {
                updatedHour.setRestaurant(restaurant);
                existingHours.add(updatedHour);
            }
        }
    }

    private void updatePaymentMethods(Restaurant restaurant, List<String> paymentMethods) {
        List<Payment> payments = paymentMethods.stream()
                .map(paymentRepository::findByMethod)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        restaurant.setPayments(payments);
    }

    private void updateMenuItems(Restaurant restaurant, Set<Menu> newMenuItems) {
        Set<Menu> existingMenuItems = restaurant.getMenu();

        for (Menu newMenuItem : newMenuItems) {
            Menu existingMenuItem = existingMenuItems.stream()
                    .filter(menu -> menu.getName().equals(newMenuItem.getName()))
                    .findFirst()
                    .orElse(null);

            if (existingMenuItem != null) {
                existingMenuItem.setCategory(newMenuItem.getCategory());
                existingMenuItem.setIngredients(newMenuItem.getIngredients());
                existingMenuItem.setPrice(newMenuItem.getPrice());
                existingMenuItem.setImage(newMenuItem.getImage());

                updateMenuAdditives(existingMenuItem, newMenuItem.getAdditives());
            } else {
                newMenuItem.setRestaurant(Collections.singleton(restaurant));

                proccessAdditives(newMenuItem);

                existingMenuItems.add(newMenuItem);
            }
        }
    }

    private void updateMenuAdditives(Menu menuItem, List<Additives> newAdditives) {
        if (newAdditives != null) {
            List<Additives> updatedAdditives = new ArrayList<>();

            for (Additives additive : newAdditives) {
                Additives existingAdditive = additivesRepository.findByNameAndValue(
                        additive.getName(), additive.getValue());

                if (existingAdditive == null) {
                    additivesRepository.save(additive);
                    updatedAdditives.add(additive);
                } else {
                    updatedAdditives.add(existingAdditive);
                }
            }

            menuItem.setAdditives(updatedAdditives);
        } else {
            menuItem.setAdditives(new ArrayList<>());
        }
    }
}
