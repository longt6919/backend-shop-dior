package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("phone_number")
    @NotBlank(message = "phone number can not blank")
    private String phoneNumber;
    private String address;
    private String email;
    @NotBlank(message = "Password can not blank")
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;
    @JsonProperty("google_account_id")
    private String googleAccountId;
    @JsonProperty("role_id")
    private Long roleId;
}
