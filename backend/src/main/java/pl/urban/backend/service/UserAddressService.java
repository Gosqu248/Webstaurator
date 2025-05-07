package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.request.UserAddressRequest;
import pl.urban.backend.dto.response.UserAddressResponse;
import pl.urban.backend.model.User;
import pl.urban.backend.model.UserAddress;
import pl.urban.backend.repository.UserAddressRepository;
import pl.urban.backend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressService {
     private  final UserAddressRepository userAddressRepository;
     private final UserRepository userRepository;
     private final GeocodingService geocodingService;
     private final MapperService mapper;

    public UserAddressResponse findAddressById(String subject, Long addressId) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address does not belong to the user");
        }
        return mapper.fromUserAddress(address);
    }

    public List<UserAddressResponse> findAvailableAddresses(String subject, double lat, double lon, double radiusKm) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        List<UserAddress> userAddresses = userAddressRepository.findAvailableAddresses(user.getId(), lat, lon, radiusKm);

        return userAddresses.stream()
                .map(mapper::fromUserAddress)
                .toList();
    }


    public UserAddressResponse addAddress(String subject, UserAddressRequest addressRequest) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        String address = addressRequest.street() + " " + addressRequest.houseNumber() + ", " + addressRequest.city();
        double[] coords = geocodingService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];

        UserAddress userAddress = toUserAddress(addressRequest, user, latitude, longitude);
        return mapper.fromUserAddress(userAddress);
    }

    public List<UserAddressResponse> getAllAddresses(String subject) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        return user.getAddresses().stream()
                .map(mapper::fromUserAddress)
                .toList();
    }

    public void deleteAddress(String subject, Long addressId) {
     User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if(!userAddress.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id not found");
        }

        userAddress.getUser().setId(null);
        userAddressRepository.save(userAddress);
    }


    public UserAddress updateAddress(String subject, Long addressId, UserAddress updatedAddress) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if(!userAddress.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id not found");
        }

        userAddress.setStreet(updatedAddress.getStreet());
        userAddress.setHouseNumber(updatedAddress.getHouseNumber());
        userAddress.setFloorNumber(updatedAddress.getFloorNumber());
        userAddress.setAccessCode(updatedAddress.getAccessCode());
        userAddress.setZipCode(updatedAddress.getZipCode());
        userAddress.setCity(updatedAddress.getCity());
        userAddress.setPhoneNumber(updatedAddress.getPhoneNumber());

        return userAddressRepository.save(userAddress);

    }

    private UserAddress toUserAddress(UserAddressRequest addressRequest, User user, double latitude, double longitude) {
        return UserAddress.builder()
                .street(addressRequest.street())
                .houseNumber(addressRequest.houseNumber())
                .floorNumber(addressRequest.floorNumber())
                .accessCode(addressRequest.accessCode())
                .zipCode(addressRequest.zipCode())
                .city(addressRequest.city())
                .phoneNumber(addressRequest.phoneNumber())
                .latitude(latitude)
                .longitude(longitude)
                .user(user)
                .build();
    }

}
