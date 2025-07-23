package pl.urban.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_securities")
public class UserSecurity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int failedLoginAttempts = 0;

    private LocalDateTime lastFailedLoginAttempt;

    private String twoFactorCode;

    private Long twoFactorCodeExpireTime;
}
