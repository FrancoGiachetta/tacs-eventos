package tacs.eventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(

        @Email @NotBlank String email,

        // Al menos 8, con letra y número.
        @NotBlank @Size(min = 8, max = 72) @Pattern(regexp = ".*(?=.*[A-Za-z])(?=.*\\d).*", message = "Debe contener letras y números") String password) {
}
