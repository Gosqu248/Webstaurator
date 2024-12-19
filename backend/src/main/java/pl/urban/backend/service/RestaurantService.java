package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.urban.backend.dto.AddRestaurantDTO;
import pl.urban.backend.model.*;
import pl.urban.backend.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

     private final RestaurantRepository restaurantRepository;
     private final GeocodingService geocodingService;
     private final PaymentRepository paymentRepository;
     private final AdditivesRepository additivesRepository;
     private final RestaurantAddressRepository restaurantAddressRepository;
     private final DeliveryRepository deliveryRepository;
     private final DeliveryHourRepository deliveryHourRepository;


    public RestaurantService(RestaurantRepository restaurantRepository, GeocodingService geocodingService, PaymentRepository paymentRepository, AdditivesRepository additivesRepository, RestaurantAddressRepository restaurantAddressRepository, DeliveryRepository deliveryRepository, DeliveryHourRepository deliveryHourRepository) {
        this.restaurantRepository = restaurantRepository;
        this.geocodingService = geocodingService;
        this.paymentRepository = paymentRepository;
        this.additivesRepository = additivesRepository;
        this.restaurantAddressRepository = restaurantAddressRepository;
        this.deliveryRepository = deliveryRepository;
        this.deliveryHourRepository = deliveryHourRepository;
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


    @Transactional
    public Restaurant updateRestaurant(AddRestaurantDTO addRestaurantDto, Long restaurantId) {
        // Find the existing restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + restaurantId));

        // Update basic details
        restaurant.setName(addRestaurantDto.getName());
        restaurant.setCategory(addRestaurantDto.getCategory());
        restaurant.setLogoUrl(addRestaurantDto.getLogoUrl());
        restaurant.setImageUrl(addRestaurantDto.getImageUrl());

        // Update address
        if (addRestaurantDto.getRestaurantAddress() != null) {
            RestaurantAddress newAddress = addRestaurantDto.getRestaurantAddress();
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
        if (addRestaurantDto.getDelivery() != null) {
            Delivery newDelivery = addRestaurantDto.getDelivery();
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
        if (addRestaurantDto.getDeliveryHours() != null) {
            List<DeliveryHour> existingHours = restaurant.getDeliveryHours();
            List<DeliveryHour> updatedHours = addRestaurantDto.getDeliveryHours();

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
        if (addRestaurantDto.getPaymentMethods() != null) {
            List<Payment> payments = new ArrayList<>();
            for (String paymentName : addRestaurantDto.getPaymentMethods()) {
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
        if (addRestaurantDto.getMenu() != null) {
            Set<Menu> existingMenus = restaurant.getMenu();
            for (Menu newMenuItem : addRestaurantDto.getMenu()) {
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

        return restaurantRepository.save(restaurant);
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
    public AddRestaurantDTO getRestaurantForEdit(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        AddRestaurantDTO addRestaurantDto = new AddRestaurantDTO();
        addRestaurantDto.setName(restaurant.getName());
        addRestaurantDto.setCategory(restaurant.getCategory());
        addRestaurantDto.setLogoUrl(restaurant.getLogoUrl());
        addRestaurantDto.setImageUrl(restaurant.getImageUrl());

        if (restaurant.getRestaurantAddress() != null) {
            RestaurantAddress address = restaurant.getRestaurantAddress();
            RestaurantAddress dtoAddress = new RestaurantAddress();
            dtoAddress.setStreet(address.getStreet());
            dtoAddress.setFlatNumber(address.getFlatNumber());
            dtoAddress.setZipCode(address.getZipCode());
            dtoAddress.setCity(address.getCity());

            addRestaurantDto.setRestaurantAddress(dtoAddress);
        }

        if (restaurant.getDelivery() != null) {
            Delivery delivery = new Delivery();
            delivery.setDeliveryPrice(restaurant.getDelivery().getDeliveryPrice());
            delivery.setMinimumPrice(restaurant.getDelivery().getMinimumPrice());
            delivery.setDeliveryMinTime(restaurant.getDelivery().getDeliveryMinTime());
            delivery.setDeliveryMaxTime(restaurant.getDelivery().getDeliveryMaxTime());
            delivery.setPickupTime(restaurant.getDelivery().getPickupTime());
            addRestaurantDto.setDelivery(delivery);
        }

        if (restaurant.getDeliveryHours() != null) {
            List<DeliveryHour> deliveryHours = new ArrayList<>(restaurant.getDeliveryHours());
            addRestaurantDto.setDeliveryHours(deliveryHours);
        }

        if (restaurant.getPayments() != null) {
            List<String> paymentMethods = restaurant.getPayments()
                    .stream()
                    .map(Payment::getMethod)
                    .collect(Collectors.toList());
            addRestaurantDto.setPaymentMethods(paymentMethods);
        }

        if (restaurant.getMenu() != null) {
            Set<Menu> menus = new HashSet<>();
            for (Menu menuItem : restaurant.getMenu()) {
                Menu dtoMenuItem = new Menu();
                dtoMenuItem.setName(menuItem.getName());
                dtoMenuItem.setCategory(menuItem.getCategory());
                dtoMenuItem.setIngredients(menuItem.getIngredients());
                dtoMenuItem.setImage(menuItem.getImage());
                dtoMenuItem.setPrice(menuItem.getPrice());

                if (menuItem.getAdditives() != null) {
                    List<Additives> additives = new ArrayList<>(menuItem.getAdditives());
                    dtoMenuItem.setAdditives(additives);
                } else {
                    dtoMenuItem.setAdditives(new ArrayList<>());
                }

                menus.add(dtoMenuItem);
            }
            addRestaurantDto.setMenu(menus);
        }

        return addRestaurantDto;
    }



    public String getLogo(Long id) {
        Restaurant restaurant = restaurantRepository.findLogoById(id);
        return restaurant.getLogoUrl();
    }


    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
}