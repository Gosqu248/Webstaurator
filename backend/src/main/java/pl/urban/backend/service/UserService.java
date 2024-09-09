package pl.urban.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    public User getCustomerBySubject(String subject) {
        return userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
    }

    public String changeName(String subject, String name) {
        User user = getCustomerBySubject(subject);
        user.setName(name);
        userRepository.save(user);
        return name;
    }

    public Boolean changePassword(String subject, String password, String newPassword) {
        User user = getCustomerBySubject(subject);
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }


}
