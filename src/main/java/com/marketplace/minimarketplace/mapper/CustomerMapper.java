package com.marketplace.minimarketplace.mapper;

import com.marketplace.minimarketplace.dto.response.CustomerResponse;
import com.marketplace.minimarketplace.entity.User;
import com.marketplace.minimarketplace.entity.UserProfile;

public class CustomerMapper {

    private CustomerMapper() {}

    public static CustomerResponse toResponse(User user) {
        UserProfile profile = user.getProfile();
        return CustomerResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(profile != null ? profile.getPhone() : null)
                .address(profile != null ? profile.getAddress() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}

