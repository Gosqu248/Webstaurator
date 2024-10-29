package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.model.User;
import pl.urban.backend.model.UserAddress;
import pl.urban.backend.repository.UserAddressRepository;
import pl.urban.backend.repository.UserRepository;

import java.util.List;

@Service
public class UserAddressService {

     private  final UserAddressRepository userAddressRepository;

     private final UserRepository userRepository;

    public UserAddressService(UserAddressRepository userAddressRepository, UserRepository userRepository) {
        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
    }


    public UserAddress addAddress(String subject, UserAddress userAddress) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        userAddress.setUserId(user.getId());
        return userAddressRepository.save(userAddress);
    }

    public List<UserAddress> getAllAddresses(String subject) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        return user.getAddresses();
    }

    public void deleteAddress(String subject, Long addressId) {
     User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if(!userAddress.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id not found");
        }

        userAddress.setUserId(null);
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



}
