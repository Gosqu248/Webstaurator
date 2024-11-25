package pl.urban.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.UserRepository;
import pl.urban.backend.security.JwtUtil;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final DetailsUserService detailsUserService;
    private final JwtUtil jwtToken;

    public GoogleAuthService(UserRepository userRepository, DetailsUserService detailsUserService, JwtUtil jwtToken) {
        this.userRepository = userRepository;
        this.detailsUserService = detailsUserService;
        this.jwtToken = jwtToken;
    }


    public String googleLogin(OAuth2User principal) {
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            return userRepository.save(newUser);
        });

        final UserDetails userDetails = detailsUserService.loadUserByUsername(email);
        return jwtToken.generateToken(userDetails);
    }

}
