package pl.urban.backend.service;

import org.springframework.stereotype.Service;
import pl.urban.backend.model.User;
import pl.urban.backend.model.UserSecurity;
import pl.urban.backend.repository.UserSecurityRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserSecurityService {

    private final UserSecurityRepository userSecurityRepository;

    public UserSecurityService(UserSecurityRepository userSecurityRepository) {
        this.userSecurityRepository = userSecurityRepository;
    }

    public void incrementFailedLoginAttempts(User user) {
        UserSecurity userSecurity = user.getUserSecurity();
        if (userSecurity == null) {
            userSecurity = new UserSecurity();
            userSecurity.setUser(user);
            user.setUserSecurity(userSecurity);
        }
        userSecurity.setFailedLoginAttempts(userSecurity.getFailedLoginAttempts() + 1);
        userSecurity.setLastFailedLoginAttempt(LocalDateTime.now());
        userSecurityRepository.save(userSecurity);
    }

    public void resetFailedLoginAttempts(User user) {
        UserSecurity userSecurity = user.getUserSecurity();
        if (userSecurity == null) {
            userSecurity = new UserSecurity();
            userSecurity.setUser(user);
            user.setUserSecurity(userSecurity);
        }
        userSecurity.setFailedLoginAttempts(0);
        userSecurity.setLastFailedLoginAttempt(null);
        userSecurityRepository.save(userSecurity);
    }

    public boolean isAccountLocked(User user) {
        UserSecurity userSecurity = user.getUserSecurity();
        if (userSecurity == null) {
            return false;
        }
        if (userSecurity.getFailedLoginAttempts() >= 5) {
            LocalDateTime lockTIme = userSecurity.getLastFailedLoginAttempt().plusMinutes(10);
            return LocalDateTime.now().isBefore(lockTIme);
        }
        return false;
    }

    public void generateAndSendTwoFactorCode(User user) {
        UserSecurity userSecurity = user.getUserSecurity();
        if (userSecurity == null) {
            userSecurity = new UserSecurity();
            userSecurity.setUser(user);
            user.setUserSecurity(userSecurity);
        }
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        userSecurity.setTwoFactorCode(String.valueOf(code));
        userSecurity.setTwoFactorCodeExpireTime(System.currentTimeMillis() + 300000);
        userSecurityRepository.save(userSecurity);

    }

    public boolean verifyTwoFactorCode(User user, String code) {
        UserSecurity userSecurity = user.getUserSecurity();
        return  userSecurity != null && userSecurity.getTwoFactorCode().equals(code) && userSecurity.getTwoFactorCodeExpireTime() > System.currentTimeMillis();
    }

}
