package pe.edu.unsch.sigemu.presentation.dto;

public record AuthResponseDTO(
        String token,
        String tipo,
        String usuario,
        String rol
) {
}
