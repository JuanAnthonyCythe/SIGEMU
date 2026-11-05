package pe.edu.unsch.sigemu.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El usuario es obligatorio")
        String usuario,

        @NotBlank(message = "La contraseña es obligatoria")
        String contrasenia
) {
}
