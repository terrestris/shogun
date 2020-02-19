package de.terrestris.shogun.lib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterUserDto {

    @Email(message = "{user.validation.email}")
    private String email;

    @NotBlank(message = "{user.validation.password.empty}")
    private String password;

    @AssertTrue(message = "{user.validation.acceptTerms.false}")
    @NotNull(message = "{user.validation.acceptTerms.empty}")
    private Boolean acceptTerms;

}
