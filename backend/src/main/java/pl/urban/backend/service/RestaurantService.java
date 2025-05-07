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

    @Transactional
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        return fromRestaurant(restaurant);
    }

    @Transactional
    public RestaurantResponse addRestaurant(AddRestaurantRequest addRestaurantRequest) {
        if (addRestaurantRequest.restaurantAddress() == null) {
            throw new IllegalArgumentException("Restaurant address is required");
        }
        if (addRestaurantRequest.delivery() == null) {
            throw new IllegalArgumentException("Delivery is required");
        }
        if (addRestaurantRequest.deliveryHours() == null) {
            throw new IllegalArgumentException("Delivery hours are required");
        }
        if (addRestaurantRequest.paymentMethods() == null) {
            throw new IllegalArgumentException("Payments are required");
        }
        if (addRestaurantRequest.menu() == null) {
            throw new IllegalArgumentException("Menu is required");
        }

        RestaurantAddress address = addRestaurantRequest.restaurantAddress();
        String addressString = address.getStreet() + " " + address.getFlatNumber() + ", " + address.getZipCode() + " " + address.getCity();
        double[] coordinates = geocodingService.getCoordinates(addressString);
        address.setLatitude(coordinates[0]);
        address.setLongitude(coordinates[1]);

        Delivery delivery = addRestaurantRequest.delivery();
        delivery.setRestaurant(null); // Will be set later

        List<DeliveryHour> deliveryHours = addRestaurantRequest.deliveryHours();
        deliveryHours.forEach(hour -> hour.setRestaurant(null)); // Will be set later

        Set<Payment> payments = addRestaurantRequest.paymentMethods().stream()
                .map(paymentRepository::findByMethod)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Menu> menus = addRestaurantRequest.menu().stream()
                .peek(menuItem -> {
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
                    menuItem.setRestaurant(Collections.singleton(null));
                })
                .collect(Collectors.toSet());

        Restaurant restaurant = Restaurant.builder()
                .name(addRestaurantRequest.name())
                .category(addRestaurantRequest.category())
                .logoUrl(addRestaurantRequest.logoUrl())
                .imageUrl(addRestaurantRequest.imageUrl())
                .restaurantAddress(address)
                .delivery(delivery)
                .deliveryHours(deliveryHours)
                .payments(new ArrayList<>(payments))
                .menu(menus)
                .build();

        address.setRestaurant(restaurant);
        delivery.setRestaurant(restaurant);
        deliveryHours.forEach(hour -> hour.setRestaurant(restaurant));
        menus.forEach(menuItem -> menuItem.setRestaurant(Collections.singleton(restaurant)));

        return fromRestaurant(restaurantRepository.save(restaurant));
    }


    @Transactional
    public RestaurantResponse updateRestaurant(AddRestaurantRequest addRestaurantRequest, Long restaurantId) {
        // Find the existing restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + restaurantId));

        // Update basic details
        restaurant.setName(addRestaurantRequest.name());
        restaurant.setCategory(addRestaurantRequest.category());
        restaurant.setLogoUrl(addRestaurantRequest.logoUrl());
        restaurant.setImageUrl(addRestaurantRequest.imageUrl());

        // Update address
        if (addRestaurantRequest.restaurantAddress() != null) {
            RestaurantAddress newAddress = addRestaurantRequest.restaurantAddress();
            RestaurantAddress existingAddress = restaurant.getRestaurantAddress();

            if (existingAddress == null) {
                existingAddress = new RestaurantAddress();
                existingAddress.setRestaurant(restaurant);
            }

            existingAddress.setStreet(newAddress.getStreet());
            existingAddress.setFlatNumber(newAddress.getFlatNumber());
            existingAddress.setCity(newAddress.getCity());
            existingAddress.setZipCode(newAddress.getZipCode());
            existingAddress.setLatitude(newAddress.getLatitude());
            existingAddress.setLongitude(newAddress.getLongitude());

            restaurant.setRestaurantAddress(existingAddress);
        } else {
            throw new IllegalArgumentException("Restaurant address is required");
        }

        // Update delivery
        if (addRestaurantRequest.delivery() != null) {
            Delivery newDelivery = addRestaurantRequest.delivery();
            Delivery existingDelivery = restaurant.getDelivery();

            if (existingDelivery == null) {
                existingDelivery = new Delivery();
                existingDelivery.setRestaurant(restaurant);
            }

            existingDelivery.setDeliveryMinTime(newDelivery.getDeliveryMinTime());
            existingDelivery.setDeliveryMaxTime(newDelivery.getDeliveryMaxTime());
            existingDelivery.setDeliveryPrice(newDelivery.getDeliveryPrice());
            existingDelivery.setMinimumPrice(newDelivery.getMinimumPrice());
            existingDelivery.setPickupTime(newDelivery.getPickupTime());

            restaurant.setDelivery(existingDelivery);
        } else {
            throw new IllegalArgumentException("Delivery is required");
        }

        // Update delivery hours
        if (addRestaurantRequest.deliveryHours() != null) {
            List<DeliveryHour> existingHours = restaurant.getDeliveryHours();
            List<DeliveryHour> updatedHours = addRestaurantRequest.deliveryHours();

            // Map existing hours by dayOfWeek for quick lookup
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

            restaurant.setDeliveryHours(existingHours);
        } else {
            throw new IllegalArgumentException("Delivery hours are required");
        }

        // Update payment methods
        if (addRestaurantRequest.paymentMethods() != null) {
            List<Payment> payments = new ArrayList<>();
            for (String paymentName : addRestaurantRequest.paymentMethods()) {
                Payment payment = paymentRepository.findByMethod(paymentName);
                if (payment != null) {
                    payments.add(payment);
                }
            }
            restaurant.setPayments(payments);
        } else {
            throw new IllegalArgumentException("Payments are required");
        }

        // Update menu items
        if (addRestaurantRequest.menu() != null) {
            Set<Menu> existingMenus = restaurant.getMenu();
            for (Menu newMenuItem : addRestaurantRequest.menu()) {
                Menu existingMenuItem = existingMenus.stream()
                        .filter(menu -> menu.getName().equals(newMenuItem.getName()))
                        .findFirst()
                        .orElse(null);

                if (existingMenuItem != null) {
                    existingMenuItem.setCategory(newMenuItem.getCategory());
                    existingMenuItem.setIngredients(newMenuItem.getIngredients());
                    existingMenuItem.setPrice(newMenuItem.getPrice());
                    existingMenuItem.setImage(newMenuItem.getImage());

                    // Update additives
                    if (newMenuItem.getAdditives() != null) {
                        List<Additives> updatedAdditives = new ArrayList<>();
                        for (Additives additive : newMenuItem.getAdditives()) {
                            Additives existingAdditive = additivesRepository.findByNameAndValue(additive.getName(), additive.getValue());
                            if (existingAdditive == null) {
                                additivesRepository.save(additive);
                                updatedAdditives.add(additive);
                            } else {
                                updatedAdditives.add(existingAdditive);
                            }
                        }
                        existingMenuItem.setAdditives(updatedAdditives);
                    }
                } else {
                    newMenuItem.setRestaurant(Collections.singleton(restaurant));
                    existingMenus.add(newMenuItem);
                }
            }
            restaurant.setMenu(existingMenus);
        } else {
            throw new IllegalArgumentException("Menu is required");
        }

        return fromRestaurant(restaurantRepository.save(restaurant));
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
                restaurant.getRestaurantAddress(),
                restaurant.getPayments().stream()
                        .map(Payment::getMethod)
                        .collect(Collectors.toList()),
                restaurant.getDelivery(),
                restaurant.getDeliveryHours(),
                restaurant.getMenu()
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
                .map(this::fromRestaurant)
                .collect(Collectors.toList());
    }

    private RestaurantResponse fromRestaurant(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getLogoUrl(),
                restaurant.getImageUrl()
        );
    }
}
