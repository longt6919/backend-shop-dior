package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @JsonProperty("phone_number")
//    @NotBlank(message = "phone number can not blank")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    private String password;
    @Min(value = 1, message = "You must enter role's Id")
    @JsonProperty("role_id")
    private Long roleId;
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    // Google Account Id, not mandatory, can be blank
    @JsonProperty("google_account_id")
    private String googleAccountId;

    //For Google, Facebook login
    // Full name, not mandatory, can be blank
    @JsonProperty("fullname")
    private String fullname;

    // Profile image URL, not mandatory, can be blank
//    @JsonProperty("profile_image")
//    private String profileImage;
    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
    // Kiểm tra facebookAccountId có hợp lệ không
    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    // Kiểm tra googleAccountId có hợp lệ không
    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
