package pl.urban.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.urban.backend.config.security.JwtUtil;
import pl.urban.backend.dto.request.SignInWithTokenRequest;
import pl.urban.backend.dto.request.UserRequest;
import pl.urban.backend.dto.response.LoginResponse;
import pl.urban.backend.dto.response.UserResponse;
import pl.urban.backend.dto.response.UserInfoForOrderResponse;
import pl.urban.backend.enums.Role;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public void registerUser(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(bCryptPasswordEncoder.encode(request.password()))
                .role(Role.user)
                .build();

        userRepository.save(user);
    }


    public LoginResponse signWithToken(@Valid SignInWithTokenRequest request) {
        String email = jwtUtil.extractSubject(request.token());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        String token = jwtUtil.generateAuthToken(user);

        return new LoginResponse(
                token,
                fromUser(user)
        );
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

    public String getRole(String subject) {
        User user = getUserBySubject(subject);
        return String.valueOf(user.getRole());
    }

    public UserResponse getUser(String subject) {
        User user = getUserBySubject(subject);
        return fromUser(user);
    }

    public String changeName(String subject, String name) {
        User user = getUserBySubject(subject);
        user.setName(name);
        userRepository.save(user);
        return name;
    }

    public UserInfoForOrderResponse getUserInfo(String subject) {
        User user = getUserBySubject(subject);
        return new UserInfoForOrderResponse(user.getId(), user.getName(), user.getEmail(), user.getAddresses().isEmpty() ? null : user.getAddresses().getFirst().getPhoneNumber());
    }

    public UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                String.valueOf(user.getRole())
        );
    }

}
