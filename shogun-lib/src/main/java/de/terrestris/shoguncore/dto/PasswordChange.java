package de.terrestris.shoguncore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordChange {
    @NotBlank(message = "{user.validation.password.empty}")
    private String oldPassword;

    @NotBlank(message = "{user.validation.password.empty}")
    private String newPassword;
}
