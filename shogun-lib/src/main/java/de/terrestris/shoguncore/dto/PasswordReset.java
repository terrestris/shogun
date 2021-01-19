package de.terrestris.shoguncore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordReset {
    @Email(message = "{user.validation.email}")
    private String email;
}
