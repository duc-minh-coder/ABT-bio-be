package com.NMCNPM.ABT_bio.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @Size(max = 36)
//    @Pattern(
//            regexp = ".+@.+",
//            message = "EMAIL_INCORRECT_FORMAT"
//    )
    private String email;

    @Size(max = 36)
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$",
//            message = "PASSWORD_INVALID_FORMAT"
//    )
    private String password;
}
