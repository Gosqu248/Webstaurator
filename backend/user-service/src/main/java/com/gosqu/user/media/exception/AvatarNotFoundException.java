package com.gosqu.user.media.exception;

import java.util.UUID;

public class AvatarNotFoundException extends RuntimeException {
    public AvatarNotFoundException(UUID userId) {
        super("User has no avatar: userId=" + userId);
    }
}
