package pl.urban.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.urban.backend.enums.Role;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.UserRepository;
import pl.urban.backend.config.security.JwtUtil;
import pl.urban.backend.config.security.PasswordGenerator;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtToken;

    public  String googleLogin(OAuth2User principal) {
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        final User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(PasswordGenerator.generateRandomPassword());
            newUser.setRole(Role.user);
            return userRepository.save(newUser);
        });
        return jwtToken.generateToken(user);
    }

}
