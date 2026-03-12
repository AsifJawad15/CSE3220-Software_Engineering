package com.marketplace.minimarketplace.mapper;
import com.marketplace.minimarketplace.dto.response.AuthResponse;
import com.marketplace.minimarketplace.entity.User;
public class UserMapper {
    private UserMapper() {}
    public static AuthResponse toAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .build();
    }
}
