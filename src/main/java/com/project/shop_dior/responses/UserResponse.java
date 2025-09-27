package com.project.shop_dior.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.models.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("fullname")
        private String fullName;
        @JsonProperty("phone_number")
        private String phoneNumber;
        @JsonProperty("address")
        private String address;
        private String email;
        @JsonProperty("is_active")
        private Boolean active;
        @JsonProperty("google_account_id")
        private String googleAccountId;
        @JsonProperty("facebook_account_id")
        private String facebookAccountId;
        @JsonProperty("date_of_birth")
        private Date dateOfBirth;
        @JsonProperty("create_at")
        private LocalDateTime createAt;
        @JsonProperty("update_at")
        private LocalDateTime updateeAt;
        @JsonProperty("role")
        private RoleResponse role;
        public static UserResponse fromUser(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .address(user.getAddress())
                    .email(user.getEmail())
                    .active(user.isActive())
                    .dateOfBirth(user.getDateOfBirth())
                    .createAt(user.getCreateAt())
                    .updateeAt(user.getUpdateAt())
                    .facebookAccountId(user.getFacebookAccountId())
                    .googleAccountId(user.getGoogleAccountId())
                    .role(RoleResponse.fromRole(user.getRole()))
                    .build();
        }
}
