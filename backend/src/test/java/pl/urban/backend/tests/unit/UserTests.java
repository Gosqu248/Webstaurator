package pl.urban.backend.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.urban.backend.dto.UserInfoForOrderResponse;
import pl.urban.backend.dto.UserResponse;
import pl.urban.backend.enums.Role;
import pl.urban.backend.model.User;
import pl.urban.backend.repository.UserRepository;
import pl.urban.backend.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserTests {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testChangePassword_Success() {
        User user = new User();
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        boolean result = userService.changePassword("user@example.com", "oldPassword", "newPassword");

        assertTrue(result);
        verify(userRepository, times(1)).save(user);
        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        User user = new User();
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword("user@example.com", "oldPassword", "newPassword"));

        assertEquals("Old password does not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserBySubject_UserExists() {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserBySubject("user@example.com");

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void testGetUserBySubject_UserDoesNotExist() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getUserBySubject("user@example.com"));

        assertEquals("User with this email not found", exception.getMessage());
    }

    @Test
    void testGetRole() {
        User user = new User();
        user.setRole(Role.user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        String role = userService.getRole("user@example.com");

        assertEquals("user", role);
    }

    @Test
    void testChangeName() {
        User user = new User();
        user.setName("Old Name");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        String newName = userService.changeName("user@example.com", "New Name");

        assertEquals("New Name", newName);
        assertEquals("New Name", user.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserInfo() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserInfoForOrderResponse userInfo = userService.getUserInfo("user@example.com");

        assertNotNull(userInfo);
        assertEquals(1L, userInfo.id());
        assertEquals("John Doe", userInfo.name());
        assertEquals("user@example.com", userInfo.email());
    }

    @Test
    void testConvertToDTO() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("user@example.com");
        user.setRole(Role.admin);

        UserResponse userDTO = userService.convertToDTO(user);

        assertNotNull(userDTO);
        assertEquals(1L, userDTO.id());
        assertEquals("John Doe", userDTO.name());
        assertEquals("user@example.com", userDTO.email());
        assertEquals("admin", userDTO.role());
    }
}
