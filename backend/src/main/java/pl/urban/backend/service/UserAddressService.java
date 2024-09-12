package pl.urban.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.User;
import pl.urban.backend.model.UserAddress;
import pl.urban.backend.repository.UserAddressRepository;
import pl.urban.backend.repository.UserRepository;

@Service
public class UserAddressService {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;


    public UserAddress addAddress(String subject, UserAddress userAddress) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        userAddress.setUser(user);
        return userAddressRepository.save(userAddress);
    }

    public void removeAddress(String subject, Long addressId) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id not found");
        }

        user.removeAddress(address);
        userRepository.save(user);
    }
}
