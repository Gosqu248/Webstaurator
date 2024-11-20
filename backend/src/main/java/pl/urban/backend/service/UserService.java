package pl.urban.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.urban.backend.dto.UserDTO;
import pl.urban.backend.dto.UserInfoForOrderDTO;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public void registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Boolean changePassword(String subject, String password, String newPassword) {
        User user = getUserBySubject(subject);
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }


    public User getUserBySubject(String subject) {
        return userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
    }

    public UserDTO getUser(String subject) {
        User user = getUserBySubject(subject);
        return convertToDTO(user);
    }

    public String changeName(String subject, String name) {
        User user = getUserBySubject(subject);
        user.setName(name);
        userRepository.save(user);
        return name;
    }


    public UserInfoForOrderDTO getUserInfo(String subject) {
        User user = getUserBySubject(subject);
        UserInfoForOrderDTO userInfo = new UserInfoForOrderDTO();
        userInfo.setId(user.getId());
        userInfo.setName(user.getName());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhoneNumber(user.getAddresses().isEmpty() ? null : user.getAddresses().getFirst().getPhoneNumber());
        return userInfo;
    }

    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }


}
